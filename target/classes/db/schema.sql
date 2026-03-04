-- ═══════════════════════════════════════════════════════════════════════
-- Ocean View Resort – Database Schema
-- Database : ocean_view_resort (MySQL 8.x)
-- Java EE Edition (same schema as Spring Boot version)
-- ═══════════════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS ocean_view_resort
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ocean_view_resort;

-- ───────────────────────────────────────────────────────────────────────
-- USERS
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('CUSTOMER','STAFF','MANAGER','MAINTENANCE') NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- STAFF (joined sub-table of users)
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    department VARCHAR(100),
    employee_id VARCHAR(20),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- MANAGERS (joined sub-table of staff)
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS managers (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES staff(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- CUSTOMERS (joined sub-table of users)
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    address TEXT,
    loyalty_points INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- ROOMS
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('STANDARD','DELUXE','SUITE','PENTHOUSE') NOT NULL,
    floor_number INT NOT NULL DEFAULT 1,
    capacity INT NOT NULL DEFAULT 2,
    rate_per_night DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    amenities JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rooms_type (room_type),
    INDEX idx_rooms_number (room_number)
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- RESERVATIONS
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    reservation_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    room_id VARCHAR(36) NOT NULL,
    staff_id VARCHAR(36),
    guest_name VARCHAR(200),
    guest_address TEXT,
    guest_contact VARCHAR(100),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    num_guests INT NOT NULL DEFAULT 1,
    special_requests TEXT,
    status ENUM('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (staff_id) REFERENCES users(id),
    INDEX idx_res_number (reservation_number),
    INDEX idx_res_customer (customer_id),
    INDEX idx_res_dates (check_in_date, check_out_date),
    INDEX idx_res_status (status)
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- BILLS
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bills (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    reservation_id VARCHAR(36) NOT NULL UNIQUE,
    num_nights INT NOT NULL,
    room_rate DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    tax_amount DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    payment_status ENUM('UNPAID','PARTIALLY_PAID','PAID') NOT NULL DEFAULT 'UNPAID',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    INDEX idx_bill_res (reservation_id),
    INDEX idx_bill_status (payment_status)
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- PAYMENTS
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    bill_id VARCHAR(36) NOT NULL,
    reservation_id VARCHAR(36),
    amount_paid DECIMAL(12,2) NOT NULL,
    payment_method ENUM('CASH','CARD','POINTS','ONLINE') NOT NULL,
    transaction_reference VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR(36),
    notes TEXT,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (processed_by) REFERENCES users(id),
    INDEX idx_pay_bill (bill_id)
) ENGINE=InnoDB;

-- ───────────────────────────────────────────────────────────────────────
-- LOYALTY TRANSACTIONS (optional tracking)
-- ───────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    reservation_id VARCHAR(36),
    points INT NOT NULL,
    transaction_type ENUM('EARNED','REDEEMED') NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) ENGINE=InnoDB;

