-- ═══════════════════════════════════════════════════════
-- Ocean View Resort Room Reservation System
-- Database Schema (MySQL 8.x)
-- ═══════════════════════════════════════════════════════
-- Author : Ocean View Resort Development Team
-- Version: 1.0.0
-- ═══════════════════════════════════════════════════════

-- Create database (run manually if not using spring.sql.init)
-- CREATE DATABASE IF NOT EXISTS oceanview_resort
--     CHARACTER SET utf8mb4
--     COLLATE utf8mb4_unicode_ci;
-- USE oceanview_resort;

-- ───────────────────────────────────────────────────────
-- 1. USERS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id              VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    username        VARCHAR(50)     UNIQUE NOT NULL,
    password        VARCHAR(255)    NOT NULL,           -- BCrypt hashed
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    UNIQUE NOT NULL,
    phone           VARCHAR(20),
    role            ENUM('CUSTOMER', 'STAFF', 'MANAGER', 'MAINTENANCE') NOT NULL,
    is_active       BOOLEAN         DEFAULT TRUE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 2. CUSTOMERS TABLE (extends users)
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    id              VARCHAR(36)     PRIMARY KEY,
    user_id         VARCHAR(36)     UNIQUE NOT NULL,
    address         TEXT,
    loyalty_points  INT             DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 3. ROOMS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    id              VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    room_number     VARCHAR(10)     UNIQUE NOT NULL,
    room_type       ENUM('STANDARD', 'DELUXE', 'SUITE', 'PENTHOUSE') NOT NULL,
    floor_number    INT             NOT NULL,
    capacity        INT             NOT NULL,
    rate_per_night  DECIMAL(10,2)   NOT NULL,
    description     TEXT,
    is_available    BOOLEAN         DEFAULT TRUE,
    amenities       JSON,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 4. RESERVATIONS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations (
    id                  VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    reservation_number  VARCHAR(20)     UNIQUE NOT NULL,
    customer_id         VARCHAR(36)     NOT NULL,
    room_id             VARCHAR(36)     NOT NULL,
    staff_id            VARCHAR(36),
    guest_name          VARCHAR(200)    NOT NULL,
    guest_address       TEXT,
    guest_contact       VARCHAR(20)     NOT NULL,
    check_in_date       DATE            NOT NULL,
    check_out_date      DATE            NOT NULL,
    num_guests          INT             NOT NULL DEFAULT 1,
    special_requests    TEXT,
    status              ENUM('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')
                        DEFAULT 'PENDING',
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id)     REFERENCES rooms(id),
    FOREIGN KEY (staff_id)    REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 5. BILLS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bills (
    id                  VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    reservation_id      VARCHAR(36)     UNIQUE NOT NULL,
    num_nights          INT             NOT NULL,
    room_rate           DECIMAL(10,2)   NOT NULL,
    subtotal            DECIMAL(10,2)   NOT NULL,
    tax_rate            DECIMAL(5,2)    DEFAULT 10.00,
    tax_amount          DECIMAL(10,2)   NOT NULL,
    discount_amount     DECIMAL(10,2)   DEFAULT 0.00,
    total_amount        DECIMAL(10,2)   NOT NULL,
    payment_status      ENUM('UNPAID', 'PARTIAL', 'PAID') DEFAULT 'UNPAID',
    generated_at        TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 6. PAYMENTS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    id                      VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    bill_id                 VARCHAR(36)     NOT NULL,
    reservation_id          VARCHAR(36)     NOT NULL,
    amount_paid             DECIMAL(10,2)   NOT NULL,
    payment_method          ENUM('CASH', 'CARD', 'POINTS', 'ONLINE') NOT NULL,
    transaction_reference   VARCHAR(100),
    payment_date            TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    processed_by            VARCHAR(36),
    notes                   TEXT,
    FOREIGN KEY (bill_id)        REFERENCES bills(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (processed_by)   REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 7. AUDIT LOG TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS audit_log (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    user_id         VARCHAR(36),
    action          VARCHAR(100)    NOT NULL,
    entity_type     VARCHAR(50),
    entity_id       VARCHAR(36),
    old_value       JSON,
    new_value       JSON,
    ip_address      VARCHAR(45),
    timestamp       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────────────────────────────
-- 8. LOYALTY TRANSACTIONS TABLE
-- ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id                  VARCHAR(36)     PRIMARY KEY DEFAULT (UUID()),
    customer_id         VARCHAR(36)     NOT NULL,
    points              INT             NOT NULL,
    transaction_type    VARCHAR(10)     NOT NULL,       -- EARN or REDEEM
    description         TEXT,
    reference_id        VARCHAR(36),
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════
-- INDEXES for performance
-- ═══════════════════════════════════════════════════════
CREATE INDEX idx_reservations_customer  ON reservations(customer_id);
CREATE INDEX idx_reservations_room      ON reservations(room_id);
CREATE INDEX idx_reservations_dates     ON reservations(check_in_date, check_out_date);
CREATE INDEX idx_reservations_status    ON reservations(status);
CREATE INDEX idx_payments_bill          ON payments(bill_id);
CREATE INDEX idx_audit_user             ON audit_log(user_id);
CREATE INDEX idx_audit_timestamp        ON audit_log(timestamp);

-- ═══════════════════════════════════════════════════════
-- STORED PROCEDURE: Calculate Bill
-- ═══════════════════════════════════════════════════════
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS CalculateBill(IN res_id VARCHAR(36))
BEGIN
    DECLARE v_nights    INT;
    DECLARE v_rate      DECIMAL(10,2);
    DECLARE v_subtotal  DECIMAL(10,2);
    DECLARE v_tax       DECIMAL(10,2);
    DECLARE v_total     DECIMAL(10,2);

    -- Retrieve number of nights and room rate
    SELECT DATEDIFF(r.check_out_date, r.check_in_date),
           rm.rate_per_night
    INTO v_nights, v_rate
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.id
    WHERE r.id = res_id;

    -- Calculate amounts
    SET v_subtotal = v_nights * v_rate;
    SET v_tax      = v_subtotal * 0.10;
    SET v_total    = v_subtotal + v_tax;

    -- Insert or update bill
    INSERT INTO bills (reservation_id, num_nights, room_rate, subtotal,
                       tax_amount, total_amount)
    VALUES (res_id, v_nights, v_rate, v_subtotal, v_tax, v_total)
    ON DUPLICATE KEY UPDATE
        num_nights   = v_nights,
        room_rate    = v_rate,
        subtotal     = v_subtotal,
        tax_amount   = v_tax,
        total_amount = v_total;
END //
DELIMITER ;

-- ═══════════════════════════════════════════════════════
-- TRIGGER: Update room availability on reservation status change
-- ═══════════════════════════════════════════════════════
DELIMITER //
CREATE TRIGGER IF NOT EXISTS after_reservation_status_update
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'CHECKED_IN' THEN
        UPDATE rooms SET is_available = FALSE WHERE id = NEW.room_id;
    ELSEIF NEW.status = 'CHECKED_OUT' OR NEW.status = 'CANCELLED' THEN
        UPDATE rooms SET is_available = TRUE  WHERE id = NEW.room_id;
    END IF;
END //
DELIMITER ;
