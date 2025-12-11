-- Migration V5: Agregar columna ticket_type a order_items
-- Fecha: 2025-12-11
-- Descripción: Agregar campo para almacenar el tipo de entrada (ADULTO, NIÑO, etc.)

ALTER TABLE order_items
ADD COLUMN ticket_type VARCHAR(50) DEFAULT NULL COMMENT 'Tipo de entrada: ADULTO, NIÑO, SENIOR, etc.';
