# Nexus Gateway API

Backend-as-a-Service enterprise — infraestrutura partilhada para aplicações que precisam de autenticação, gestão de identidades, feature flags, filas de jobs assíncronos e pesquisa full-text.

---

## Índice

- [Visão Geral](#visão-geral)
- [Arquitectura](#arquitectura)
- [Stack Tecnológica](#stack-tecnológica)
- [Módulos](#módulos)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Arranque](#configuração-e-arranque)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Base de Dados e Migrações](#base-de-dados-e-migrações)
- [Referência da API](#referência-da-api)
- [Autenticação](#autenticação)
- [Integração Frontend](#integração-frontend)
- [Integração Backend (M2M)](#integração-backend-m2m)
- [Rate Limiting](#rate-limiting)
- [Erros](#erros)
- [Swagger UI](#swagger-ui)

---

## Visão Geral

O Nexus Gateway expõe uma única aplicação Spring Boot que agrega seis módulos Maven:

| Módulo | Responsabilidade |
|---|---|
| `nexus-iam` | Identidades, autenticação JWT, RBAC, audit log |
| `nexus-feature-flags` | Feature flags por ambiente |
| `nexus-job-queue` | Filas de jobs assíncronos com Kafka |
| `nexus-search-engine` | Pesquisa full-text com PostgreSQL tsvector |
| `nexus-cache-layer` | Cache Redis partilhado entre módulos |
| `nexus-commons` | DTOs, eventos e excepções partilhadas |

---

## Arquitectura

```
nexus-gateway (Spring Boot — porta de entrada)
├── nexus-iam          (Hexagonal: domain → application → infrastructure → presentation)
├── nexus-feature-flags
├── nexus-job-queue    (Kafka producer + consumer)
├── nexus-search-engine (PostgreSQL tsvector)
├── nexus-cache-layer  (Redis)
└── nexus-commons      (DTOs, eventos, excepções)
```

**Padrões aplicados:**
- Hexagonal Architecture (Ports & Adapters) em `nexus-iam`
- Domain-Driven Design puro — domain services são `@Bean`, sem `@Service` no domínio
- JWT stateless com tipos explícitos (`access`, `refresh`, `client_access`, `client_refresh`)
- Redis sliding window para rate limiting de API clients
- Audit log assíncrono (`@Async`) com JSONB no PostgreSQL
- Flyway para migrações de schema

---

## Stack Tecnológica

| Componente | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.2 |
| Spring Security | 7 |
| Hibernate | 7 |
| PostgreSQL | 15+ |
| Redis | 7+ |
| Kafka | 3+ |
| Flyway | 10+ |
| springdoc-openapi | 2.8.8 |

---

## Módulos

### nexus-iam
Gestão completa de identidades e controlo de acesso:
- Registo de organização + owner (fluxo único)
- Login com JWT (access + refresh token)
- Logout com blacklist Redis
- Quatro roles: `ORG_OWNER`, `TEAM_ADMIN`, `TEAM_MEMBER`, `VIEWER`
- API Clients para autenticação M2M (machine-to-machine)
- Rotação de apiKey
- Audit log imutável com JSONB metadata

### nexus-feature-flags
Flags com activação/desactivação por chave e por ambiente (`production`, `staging`, `development`, etc.).

### nexus-job-queue
Submissão de jobs assíncronos com prioridades 1–10. Eventos publicados no Kafka (`job.created`, `job.completed`). Consumers actualizam status: `PENDING → RUNNING → COMPLETED/FAILED`.

### nexus-search-engine
Indexação e pesquisa full-text em PostgreSQL com `tsvector GENERATED ALWAYS AS`. Suporta múltiplos índices (`indexName`), paginação e ranking por relevância (`ts_rank`).

### nexus-cache-layer
Serviço Redis partilhado com operações de get/set/delete/TTL. Usado internamente pelo rate limiter e pelo módulo de jobs.

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (base de dados `nexus`)
- Redis 7+
- Kafka 3+ (com Zookeeper ou KRaft)

**Docker Compose (dev):**
```bash
docker compose up -d
```

---

## Configuração e Arranque

```bash
# 1. Clonar
git clone <repo-url>
cd nexus-gateway-api

# 2. Copiar variáveis de ambiente
cp .env.example .env
# Editar .env com os valores correctos

# 3. Compilar todos os módulos
mvn install -DskipTests

# 4. Arrancar
mvn spring-boot:run -pl nexus-gateway
```

A aplicação fica disponível em `http://localhost:8080`.

---

## Variáveis de Ambiente

Copiar `.env.example` para `.env` e preencher:

| Variável | Default (dev) | Descrição |
|---|---|---|
| `SERVER_PORT` | `8080` | Porta HTTP |
| `SSL_ENABLED` | `false` | Activar HTTPS |
| `SSL_KEYSTORE_PATH` | — | Caminho para o keystore PKCS12 |
| `SSL_KEYSTORE_PASSWORD` | — | Password do keystore |
| `DB_URL` | `jdbc:postgresql://localhost:5432/nexus` | JDBC URL |
| `DB_USERNAME` | `nexus` | Utilizador da base de dados |
| `DB_PASSWORD` | `nexus_dev` | Password da base de dados |
| `REDIS_HOST` | `localhost` | Host Redis |
| `REDIS_PORT` | `6379` | Porta Redis |
| `REDIS_PASSWORD` | — | Password Redis (vazio = sem auth) |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Brokers Kafka |
| `KAFKA_CONSUMER_GROUP` | `nexus-job-queue` | Consumer group |
| `JWT_SECRET` | *(dev key)* | Chave HMAC-SHA256 (mínimo 32 bytes hex) |
| `JWT_EXPIRATION` | `900000` | Expiração do access token (ms) — 15 min |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Expiração do refresh token (ms) — 7 dias |

**Produção:** gerar um JWT_SECRET seguro:
```bash
openssl rand -hex 32
```

---

## Base de Dados e Migrações

O Flyway corre automaticamente no arranque da aplicação. As migrações estão em `nexus-gateway/src/main/resources/db/migration/`:

| Versão | Descrição |
|---|---|
| V1 | Tabelas iniciais (`flags`, `jobs`) |
| V2 | Tabelas IAM (`organizations`, `users`, `teams`, `projects`, `api_clients`) |
| V3 | Coluna `role` em `users` |
| V4 | Tabela `audit_logs` com JSONB |
| V5 | Separação de schemas (`nexus_iam`, `nexus_flags`, `nexus_jobs`) |
| V6 | Tabela `nexus_search.search_documents` com tsvector |
| V7 | Tabela `nexus_iam.revoked_tokens` (auditoria forense) |

**Schemas:**
- `nexus_iam` — identidades, audit logs, tokens revogados
- `nexus_flags` — feature flags
- `nexus_jobs` — filas de jobs
- `nexus_search` — documentos indexados
- `public` — histórico Flyway

---

## Referência da API

Base URL: `http://localhost:8080`

Todos os endpoints que não sejam auth requerem header:
```
Authorization: Bearer <access_token>
```

---

### Autenticação (`/api/iam/auth`)

#### POST `/api/iam/auth/register`
Cria organização + owner num único passo. Retorna tokens de acesso imediatamente.

**Request:**
```json
{
  "organizationName": "Acme Corp",
  "organizationSlug": "acme",
  "organizationDescription": "Empresa de exemplo",
  "ownerName": "João Silva",
  "ownerEmail": "joao@acme.com",
  "ownerPassword": "minimo8chars"
}
```

**Response 201:**
```json
{
  "organizationId": "uuid",
  "organizationName": "Acme Corp",
  "organizationSlug": "acme",
  "ownerId": "uuid",
  "ownerEmail": "joao@acme.com",
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "accessTokenExpiresIn": 900000,
  "refreshTokenExpiresIn": 604800000
}
```

---

#### POST `/api/iam/auth/login`
**Request:**
```json
{
  "email": "joao@acme.com",
  "password": "minimo8chars"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "email": "joao@acme.com",
  "accessTokenExpiresIn": 900000,
  "refreshTokenExpiresIn": 604800000
}
```

---

#### POST `/api/iam/auth/logout`
Revoga o access token actual (adiciona ao blacklist Redis com TTL).

**Headers:** `Authorization: Bearer <access_token>`

**Response 204** — sem body.

---

#### POST `/api/iam/auth/refresh`
Renova o access token de utilizador.

**Request:**
```json
{ "refreshToken": "eyJ..." }
```

**Response 200:** mesmo formato que `/login`.

---

#### POST `/api/iam/auth/token`
Emite tokens para API clients (M2M).

**Request:**
```json
{
  "clientId": "nexus_abc123...",
  "apiKey": "nexus_live_..."
}
```

**Response 200:**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "clientId": "nexus_abc123...",
  "expiresIn": 900000,
  "refreshExpiresIn": 604800000
}
```

---

#### POST `/api/iam/auth/token/refresh`
Renova o access token de API client.

**Request:**
```json
{ "refreshToken": "eyJ..." }
```

---

### Organizações (`/api/iam/organizations`)

Requer role: `ORG_OWNER`, `TEAM_ADMIN`, `TEAM_MEMBER` ou `VIEWER`.

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/iam/organizations` | Criar organização |
| `GET` | `/api/iam/organizations?page=0&size=20` | Listar (paginado) |
| `GET` | `/api/iam/organizations/{id}` | Obter por ID |
| `PUT` | `/api/iam/organizations/{id}` | Actualizar |
| `DELETE` | `/api/iam/organizations/{id}` | Eliminar |

**Request (POST/PUT):**
```json
{
  "name": "Acme Corp",
  "slug": "acme",
  "description": "Descrição opcional"
}
```

**Response:**
```json
{
  "id": "uuid",
  "name": "Acme Corp",
  "slug": "acme",
  "description": "...",
  "active": true,
  "createdAt": "2026-01-01T00:00:00Z",
  "updatedAt": "2026-01-01T00:00:00Z"
}
```

**Resposta paginada (GET lista):**
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

---

### Utilizadores (`/api/iam/users`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/iam/users` | Criar utilizador |
| `GET` | `/api/iam/users?page=0&size=20` | Listar (paginado) |
| `GET` | `/api/iam/users/{id}` | Obter por ID |
| `PUT` | `/api/iam/users/{id}` | Actualizar |
| `DELETE` | `/api/iam/users/{id}` | Eliminar |

**Request (POST/PUT):**
```json
{
  "name": "Ana Costa",
  "email": "ana@acme.com",
  "password": "minimo8chars",
  "organizationId": "uuid",
  "role": "TEAM_MEMBER"
}
```

Roles disponíveis: `ORG_OWNER`, `TEAM_ADMIN`, `TEAM_MEMBER`, `VIEWER`.

---

### Teams (`/api/iam/teams`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/iam/teams` | Criar team |
| `GET` | `/api/iam/teams?page=0&size=20` | Listar (paginado) |
| `GET` | `/api/iam/teams/{id}` | Obter por ID |
| `PUT` | `/api/iam/teams/{id}` | Actualizar |
| `DELETE` | `/api/iam/teams/{id}` | Eliminar |

**Request:**
```json
{
  "name": "Backend Team",
  "description": "Equipa de backend",
  "organizationId": "uuid"
}
```

---

### Projectos (`/api/iam/projects`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/iam/projects` | Criar projecto |
| `GET` | `/api/iam/projects?page=0&size=20` | Listar (paginado) |
| `GET` | `/api/iam/projects/{id}` | Obter por ID |
| `PUT` | `/api/iam/projects/{id}` | Actualizar |
| `DELETE` | `/api/iam/projects/{id}` | Eliminar |

**Request:**
```json
{
  "name": "Portal Web",
  "description": "Portal público",
  "teamId": "uuid"
}
```

---

### API Clients (`/api/iam/clients`)

Utilizados para integração M2M (backend ↔ backend). O `apiKey` é mostrado **apenas uma vez** na criação ou rotação.

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/iam/clients` | Registar client |
| `GET` | `/api/iam/clients/{id}` | Obter por ID |
| `GET` | `/api/iam/clients/organization/{orgId}` | Listar por organização |
| `GET` | `/api/iam/clients/project/{projectId}` | Listar por projecto |
| `DELETE` | `/api/iam/clients/{id}` | Eliminar |
| `PATCH` | `/api/iam/clients/{id}/deactivate` | Desactivar |
| `POST` | `/api/iam/clients/{id}/rotate-key` | Rodar apiKey |

**Request (POST):**
```json
{
  "name": "Mobile App Client",
  "projectId": "uuid",
  "organizationId": "uuid",
  "rateLimitRpm": 200,
  "rateLimitBurst": 40
}
```

**Response 201 (apiKey mostrado só aqui):**
```json
{
  "id": "uuid",
  "name": "Mobile App Client",
  "projectId": "uuid",
  "organizationId": "uuid",
  "clientId": "nexus_abc123...",
  "apiKey": "nexus_live_XXXXXXXXXXX",
  "rateLimitRpm": 200,
  "rateLimitBurst": 40
}
```

**Response rotate-key 200:**
```json
{
  "clientId": "uuid",
  "apiKey": "nexus_live_YYYYYYYYYY",
  "message": "API key rotated successfully. Store it securely — it will not be shown again."
}
```

---

### Audit Log (`/api/iam/audit`)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/iam/audit/organization/{orgId}?page=0&size=20` | Logs por organização |
| `GET` | `/api/iam/audit/actor/{actorId}?page=0&size=20` | Logs por actor |

**Response:**
```json
{
  "content": [
    {
      "id": "uuid",
      "action": "USER_LOGIN",
      "actorId": "uuid-do-utilizador",
      "actorType": "USER",
      "organizationId": "uuid",
      "resourceType": "USER",
      "resourceId": "uuid",
      "metadata": { "email": "joao@acme.com" },
      "ipAddress": "192.168.1.1",
      "createdAt": "2026-01-01T10:00:00Z"
    }
  ],
  "totalElements": 100,
  "totalPages": 5
}
```

Acções auditadas: `USER_REGISTERED`, `USER_LOGIN`, `USER_LOGIN_FAILED`, `API_CLIENT_TOKEN_ISSUED`.

---

### Feature Flags (`/api/flags`)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/flags/{flagKey}/{environment}` | Avaliar flag |
| `GET` | `/api/flags` | Listar todas |
| `GET` | `/api/flags/{id}` | Obter por ID |
| `POST` | `/api/flags` | Criar flag |
| `PUT` | `/api/flags/{id}` | Actualizar flag |
| `DELETE` | `/api/flags/{id}` | Eliminar |

**Request (POST/PUT):**
```json
{
  "flagKey": "new-checkout-flow",
  "value": "v2",
  "environment": "production",
  "enabled": true
}
```

**Response evaluate:**
```json
{
  "flagKey": "new-checkout-flow",
  "value": "v2",
  "reason": "FLAG_ENABLED"
}
```

Quando a flag não existe ou está desactivada, `reason` é `FLAG_DISABLED` ou `FLAG_NOT_FOUND` e `value` é `"default"`.

---

### Job Queue (`/api/jobs`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/jobs` | Submeter job |
| `GET` | `/api/jobs` | Listar todos |
| `GET` | `/api/jobs/{id}` | Obter por ID |
| `DELETE` | `/api/jobs/{id}` | Cancelar (status → FAILED) |

**Request:**
```json
{
  "type": "send-email",
  "payload": {
    "to": "user@example.com",
    "template": "welcome",
    "data": { "name": "João" }
  },
  "priority": 5
}
```

Prioridade: 1 (baixa) a 10 (máxima).

**Response:**
```json
{
  "jobId": "uuid",
  "status": "PENDING",
  "createdAt": "2026-01-01T10:00:00Z"
}
```

**Ciclo de vida:** `PENDING → RUNNING → COMPLETED / FAILED`

O evento `job.created` é publicado no Kafka imediatamente após a submissão. O consumer `JobEventConsumer` processa o evento e actualiza o status para `RUNNING`.

---

### Search (`/api/search`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/search/query` | Pesquisar documentos |
| `POST` | `/api/search/index` | Indexar documento |
| `DELETE` | `/api/search/{id}` | Remover documento |

**Request query:**
```json
{
  "query": "pagamento mobile",
  "indexName": "products",
  "page": 0,
  "size": 10
}
```

**Response:**
```json
{
  "documents": [
    {
      "id": "uuid",
      "title": "Pagamento por M-Pesa",
      "content": "...",
      "indexName": "products",
      "metadata": { "category": "payments" },
      "orgId": "uuid"
    }
  ],
  "totalResults": 3,
  "page": 0,
  "size": 10
}
```

**Request index:**
```json
{
  "title": "Pagamento por M-Pesa",
  "content": "Guia completo de integração M-Pesa para lojas online em Moçambique.",
  "indexName": "products",
  "metadata": { "category": "payments", "tags": ["mpesa", "mobile"] },
  "orgId": "uuid"
}
```

---

### Utilitários

#### GET `/api/ping`
Health check rápido sem autenticação.

**Response:**
```json
{
  "status": "ok",
  "timestamp": "2026-01-01T10:00:00Z"
}
```

---

## Autenticação

### Fluxo de utilizador humano

```
1. POST /api/iam/auth/register   →  accessToken + refreshToken
2. Usar accessToken nas requests  (válido 15 min)
3. POST /api/iam/auth/refresh     →  novo accessToken (antes de expirar)
4. POST /api/iam/auth/logout      →  revoga o token actual
```

### Fluxo M2M (API Client)

```
1. Criar API Client:  POST /api/iam/clients  →  clientId + apiKey (guardar)
2. Obter token:       POST /api/iam/auth/token  →  client_access + client_refresh
3. Usar client_access nas requests para /api/*
4. Renovar:          POST /api/iam/auth/token/refresh
5. Rodar apiKey:     POST /api/iam/clients/{id}/rotate-key  (substitui o apiKey)
```

### Tipos de token JWT

| Tipo | Claim `type` | Utilização |
|---|---|---|
| Access (user) | `access` | Autenticar utilizadores em `/api/iam/**` |
| Refresh (user) | `refresh` | Renovar access token em `/api/iam/auth/refresh` |
| Client Access | `client_access` | Autenticar API clients em `/api/**` |
| Client Refresh | `client_refresh` | Renovar client token em `/api/iam/auth/token/refresh` |

**Nota:** tokens de tipo `refresh` e `client_refresh` são **rejeitados** em qualquer endpoint que não seja o de renovação.

---

## Integração Frontend

### Setup inicial (JavaScript / TypeScript)

```typescript
const BASE_URL = "http://localhost:8080";

async function login(email: string, password: string) {
  const res = await fetch(`${BASE_URL}/api/iam/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) throw await res.json();
  const data = await res.json();
  localStorage.setItem("accessToken", data.accessToken);
  localStorage.setItem("refreshToken", data.refreshToken);
  localStorage.setItem("tokenExpiry", String(Date.now() + data.accessTokenExpiresIn));
  return data;
}
```

### Cliente HTTP com renovação automática de token

```typescript
class NexusClient {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  private getToken(): string | null {
    return localStorage.getItem("accessToken");
  }

  private async refreshToken(): Promise<boolean> {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) return false;

    try {
      const res = await fetch(`${this.baseUrl}/api/iam/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
      });
      if (!res.ok) return false;
      const data = await res.json();
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);
      localStorage.setItem("tokenExpiry", String(Date.now() + data.accessTokenExpiresIn));
      return true;
    } catch {
      return false;
    }
  }

  private isExpired(): boolean {
    const expiry = localStorage.getItem("tokenExpiry");
    if (!expiry) return true;
    // Renovar 60 segundos antes de expirar
    return Date.now() > Number(expiry) - 60_000;
  }

  async request<T>(path: string, options: RequestInit = {}): Promise<T> {
    if (this.isExpired()) {
      const ok = await this.refreshToken();
      if (!ok) {
        localStorage.clear();
        window.location.href = "/login";
        throw new Error("Session expired");
      }
    }

    const res = await fetch(`${this.baseUrl}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${this.getToken()}`,
        ...options.headers,
      },
    });

    if (res.status === 401) {
      localStorage.clear();
      window.location.href = "/login";
      throw new Error("Unauthorized");
    }

    if (!res.ok) {
      const error = await res.json();
      throw error;
    }

    if (res.status === 204) return undefined as T;
    return res.json();
  }

  // Atalhos
  get<T>(path: string) { return this.request<T>(path); }
  post<T>(path: string, body: unknown) {
    return this.request<T>(path, { method: "POST", body: JSON.stringify(body) });
  }
  put<T>(path: string, body: unknown) {
    return this.request<T>(path, { method: "PUT", body: JSON.stringify(body) });
  }
  delete<T>(path: string) {
    return this.request<T>(path, { method: "DELETE" });
  }
  patch<T>(path: string, body?: unknown) {
    return this.request<T>(path, { method: "PATCH", body: body ? JSON.stringify(body) : undefined });
  }

  async logout() {
    await this.request("/api/iam/auth/logout", { method: "POST" });
    localStorage.clear();
  }
}

export const nexus = new NexusClient("http://localhost:8080");
```

### Exemplos de uso

```typescript
// Registo
const session = await nexus.post("/api/iam/auth/register", {
  organizationName: "Acme",
  organizationSlug: "acme",
  ownerName: "João",
  ownerEmail: "joao@acme.com",
  ownerPassword: "password123",
});

// Listar organizações (paginado)
const orgs = await nexus.get("/api/iam/organizations?page=0&size=10");
// orgs.content, orgs.totalPages, orgs.totalElements

// Criar utilizador
const user = await nexus.post("/api/iam/users", {
  name: "Ana",
  email: "ana@acme.com",
  password: "password123",
  organizationId: "uuid-da-org",
  role: "TEAM_MEMBER",
});

// Avaliar feature flag
const flag = await nexus.get("/api/flags/new-checkout-flow/production");
if (flag.value === "v2" && flag.reason !== "FLAG_DISABLED") {
  // mostrar nova versão
}

// Pesquisar
const results = await nexus.post("/api/search/query", {
  query: "produto mobile",
  indexName: "products",
  page: 0,
  size: 10,
});

// Submeter job
const job = await nexus.post("/api/jobs", {
  type: "send-email",
  payload: { to: "user@example.com", template: "welcome" },
  priority: 3,
});

// Logout
await nexus.logout();
```

### Tratamento de erros

Todos os erros têm o mesmo formato:

```typescript
interface NexusError {
  status: number;
  code: string;
  message: string;
  timestamp: string;
}

// Exemplo de tratamento
try {
  await nexus.post("/api/iam/auth/login", { email, password });
} catch (err) {
  const error = err as NexusError;
  switch (error.code) {
    case "INVALID_CREDENTIALS":
      showToast("Email ou password incorrectos");
      break;
    case "TOKEN_EXPIRED":
      // renovação automática já foi tentada — redirecionar para login
      router.push("/login");
      break;
    case "VALIDATION_ERROR":
      showToast(error.message); // "name: must not be blank; email: ..."
      break;
    default:
      showToast("Erro inesperado");
  }
}
```

**Códigos de erro comuns:**

| Código | HTTP | Descrição |
|---|---|---|
| `INVALID_CREDENTIALS` | 401 | Email ou password incorrectos |
| `TOKEN_EXPIRED` | 401 | JWT expirado |
| `INVALID_TOKEN` | 401 | JWT malformado ou assinatura inválida |
| `TOKEN_REVOKED` | 401 | Token foi revogado (logout) |
| `ACCESS_DENIED` | 403 | Role insuficiente |
| `ACCOUNT_DISABLED` | 403 | Conta desactivada |
| `NOT_FOUND` | 404 | Recurso não encontrado |
| `EMAIL_ALREADY_EXISTS` | 409 | Email já registado |
| `SLUG_ALREADY_EXISTS` | 409 | Slug já em uso |
| `RATE_LIMIT_EXCEEDED` | 429 | Limite de requests atingido |
| `VALIDATION_ERROR` | 400 | Campos inválidos |
| `INTERNAL_ERROR` | 500 | Erro interno |

---

## Integração Backend (M2M)

Para serviços backend que precisam de chamar a API de forma autónoma (sem utilizador humano):

```typescript
// Node.js / TypeScript

class NexusM2MClient {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private expiresAt: number = 0;

  constructor(
    private baseUrl: string,
    private clientId: string,
    private apiKey: string
  ) {}

  private async authenticate() {
    const res = await fetch(`${this.baseUrl}/api/iam/auth/token`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ clientId: this.clientId, apiKey: this.apiKey }),
    });
    const data = await res.json();
    this.accessToken = data.accessToken;
    this.refreshToken = data.refreshToken;
    this.expiresAt = Date.now() + data.expiresIn - 60_000;
  }

  private async ensureToken() {
    if (!this.accessToken || Date.now() >= this.expiresAt) {
      if (this.refreshToken) {
        try {
          const res = await fetch(`${this.baseUrl}/api/iam/auth/token/refresh`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken: this.refreshToken }),
          });
          if (res.ok) {
            const data = await res.json();
            this.accessToken = data.accessToken;
            this.refreshToken = data.refreshToken;
            this.expiresAt = Date.now() + data.expiresIn - 60_000;
            return;
          }
        } catch {}
      }
      await this.authenticate();
    }
  }

  async request<T>(path: string, options: RequestInit = {}): Promise<T> {
    await this.ensureToken();
    const res = await fetch(`${this.baseUrl}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${this.accessToken}`,
        ...options.headers,
      },
    });
    if (!res.ok) throw await res.json();
    if (res.status === 204) return undefined as T;
    return res.json();
  }
}

// Uso
const client = new NexusM2MClient(
  "http://localhost:8080",
  "nexus_abc123...",
  "nexus_live_..."
);

// O token é obtido/renovado automaticamente
const jobs = await client.request("/api/jobs");
```

---

## Rate Limiting

API Clients (tokens `client_access`) têm rate limiting por sliding window em Redis.

Os limites são configurados por client na criação:
- `rateLimitRpm` — requests por minuto (default: 100)
- `rateLimitBurst` — burst máximo (default: 20)

Headers de resposta:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
Retry-After: 1500   (só presente quando 429)
```

Quando o limite é atingido, a resposta é `429 Too Many Requests`:
```json
{
  "status": 429,
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit exceeded",
  "timestamp": "2026-01-01T10:00:00Z"
}
```

Utilizadores humanos (tokens `access`) **não** têm rate limiting aplicado.

---

## Swagger UI

Disponível em desenvolvimento:

```
http://localhost:8080/swagger-ui.html
```

Para autenticar no Swagger UI:
1. Fazer login via `POST /api/iam/auth/login`
2. Copiar o `accessToken`
3. Clicar em **Authorize** (canto superior direito)
4. Introduzir `Bearer <token>`

O JSON OpenAPI está disponível em: `http://localhost:8080/api-docs`
