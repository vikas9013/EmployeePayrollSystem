-- V5__link_user_to_employee.sql
-- Add employee_id column to users table to link a login account to an employee record
ALTER TABLE users ADD COLUMN employee_id BIGINT UNIQUE REFERENCES employees(id);
