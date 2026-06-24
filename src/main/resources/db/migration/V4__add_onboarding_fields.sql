ALTER TABLE employees ADD COLUMN work_email VARCHAR(255);
ALTER TABLE employees ADD COLUMN slack_invite_sent BOOLEAN DEFAULT FALSE;
ALTER TABLE employees ADD COLUMN training_assigned BOOLEAN DEFAULT FALSE;
ALTER TABLE employees ADD COLUMN payroll_configured BOOLEAN DEFAULT FALSE;
ALTER TABLE employees ADD COLUMN ai_onboarding_message TEXT;
