-- V13__add_ruc_razon_social_to_users.sql
-- Agregar campos para facturación (RUC y Razón Social)

ALTER TABLE users
ADD COLUMN ruc VARCHAR(20) NULL,
ADD COLUMN razon_social VARCHAR(255) NULL;

CREATE INDEX idx_users_ruc ON users(ruc);
