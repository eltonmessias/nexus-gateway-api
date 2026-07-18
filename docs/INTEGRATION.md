# Nexus Gateway API — Guia de Integração

## Hierarquia de recursos

Antes de usar a API, é necessário criar os recursos pela ordem correcta:

```
Organization
└── User (com role ORG_OWNER)
    └── Team
        └── Project
            └── ApiClient (para integração M2M)
```

## Passo a passo completo (curl)

### 1. Registar organização + owner

```bash
curl -X POST http://localhost:8080/api/iam/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "organizationName": "Acme Corp",
    "organizationSlug": "acme",
    "ownerName": "João Silva",
    "ownerEmail": "joao@acme.com",
    "ownerPassword": "password123"
  }'
```

Guardar `accessToken` e `organizationId` da resposta.

### 2. Criar team

```bash
curl -X POST http://localhost:8080/api/iam/teams \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Backend Team",
    "organizationId": "$ORG_ID"
  }'
```

### 3. Criar projecto

```bash
curl -X POST http://localhost:8080/api/iam/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Portal Web",
    "teamId": "$TEAM_ID"
  }'
```

### 4. Registar API Client (M2M)

```bash
curl -X POST http://localhost:8080/api/iam/clients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Backend Service",
    "projectId": "$PROJECT_ID",
    "organizationId": "$ORG_ID",
    "rateLimitRpm": 200,
    "rateLimitBurst": 40
  }'
```

Guardar `clientId` e `apiKey` (o `apiKey` não será mostrado novamente).

### 5. Autenticar como API Client

```bash
curl -X POST http://localhost:8080/api/iam/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "nexus_abc123...",
    "apiKey": "nexus_live_..."
  }'
```

Usar o `accessToken` retornado para chamar `/api/*`.

---

## Feature Flags — Padrão de uso recomendado

```typescript
// Verificar flag no frontend antes de renderizar
async function isFeatureEnabled(flagKey: string): Promise<boolean> {
  try {
    const result = await nexus.get(`/api/flags/${flagKey}/production`);
    return result.reason !== "FLAG_DISABLED" && result.reason !== "FLAG_NOT_FOUND";
  } catch {
    return false; // fail-safe: desactivar se a API não responder
  }
}

// Uso num componente React
if (await isFeatureEnabled("new-checkout-flow")) {
  return <NewCheckout />;
}
return <LegacyCheckout />;
```

---

## Job Queue — Padrão de uso recomendado

```typescript
// Submeter job e fazer polling de status
async function submitAndWait(jobRequest: object, maxWaitMs = 30_000) {
  const job = await nexus.post("/api/jobs", jobRequest);
  
  const start = Date.now();
  while (Date.now() - start < maxWaitMs) {
    await new Promise(r => setTimeout(r, 1000));
    const status = await nexus.get(`/api/jobs/${job.jobId}`);
    if (status.status === "COMPLETED") return status;
    if (status.status === "FAILED") throw new Error("Job failed");
  }
  throw new Error("Job timeout");
}

// Exemplo
const result = await submitAndWait({
  type: "generate-report",
  payload: { reportId: "monthly-sales", format: "pdf" },
  priority: 7,
});
```

---

## Search — Indexar e pesquisar

```typescript
// Indexar documento quando um produto é criado
async function indexProduct(product: Product) {
  await nexus.post("/api/search/index", {
    title: product.name,
    content: `${product.name} ${product.description} ${product.category}`,
    indexName: "products",
    metadata: {
      productId: product.id,
      category: product.category,
      price: product.price,
    },
    orgId: currentUser.organizationId,
  });
}

// Pesquisar
async function searchProducts(query: string, page = 0) {
  return nexus.post("/api/search/query", {
    query,
    indexName: "products",
    page,
    size: 20,
  });
}
```

---

## Paginação

Todos os endpoints de listagem devolvem `PagedResult<T>`:

```typescript
interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Componente de paginação genérico
async function fetchPage<T>(endpoint: string, page: number, size = 20): Promise<PagedResult<T>> {
  return nexus.get(`${endpoint}?page=${page}&size=${size}`);
}

// Uso
const { content, totalPages, totalElements } = await fetchPage("/api/iam/users", 0, 20);
```

---

## Rodar apiKey de forma segura

```typescript
// Nunca guardar o apiKey no frontend. Fazer isto via backend ou painel admin.
async function rotateClientKey(clientId: string) {
  const result = await adminClient.patch(`/api/iam/clients/${clientId}/rotate-key`);
  // result.apiKey é o novo valor — mostrar uma vez e guardar em secret manager
  await secretManager.set(`nexus_apikey_${clientId}`, result.apiKey);
  console.log("Novo apiKey guardado. Antigo foi invalidado.");
}
```

---

## CORS (configuração necessária em produção)

Adicionar ao `SecurityConfig.java` antes do deploy:

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://meusite.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

E em `securityFilterChain`:
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```
