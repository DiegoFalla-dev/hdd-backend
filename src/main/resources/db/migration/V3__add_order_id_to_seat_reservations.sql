-- V3__add_order_id_to_seat_reservations.sql
ALTER TABLE seat_reservations
  ADD COLUMN order_id BIGINT NULL;

ALTER TABLE seat_reservations
  ADD CONSTRAINT FK_seat_reservation_order FOREIGN KEY (order_id) REFERENCES orders(id);
