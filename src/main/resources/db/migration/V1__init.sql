-- Flyway migration: initial schema

CREATE TABLE IF NOT EXISTS cinemas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  address VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS auditoriums (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  capacity INT,
  location VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS movies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  description TEXT,
  genre VARCHAR(100),
  duration_minutes INT,
  release_date DATE,
  poster_url VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS showtimes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  movie_id BIGINT,
  auditorium_id BIGINT,
  start_time DATETIME,
  end_time DATETIME,
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (auditorium_id) REFERENCES auditoriums(id)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE,
  email VARCHAR(255),
  password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT,
  role VARCHAR(100),
  PRIMARY KEY (user_id, role),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  showtime_id BIGINT,
  seats INT,
  total_price DOUBLE,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
);

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(100),
  amount DOUBLE
);

-- seed sample movie
INSERT INTO movies (title, description, genre, duration_minutes) VALUES
('The Example Movie', 'Demo movie for development', 'Drama', 120);

-- seed admin user (password should be encoded by application; this is plain for dev only)
INSERT INTO users (username, email, password) VALUES ('admin', 'admin@example.com', 'admin');
INSERT INTO user_roles (user_id, role) VALUES (LAST_INSERT_ID(), 'ROLE_ADMIN');
