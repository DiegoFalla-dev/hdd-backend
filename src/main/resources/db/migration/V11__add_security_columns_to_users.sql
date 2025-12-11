-- Add new security columns to users table
-- is_active: indicates if the account is active
-- is_two_factor_enabled: indicates if two-factor authentication is enabled
-- activation_token_expiry: expiration date/time of the activation token

ALTER TABLE users
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users
ADD COLUMN is_two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE users
ADD COLUMN activation_token_expiry DATETIME NULL;
