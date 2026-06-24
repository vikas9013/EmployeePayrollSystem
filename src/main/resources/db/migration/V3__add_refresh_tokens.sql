CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_refresh_tokens_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
