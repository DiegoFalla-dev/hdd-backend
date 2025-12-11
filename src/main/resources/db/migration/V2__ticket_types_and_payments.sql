-- Seed ticket types and ensure payment_transactions table exists

CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider VARCHAR(50),
    reference VARCHAR(80),
    created_at DATETIME NOT NULL,
    raw_response VARCHAR(2000),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;

INSERT INTO ticket_types (code, name, price, active) VALUES
('PROMO_ONLINE', 'PROMO ONLINE', 14.96, true),
('PERSONA_CON_DISCAPACIDAD', 'PERSONA CON DISCAPACIDAD', 17.70, true),
('SILLA_DE_RUEDAS', 'SILLA DE RUEDAS', 17.70, true),
('NINO', 'NIÑO', 21.60, true),
('ADULTO', 'ADULTO', 23.60, true)
ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price), active = VALUES(active);

INSERT INTO ticket_types (code, name, price, active) VALUES
('50_DCTO_BANCO_RIPLEY', '50% DCTO BANCO RIPLEY', 12.80, true)
ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price), active = VALUES(active);
