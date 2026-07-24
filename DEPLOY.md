# Deploying Nexus Gateway

Two pieces: the **backend** (Spring Boot + Postgres + Redis + Kafka) and the
**frontend** (Next.js). Recommended setup — best cost/robustness for this stack:

- **Backend** → one small Linux VM (≈2 GB RAM) running `docker-compose.prod.yml`,
  fronted by Caddy for automatic HTTPS.
- **Frontend** → Vercel (free), pointed at the backend's HTTPS URL.

---

## 0. Prerequisites (do these first)

1. **The backend must compile and its tests pass.** Deploying broken code is pointless:
   ```bash
   mvn -pl nexus-gateway -am clean package
   ```
   Fix anything that fails before continuing.
2. A **domain name** you control (e.g. `api.yourdomain.com`) with a DNS **A record**
   pointing to the VM's IP. Caddy needs it to issue a TLS certificate.
3. A **VM** with Docker + the Compose plugin installed (DigitalOcean, Hetzner, Linode…).
4. Make sure `.env` is git-ignored:
   ```bash
   echo ".env" >> .gitignore
   ```

---

## 1. Backend (VM)

```bash
# on the VM, in the repo root
cp .env.production.example .env
# edit .env — set API_DOMAIN, DB_PASSWORD, JWT_SECRET, CORS_ALLOWED_ORIGINS
#   openssl rand -hex 64      → JWT_SECRET
#   openssl rand -base64 24   → DB_PASSWORD

docker compose -f docker-compose.prod.yml --env-file .env up -d --build

# watch it come up
docker compose -f docker-compose.prod.yml logs -f app
```

Verify: `https://api.yourdomain.com/actuator/health` returns `{"status":"UP"}`,
and `https://api.yourdomain.com/swagger-ui.html` loads.

> First `--build` compiles the whole Maven reactor in Docker — it takes a few minutes.

---

## 2. Frontend (Vercel)

1. Import the `nexus-frontend` repo in Vercel; set the project root to
   `apps/dashboard` (it's a Turborepo).
2. Set the environment variable:
   ```
   NEXT_PUBLIC_API_URL = https://api.yourdomain.com
   ```
3. Deploy. Vercel gives you `https://your-frontend.vercel.app`.
4. **Back on the VM**, put that URL in `.env` → `CORS_ALLOWED_ORIGINS`, then:
   ```bash
   docker compose -f docker-compose.prod.yml --env-file .env up -d
   ```
   (The frontend and backend are on different domains, so the backend must allow
   the Vercel origin via CORS, and the browser needs HTTPS on both — hence Caddy.)

---

## 3. First run — create the platform admin

The system starts with no users. Create the super-admin once:

```bash
curl -X POST https://api.yourdomain.com/api/iam/auth/setup \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@yourdomain.com","password":"a-strong-password"}'
```

Then open the frontend, log in, and register a company via `/register` to exercise
the org portal.

---

## Security checklist (before sharing the link)

- [ ] `JWT_SECRET` replaced (the default in `application.yml` is public — anyone can forge tokens with it).
- [ ] `DB_PASSWORD` is strong and unique.
- [ ] `.env` is **not** committed.
- [ ] `CORS_ALLOWED_ORIGINS` lists only your real frontend origin(s).
- [ ] HTTPS works end to end (Caddy cert issued; no mixed-content errors in the browser console).
- [ ] Postgres/Redis/Kafka ports are **not** published to the internet (this compose only exposes Caddy's 80/443 — keep it that way).

---

## Alternative: managed platforms

If you'd rather not run a VM: Railway/Render can host the backend container and
give you a managed Postgres + Redis and an HTTPS URL out of the box. Kafka is the
sticky part — most managed tiers don't include it, so you'd use a hosted Kafka
(Confluent Cloud / Upstash Kafka) and point `KAFKA_BOOTSTRAP_SERVERS` at it.
The Dockerfile and env vars here work unchanged; you just drop the `postgres`,
`redis`, `kafka`, `zookeeper`, and `caddy` services and wire the managed URLs.
