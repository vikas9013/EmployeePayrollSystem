-- V1__init_schema.sql
-- Place this file at: src/main/resources/db/migration/V1__init_schema.sql
-- Flyway runs this automatically on startup instead of ddl-auto=update

CREATE TABLE IF NOT EXISTS employees (
                                         id          BIGSERIAL PRIMARY KEY,
                                         name        VARCHAR(255) NOT NULL,
    designation VARCHAR(255) NOT NULL,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS fulltime_employees (
                                                  id             BIGINT PRIMARY KEY REFERENCES employees(id),
    monthly_salary DOUBLE PRECISION NOT NULL
    );

CREATE TABLE IF NOT EXISTS parttime_employees (
                                                  id           BIGINT PRIMARY KEY REFERENCES employees(id),
    hours_worked INTEGER          NOT NULL,
    hourly_rate  DOUBLE PRECISION NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
                                     id       BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL
    );

CREATE TABLE IF NOT EXISTS audit_log (
                                         id          BIGSERIAL PRIMARY KEY,
                                         entity_type VARCHAR(100),
    entity_id   BIGINT,
    action      VARCHAR(50),
    changed_by  VARCHAR(100),
    changed_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    old_value   TEXT,
    new_value   TEXT
    );