-- V14: Add phone_number column to users table (unencrypted)
ALTER TABLE users 
ADD COLUMN phone_number VARCHAR(20) AFTER phone_encrypted;

CREATE INDEX idx_phone_number ON users(phone_number);
