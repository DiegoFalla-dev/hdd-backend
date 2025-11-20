-- =====================================================
-- TABLAS DE COMPRAS Y PAGOS
-- =====================================================

-- Tabla de compras/órdenes
CREATE TABLE IF NOT EXISTS purchases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT,
    payment_method_id BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL,
    purchase_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(100),
    session_id VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
    INDEX idx_user_id (user_id),
    INDEX idx_purchase_number (purchase_number),
    INDEX idx_session_id (session_id),
    INDEX idx_purchase_date (purchase_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de items de compra
CREATE TABLE IF NOT EXISTS purchase_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    concession_product_id BIGINT,
    seat_identifiers VARCHAR(500),
    FOREIGN KEY (purchase_id) REFERENCES purchases(id) ON DELETE CASCADE,
    FOREIGN KEY (concession_product_id) REFERENCES concession_products(id),
    INDEX idx_purchase_id (purchase_id),
    INDEX idx_item_type (item_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
