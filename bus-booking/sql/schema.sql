-- Kenya Bus Booking System Database Schema
-- Run this script in MySQL to set up the database

CREATE DATABASE IF NOT EXISTS bus_booking_db;
USE bus_booking_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(20) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(10) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin table
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Insert default admin
INSERT IGNORE INTO admins (username, password) VALUES ('admin', 'admin123');

-- Bus companies table
CREATE TABLE IF NOT EXISTS bus_companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL
);

-- Insert bus companies
INSERT IGNORE INTO bus_companies (id, name, total_seats) VALUES
(1, 'Maseno Speeder Bus', 42),
(2, 'Kisumu Express', 48),
(3, 'Green Safaris', 46),
(4, 'Dualipa', 40),
(5, 'Honkie Travels', 41);

-- Routes table
CREATE TABLE IF NOT EXISTS routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    bus_company_id INT,
    departure_time TIME NOT NULL,
    FOREIGN KEY (bus_company_id) REFERENCES bus_companies(id)
);

-- 8 Kenyan cities/towns
-- Nairobi, Mombasa, Kisumu, Nakuru, Eldoret, Thika, Nyeri, Malindi

INSERT INTO routes (origin, destination, price, bus_company_id, departure_time) VALUES
('Nairobi', 'Mombasa', 1500.00, 1, '06:00:00'),
('Nairobi', 'Kisumu', 1200.00, 2, '07:00:00'),
('Nairobi', 'Nakuru', 600.00, 3, '08:00:00'),
('Nairobi', 'Eldoret', 1000.00, 4, '06:30:00'),
('Mombasa', 'Malindi', 400.00, 5, '09:00:00'),
('Kisumu', 'Eldoret', 500.00, 1, '10:00:00'),
('Nakuru', 'Nyeri', 350.00, 2, '07:30:00'),
('Nairobi', 'Thika', 200.00, 3, '08:30:00'),
('Mombasa', 'Nairobi', 1500.00, 4, '06:00:00'),
('Kisumu', 'Nairobi', 1200.00, 5, '07:00:00'),
('Eldoret', 'Nairobi', 1000.00, 1, '05:30:00'),
('Thika', 'Nyeri', 450.00, 2, '09:00:00'),
('Malindi', 'Mombasa', 400.00, 3, '08:00:00'),
('Nyeri', 'Nairobi', 500.00, 4, '06:00:00'),
('Nakuru', 'Kisumu', 700.00, 5, '07:00:00');

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    route_id INT NOT NULL,
    seat_number INT NOT NULL,
    booking_date DATE NOT NULL,
    travel_date DATE NOT NULL,
    mpesa_code VARCHAR(20),
    phone_paid VARCHAR(10),
    amount_paid DECIMAL(10,2),
    status ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (route_id) REFERENCES routes(id),
    UNIQUE KEY unique_seat_booking (route_id, seat_number, travel_date)
);

