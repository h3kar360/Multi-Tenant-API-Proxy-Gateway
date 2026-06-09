CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS proxy_key (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    proxy_secret_token TEXT NOT NULL,
    available_tokens INT NOT NULL,
    last_refill_time TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS external_api_key (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    api_name VARCHAR(255) NOT NULL,
    target_base_url VARCHAR(255) NOT NULL,
    encrypted_api_key TEXT NOT NULL,
    proxy_key_id UUID NOT NULL,
    CONSTRAINT fk_proxy_key FOREIGN KEY (proxy_key_id) REFERENCES proxy_key(id) ON DELETE CASCADE
);