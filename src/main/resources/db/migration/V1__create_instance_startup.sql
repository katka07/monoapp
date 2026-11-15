CREATE TABLE instance_startup (
    id          BIGSERIAL PRIMARY KEY,
    hostname    VARCHAR(255)                NOT NULL,
    app_version VARCHAR(100)                NOT NULL,
    git_sha     VARCHAR(100),
    environment VARCHAR(50)                 NOT NULL,
    started_at  TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_instance_startup_started_at
    ON instance_startup (started_at DESC);