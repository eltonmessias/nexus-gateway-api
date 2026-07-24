# Deploy on a Hetzner Cloud VPS

Step-by-step for the backend stack (Spring Boot + Postgres + Redis + Kafka +
Caddy) on one Hetzner Cloud server, with the frontend on Vercel.

Prerequisites: the backend is pushed to a GitHub repo, and you own a domain name.

---

## A. Create the server (Hetzner Cloud Console)

1. https://console.hetzner.cloud → **+ Add Server**
2. **Location:** Falkenstein or Nuremberg (Germany — closest to PT/MZ).
3. **Image:** Ubuntu 24.04
4. **Type:** Shared vCPU → **CX22** (2 vCPU / 4 GB). (8 GB tier if you want OOM headroom.)
5. **SSH key:** add your public key (`cat ~/.ssh/id_ed25519.pub`). If you have none:
   `ssh-keygen -t ed25519` first. (Password login also works but SSH keys are safer.)
6. **Create & Buy now.** Note the server's **public IPv4**.

---

## B. Connect and prepare the server

```bash
ssh root@YOUR_SERVER_IP
```

Then, on the server:

```bash
# Update
apt update && apt upgrade -y

# Firewall: allow SSH + HTTP + HTTPS only
apt install -y ufw
ufw allow OpenSSH
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable

# Docker + Compose plugin (official)
curl -fsSL https://get.docker.com | sh
docker --version && docker compose version

# Git
apt install -y git
```

---

## C. Point your domain at the server (DNS)

At your domain registrar (Namecheap, Cloudflare, …) add an **A record**:

| Type | Name | Value |
|------|------|-------|
| A | `api` | YOUR_SERVER_IP |

So `api.yourdomain.com` resolves to the VPS. (If using Cloudflare, set the proxy
to **DNS only / grey cloud** so Caddy can obtain the certificate.)

Verify it resolves before continuing:

```bash
dig +short api.yourdomain.com
```

---

## D. Deploy the backend

```bash
git clone -b dev https://github.com/eltonmessias/nexus-gateway-api.git
cd nexus-gateway-api

cp .env.production.example .env
nano .env
```

Fill `.env`:

```env
API_DOMAIN=api.yourdomain.com
DB_NAME=nexus
DB_USERNAME=nexus
DB_PASSWORD=          # paste output of:  openssl rand -base64 24
REDIS_PASSWORD=
JWT_SECRET=           # paste output of:  openssl rand -hex 64
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app   # set after Vercel deploy
```

Generate the secrets:

```bash
openssl rand -hex 64      # → JWT_SECRET
openssl rand -base64 24   # → DB_PASSWORD
```

Bring the stack up (first run compiles the Maven project inside Docker — a few minutes):

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
docker compose -f docker-compose.prod.yml logs -f app
```

---

## E. Verify and create the platform admin

```bash
curl https://api.yourdomain.com/actuator/health          # → {"status":"UP"}
```

Open `https://api.yourdomain.com/swagger-ui.html` in a browser.

Create the super-admin (once):

```bash
curl -X POST https://api.yourdomain.com/api/iam/auth/setup \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@yourdomain.com","password":"a-strong-password"}'
```

---

## F. Frontend on Vercel, then wire CORS

1. Vercel → Import the `nexus-frontend` repo, **root = `apps/dashboard`**.
2. Env var: `NEXT_PUBLIC_API_URL = https://api.yourdomain.com`
3. Deploy → note the URL, e.g. `https://nexus-xyz.vercel.app`.
4. Back on the VPS, put that URL in `.env` → `CORS_ALLOWED_ORIGINS`, then reload:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

---

## Operating it

```bash
# Status / logs
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f app

# Update after a git push
git pull
docker compose -f docker-compose.prod.yml --env-file .env up -d --build

# Stop (keeps data) / start
docker compose -f docker-compose.prod.yml down
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

> Deleting the **server** in Hetzner deletes its disk (and your data). Stopping
> the containers with `down` keeps the named volumes. Back up Postgres with
> `docker compose exec postgres pg_dump -U nexus nexus > backup.sql` if it matters.

If the app gets OOM-killed on 4 GB, resize the server to 8 GB in the Hetzner
console (a couple of minutes, no data loss), or drop Kafka/Zookeeper from the
compose if you don't need the job queue for the demo.
