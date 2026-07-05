CREATE TABLE public.pending_file (
    ulid        VARCHAR(26) PRIMARY KEY,
    file_key    VARCHAR(255) NOT NULL,
    domain      VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP NOT NULL
);

ALTER TABLE public.pending_file ADD CONSTRAINT "UK_PENDING_TABLE_FILE_KEY" UNIQUE (file_key);
CREATE INDEX "IDX_PENDING_TABLE_CREATED_AT" ON public.pending_file (created_at);