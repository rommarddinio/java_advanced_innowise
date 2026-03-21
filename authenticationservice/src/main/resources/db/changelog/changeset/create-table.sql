CREATE TABLE credentials (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);