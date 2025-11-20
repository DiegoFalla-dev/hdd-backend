-- V1__create_seats_and_reservations.sql
CREATE TABLE IF NOT EXISTS seats (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  showtime_id BIGINT NOT NULL,
  seat_row VARCHAR(4) NOT NULL,
  seat_number INT NOT NULL,
  label VARCHAR(50),
  state VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  version BIGINT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_showtime_seat (showtime_id, seat_row, seat_number)
);

CREATE TABLE IF NOT EXISTS seat_reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  showtime_id BIGINT NOT NULL,
  seat_id BIGINT NOT NULL,
  user_id BIGINT NULL,
  status VARCHAR(20) NOT NULL,
  token VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL,
  expires_at DATETIME NOT NULL,
  FOREIGN KEY (seat_id) REFERENCES seats(id)
);
