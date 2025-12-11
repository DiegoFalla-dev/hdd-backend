-- Add is_valid column to users table
-- This column indicates if the user account has been validated by an administrator
-- Default value is FALSE for new accounts

ALTER TABLE users
ADD COLUMN is_valid BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing users to be valid by default (optional - adjust as needed)
UPDATE users SET is_valid = TRUE WHERE id > 0;
