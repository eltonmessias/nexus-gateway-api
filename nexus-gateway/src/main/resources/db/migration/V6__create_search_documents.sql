CREATE SCHEMA IF NOT EXISTS nexus_search;

CREATE TABLE nexus_search.search_documents (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       TEXT NOT NULL,
    content     TEXT NOT NULL,
    index_name  VARCHAR(255) NOT NULL,
    metadata    JSONB,
    org_id      UUID,
    search_vector TSVECTOR GENERATED ALWAYS AS (
        to_tsvector('english', coalesce(title, '') || ' ' || coalesce(content, ''))
    ) STORED,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_search_documents_vector ON nexus_search.search_documents USING GIN(search_vector);
CREATE INDEX idx_search_documents_index_name ON nexus_search.search_documents(index_name);
CREATE INDEX idx_search_documents_org_id ON nexus_search.search_documents(org_id);
