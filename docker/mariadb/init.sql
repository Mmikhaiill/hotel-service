-- MariaDB Initialization Script for Hotel Service
-- This script is executed when the MariaDB container starts for the first time

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS hoteldb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hoteldb;

-- Create tables (optional - Hibernate will create them automatically)
-- These are just for reference and initial data

-- Addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    postal_code VARCHAR(20),
    city VARCHAR(100) NOT NULL,
    street VARCHAR(200) NOT NULL,
    building VARCHAR(50) NOT NULL,
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hotels table
CREATE TABLE IF NOT EXISTS hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_id BIGINT NOT NULL UNIQUE,
    category VARCHAR(20),
    notes TEXT,
    INDEX idx_name (name),
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO addresses (postal_code, city, street, building) VALUES
    ('101000', 'Москва', 'Тверская улица', '15'),
    ('190000', 'Санкт-Петербург', 'Невский проспект', '28'),
    ('420000', 'Казань', 'улица Баумана', '44'),
    ('630000', 'Новосибирск', 'Красный проспект', '100'),
    ('350000', 'Краснодар', 'улица Красная', '75');

INSERT INTO hotels (name, address_id, category, notes) VALUES
    ('Grand Hotel Moscow', 1, 'FIVE_STARS', 'Роскошный отель в центре Москвы с видом на Кремль'),
    ('Невский Палас', 2, 'FOUR_STARS', 'Исторический отель на Невском проспекте'),
    ('Казань Плаза', 3, 'FOUR_STARS', 'Современный бизнес-отель в центре Казани'),
    ('Сибирь', 4, 'THREE_STARS', 'Комфортабельный отель для деловых путешественников'),
    ('Кубань', 5, 'THREE_STARS', 'Уютный отель в южной столице России');
