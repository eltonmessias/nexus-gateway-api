# Nexus Gateway API — Arquitectura

## Estrutura do Monorepo

```
nexus-gateway-api/
├── nexus-commons/          # DTOs, eventos Kafka, excepções partilhadas
├── nexus-cache-layer/      # Serviço Redis (get/set/delete/TTL)
├── nexus-feature-flags/    # Feature flags por ambiente
├── nexus-job-queue/        # Jobs assíncronos + Kafka producer/consumer
├── nexus-search-engine/    # Pesquisa full-text PostgreSQL tsvector
├── nexus-iam/              # Identidades, JWT, RBAC, audit log
└── nexus-gateway/          # Spring Boot main app + Flyway + configs globais
```

## nexus-iam — Hexagonal Architecture

```
domain/
├── model/          Organization, User, Team, Project, ApiClient, Role, ...
├── service/        OrganizationService, UserService, ...  (@Bean, sem @Service)
├── port/
│   ├── in/         OrganizationUseCase, UserUseCase, ...  (interfaces)
│   └── out/        OrganizationRepository, UserRepository, ...  (interfaces)
└── exception/      OrganizationNotFoundException, ...

application/
└── usecase/        OrganizationUseCaseImpl, RegisterUseCaseImpl, ...

infrastructure/
├── persistence/
│   ├── entity/     JPA entities com @Table(schema = "nexus_iam")
│   ├── repository/ JpaRepository interfaces
│   ├── adapter/    OrganizationRepositoryAdapter (implementa port out)
│   └── mapper/     MapStruct mappers (entity ↔ domain)
├── security/       JwtService, JwtAuthenticationFilter, TokenBlacklistService
├── audit/          AuditLogService (@Async)
└── mapper/         Mappers de apresentação (domain ↔ DTO response)

presentation/
├── controller/     REST controllers
└── dto/
    ├── request/    Records com validações Jakarta
    └── response/   Records imutáveis
```

## Fluxo de um request autenticado

```
Request HTTP
    ↓
JwtAuthenticationFilter
    ├── Token revogado? (Redis blacklist) → 401
    ├── Tipo "refresh"? → 401
    ├── Tipo "client_access" → autenticar como ApiClient
    └── Tipo "access"      → autenticar como User (UserDetailsService)
    ↓
RateLimitFilter (só client_access)
    ├── Sliding window Redis
    └── Over limit? → 429
    ↓
SecurityFilterChain (roles)
    ↓
Controller → UseCase → DomainService → Repository (port out)
                                           ↓
                                       Adapter → JpaRepository
    ↓
AuditLogService.log() [@Async, não bloqueia]
    ↓
Response
```

## Schemas PostgreSQL

| Schema | Tabelas |
|---|---|
| `nexus_iam` | `organizations`, `users`, `teams`, `projects`, `api_clients`, `audit_logs`, `revoked_tokens` |
| `nexus_flags` | `flags` |
| `nexus_jobs` | `jobs` |
| `nexus_search` | `search_documents` |
| `public` | `flyway_schema_history` |

## Eventos Kafka

| Tópico | Publicado por | Consumido por | Payload |
|---|---|---|---|
| `job.created` | `JobEventProducer` | `JobEventConsumer` | `JobCreatedEvent(jobId, type, priority, createdAt)` |
| `job.completed` | `JobEventProducer` | `JobEventConsumer` | `JobCompletedEvent(jobId, status, errorMessage, completedAt)` |

## JWT — Claims

**access token (utilizador):**
```json
{
  "sub": "joao@acme.com",
  "userId": "uuid",
  "organizationId": "uuid",
  "role": "ORG_OWNER",
  "type": "access",
  "iat": 1700000000,
  "exp": 1700000900
}
```

**client_access token:**
```json
{
  "sub": "nexus_abc123",
  "clientId": "uuid",
  "organizationId": "uuid",
  "projectId": "uuid",
  "type": "client_access",
  "iat": 1700000000,
  "exp": 1700000900
}
```

## Config de produção (checklist)

- [ ] `JWT_SECRET` — chave aleatória de 32+ bytes (`openssl rand -hex 32`)
- [ ] `SSL_ENABLED=true` + keystore PKCS12
- [ ] `DB_PASSWORD` — password forte, não o default
- [ ] `REDIS_PASSWORD` — activar auth no Redis
- [ ] `logging.level.com.nexus: WARN` (remover DEBUG)
- [ ] Flyway `validateOnMigrate: true` após estabilização dos checksums
- [ ] Configurar CORS no `SecurityConfig` para o domínio do frontend
