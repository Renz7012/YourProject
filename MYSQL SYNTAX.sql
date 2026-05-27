CREATE DATABASE vet_clinic;

USE vet_clinic;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('CLIENT', 'VET') NOT NULL
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    pet_name VARCHAR(100) NOT NULL,
    appt_date VARCHAR(20) NOT NULL,
    appt_time VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending', -- Pending, Approved, Completed
    diagnosis VARCHAR(255) DEFAULT 'Pending',
    lab_service VARCHAR(100) DEFAULT 'None',
    lab_fee DECIMAL(10,2) DEFAULT 0.00,
    treatment_service VARCHAR(100) DEFAULT 'None',
    treatment_fee DECIMAL(10,2) DEFAULT 0.00,
    total_bill DECIMAL(10,2) DEFAULT 0.00,
    is_paid BOOLEAN DEFAULT FALSE
);

INSERT INTO users (username, password, role) VALUES ('vet', 'admin', 'VET');