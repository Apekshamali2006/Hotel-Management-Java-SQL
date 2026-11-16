CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN'
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(30) NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS guests (
    guest_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Booked',
    CONSTRAINT fk_booking_guest FOREIGN KEY (guest_id)
        REFERENCES guests(guest_id),
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_date DATE NOT NULL,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id)
        REFERENCES bookings(booking_id)
);

INSERT INTO users(username,password,role)
SELECT 'admin','admin123','ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

INSERT INTO rooms(room_number,room_type,price_per_day,status)
SELECT '101','Single',1500,'Available'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_number='101');

INSERT INTO rooms(room_number,room_type,price_per_day,status)
SELECT '102','Double',2500,'Available'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_number='102');

INSERT INTO rooms(room_number,room_type,price_per_day,status)
SELECT '103','Deluxe',3500,'Available'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_number='103');
