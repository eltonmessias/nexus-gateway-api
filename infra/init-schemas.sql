-- Executado automaticamente pelo PostgreSQL no primeiro arranque
CREATE SCHEMA IF NOT EXISTS nexus_gateway;
CREATE SCHEMA IF NOT EXISTS nexus_iam;
CREATE SCHEMA IF NOT EXISTS nexus_flags;
CREATE SCHEMA IF NOT EXISTS nexus_jobs;
CREATE SCHEMA IF NOT EXISTS nexus_search;

-- Extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";   -- busca fuzzy
CREATE EXTENSION IF NOT EXISTS "unaccent";  -- busca sem acentos