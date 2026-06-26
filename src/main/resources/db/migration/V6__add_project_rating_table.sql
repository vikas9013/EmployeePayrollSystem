CREATE TABLE project_ratings (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    feedback TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
