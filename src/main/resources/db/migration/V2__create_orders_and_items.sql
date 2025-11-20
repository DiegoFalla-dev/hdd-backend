-- V2__create_orders_and_items.sql
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  showtime_id BIGINT,
  user_id BIGINT,
  created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT,
  seat_id BIGINT,
  seat_identifier VARCHAR(255),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (seat_id) REFERENCES seats(id)
);
