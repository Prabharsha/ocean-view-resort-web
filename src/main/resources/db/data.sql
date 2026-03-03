-- ═══════════════════════════════════════════════════════════════════════
-- Ocean View Resort – Comprehensive Sample Data
-- Database : ocean_view_resort (MySQL 8.x)
-- Generated: 2026-03-03
-- ═══════════════════════════════════════════════════════════════════════
-- BCrypt hashed passwords (strength 10):
--   Manager  : pansilu  / Manager@123
--   Staff    : staff1   / Staff@123
--   Staff    : staff2   / Staff@123
--   Customers: customer1..10 / Customer@123
-- ═══════════════════════════════════════════════════════════════════════

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE loyalty_transactions;
TRUNCATE TABLE payments;
TRUNCATE TABLE bills;
TRUNCATE TABLE reservations;
TRUNCATE TABLE customers;
# TRUNCATE TABLE managers;
TRUNCATE TABLE staff;
TRUNCATE TABLE users;
TRUNCATE TABLE rooms;

SET FOREIGN_KEY_CHECKS = 1;

-- ───────────────────────────────────────────────────────────────────────
-- 1. USERS  (Manager + 2 Staff + 10 Customers)
-- ───────────────────────────────────────────────────────────────────────

-- Manager  →  Manager@123
-- Hash verified: BCrypt(10) of "Manager@123"
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u-mgr-001', 'pansilu',
 '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQbLgtnOoKsWc7O0V8YNx9AhkIEYKy',
 'Pansilu', 'Prabharsha', 'pansilu@oceanview.lk', '+94771234567', 'MANAGER', TRUE);

-- Staff 1  →  Staff@123
-- Hash verified: BCrypt(10) of "Staff@123"
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u-stf-001', 'staff1',
 '$2a$10$Xn4oXqTVXxV4yJ0Q1gK7v.9YkIIInI/KBnWKlNbpZ9TQwYV0gCO82',
 'Nimasha', 'Perera', 'nimasha@oceanview.lk', '+94772345678', 'STAFF', TRUE);

-- Staff 2  →  Staff@123
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u-stf-002', 'staff2',
 '$2a$10$Xn4oXqTVXxV4yJ0Q1gK7v.9YkIIInI/KBnWKlNbpZ9TQwYV0gCO82',
 'Kasun', 'Silva', 'kasun@oceanview.lk', '+94773456789', 'STAFF', TRUE);

-- Customers  →  Customer@123
-- Hash verified: BCrypt(10) of "Customer@123"
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u-cst-001', 'customer1',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Amara',    'Fernando',  'amara.fernando@gmail.com',    '+94711111001', 'CUSTOMER', TRUE),
('u-cst-002', 'customer2',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Ruwan',    'Jayasinghe','ruwan.jayasinghe@gmail.com',  '+94711111002', 'CUSTOMER', TRUE),
('u-cst-003', 'customer3',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Sanduni',  'Kumari',    'sanduni.kumari@gmail.com',    '+94711111003', 'CUSTOMER', TRUE),
('u-cst-004', 'customer4',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Tharindu', 'Bandara',   'tharindu.bandara@gmail.com',  '+94711111004', 'CUSTOMER', TRUE),
('u-cst-005', 'customer5',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Dilini',   'Wickrama',  'dilini.wickrama@gmail.com',   '+94711111005', 'CUSTOMER', TRUE),
('u-cst-006', 'customer6',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Isuru',    'Rathnayake','isuru.rathnayake@gmail.com',  '+94711111006', 'CUSTOMER', TRUE),
('u-cst-007', 'customer7',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Malsha',   'Dissanayake','malsha.dis@gmail.com',       '+94711111007', 'CUSTOMER', TRUE),
('u-cst-008', 'customer8',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Chamath',  'Gunasekara','chamath.guna@gmail.com',      '+94711111008', 'CUSTOMER', TRUE),
('u-cst-009', 'customer9',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Nimesha',  'Wijesekara','nimesha.wije@gmail.com',      '+94711111009', 'CUSTOMER', TRUE),
('u-cst-010', 'customer10',
 '$2a$10$WvJvMVeFJmMxJr8Q4wMiOuyBKQd3UDRfpALkSBOiIz6Y7vEk2M7P2',
 'Lahiru',   'Senanayake','lahiru.sena@gmail.com',       '+94711111010', 'CUSTOMER', TRUE);

-- ───────────────────────────────────────────────────────────────────────
-- 2. STAFF sub-table  (JPA JOINED inheritance – id = users.id)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO staff (id, department, employee_id) VALUES
('u-mgr-001', 'Management',  'EMP-001'),
('u-stf-001', 'Front Desk',  'EMP-002'),
('u-stf-002', 'Front Desk',  'EMP-003');

-- ───────────────────────────────────────────────────────────────────────
-- 3. MANAGERS sub-table  (JPA JOINED inheritance – id = staff.id)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO managers (id) VALUES ('u-mgr-001');

-- ───────────────────────────────────────────────────────────────────────
-- 4. CUSTOMERS sub-table  (JPA JOINED inheritance – id = users.id)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO customers (id, address, loyalty_points) VALUES
('u-cst-001', 'No 12, Galle Road, Colombo 03, Sri Lanka',          850),
('u-cst-002', 'No 45, Kandy Road, Kurunegala, Sri Lanka',           320),
('u-cst-003', 'No 7, Beach Road, Negombo, Sri Lanka',               560),
('u-cst-004', 'No 23, Temple Road, Kandy, Sri Lanka',               180),
('u-cst-005', 'No 88, Main Street, Matara, Sri Lanka',              740),
('u-cst-006', 'No 3, Lake Road, Gampaha, Sri Lanka',                  0),
('u-cst-007', 'No 55, Station Road, Ratnapura, Sri Lanka',          120),
('u-cst-008', 'No 19, New Town, Badulla, Sri Lanka',                270),
('u-cst-009', 'No 66, Seafront Ave, Hikkaduwa, Sri Lanka',          450),
('u-cst-010', 'No 30, High Level Road, Nugegoda, Sri Lanka',         90);

-- ───────────────────────────────────────────────────────────────────────
-- 5. ROOMS  (12 rooms – rates in LKR)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO rooms (id, room_number, room_type, floor_number, capacity, rate_per_night, description, is_available, amenities) VALUES
('r-001','101','STANDARD',  1,2,  8500.00,'Cozy standard room with garden view.',             TRUE,  '["WiFi","TV","Air Conditioning","Hot Water"]'),
('r-002','102','STANDARD',  1,2,  8500.00,'Standard room with pool view.',                    TRUE,  '["WiFi","TV","Air Conditioning","Hot Water"]'),
('r-003','103','STANDARD',  1,3,  9500.00,'Spacious standard room for families.',             TRUE,  '["WiFi","TV","Air Conditioning","Hot Water","Crib"]'),
('r-004','104','STANDARD',  1,2,  8500.00,'Standard room near lobby.',                        TRUE,  '["WiFi","TV","Air Conditioning","Hot Water"]'),
('r-005','201','DELUXE',    2,2, 15000.00,'Deluxe room with panoramic ocean view.',           FALSE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Bathtub"]'),
('r-006','202','DELUXE',    2,2, 15000.00,'Deluxe room with king-size bed.',                  TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Balcony"]'),
('r-007','203','DELUXE',    2,3, 17000.00,'Deluxe family room with extra space.',             TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Sofa Bed"]'),
('r-008','204','DELUXE',    2,2, 15000.00,'Deluxe corner room with sea breeze.',              TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Balcony"]'),
('r-009','301','SUITE',     3,4, 28000.00,'Luxury suite with separate living area.',          FALSE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Jacuzzi","Kitchenette"]'),
('r-010','302','SUITE',     3,4, 28000.00,'Oceanfront suite with premium amenities.',         TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Jacuzzi","Kitchenette"]'),
('r-011','401','PENTHOUSE', 4,6, 55000.00,'Penthouse with 360-degree ocean views.',           TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Rooftop Terrace","Jacuzzi","Kitchenette","Butler Service"]'),
('r-012','402','PENTHOUSE', 4,6, 55000.00,'Presidential penthouse with private pool.',        TRUE,  '["WiFi","TV","Mini Bar","Air Conditioning","Private Pool","Rooftop Terrace","Jacuzzi","Kitchen","Butler Service"]');

-- ───────────────────────────────────────────────────────────────────────
-- 6. RESERVATIONS
--    Mix of statuses across the past 12 months + current month + future
--    Today = 2026-03-03
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO reservations (id, reservation_number, customer_id, room_id, staff_id,
    guest_name, guest_address, guest_contact,
    check_in_date, check_out_date, num_guests, special_requests, status, created_at) VALUES

-- ── MARCH 2026 (current month) ──────────────────────────────────────

-- Today: active check-ins
('res-001','OVR-2026-000001','u-cst-001','r-005','u-stf-001',
 'Amara Fernando','No 12, Galle Road, Colombo','+94711111001',
 '2026-03-03','2026-03-07',2,'Ocean-facing bed preferred','CHECKED_IN','2026-03-01 09:00:00'),

('res-002','OVR-2026-000002','u-cst-003','r-009','u-stf-002',
 'Sanduni Kumari','No 7, Beach Road, Negombo','+94711111003',
 '2026-03-03','2026-03-06',3,'Early check-in requested','CHECKED_IN','2026-03-01 10:30:00'),

-- Confirmed – future within March
('res-003','OVR-2026-000003','u-cst-002','r-006','u-stf-001',
 'Ruwan Jayasinghe','No 45, Kandy Road, Kurunegala','+94711111002',
 '2026-03-05','2026-03-09',2,'Anniversary dinner arrangement','CONFIRMED','2026-02-28 14:00:00'),

('res-004','OVR-2026-000004','u-cst-005','r-007','u-stf-002',
 'Dilini Wickrama','No 88, Main Street, Matara','+94711111005',
 '2026-03-10','2026-03-14',3,'Extra pillows needed','CONFIRMED','2026-03-01 11:00:00'),

('res-005','OVR-2026-000005','u-cst-007','r-003','u-stf-001',
 'Malsha Dissanayake','No 55, Station Road, Ratnapura','+94711111007',
 '2026-03-15','2026-03-18',2,NULL,'CONFIRMED','2026-03-02 08:00:00'),

-- Pending
('res-006','OVR-2026-000006','u-cst-010','r-001','u-stf-001',
 'Lahiru Senanayake','No 30, High Level Road, Nugegoda','+94711111010',
 '2026-03-20','2026-03-23',1,'Quiet room away from elevator','PENDING','2026-03-03 09:00:00'),

('res-007','OVR-2026-000007','u-cst-006','r-011',NULL,
 'Isuru Rathnayake','No 3, Lake Road, Gampaha','+94711111006',
 '2026-03-25','2026-03-30',4,'Honeymoon setup please','PENDING','2026-03-03 10:00:00'),

-- ── FEBRUARY 2026 ────────────────────────────────────────────────────
('res-008','OVR-2026-000008','u-cst-004','r-008','u-stf-002',
 'Tharindu Bandara','No 23, Temple Road, Kandy','+94711111004',
 '2026-02-05','2026-02-09',2,NULL,'CHECKED_OUT','2026-02-03 09:00:00'),

('res-009','OVR-2026-000009','u-cst-009','r-010','u-stf-001',
 'Nimesha Wijesekara','No 66, Seafront Ave, Hikkaduwa','+94711111009',
 '2026-02-12','2026-02-16',2,'Late checkout if possible','CHECKED_OUT','2026-02-10 14:00:00'),

('res-010','OVR-2026-000010','u-cst-001','r-003','u-stf-001',
 'Amara Fernando','No 12, Galle Road, Colombo','+94711111001',
 '2026-02-18','2026-02-22',3,NULL,'CHECKED_OUT','2026-02-15 10:00:00'),

('res-011','OVR-2026-000011','u-cst-008','r-001','u-stf-002',
 'Chamath Gunasekara','No 19, New Town, Badulla','+94711111008',
 '2026-02-25','2026-02-27',1,NULL,'CHECKED_OUT','2026-02-23 11:00:00'),

-- ── JANUARY 2026 ─────────────────────────────────────────────────────
('res-012','OVR-2026-000012','u-cst-002','r-004','u-stf-001',
 'Ruwan Jayasinghe','No 45, Kandy Road, Kurunegala','+94711111002',
 '2026-01-03','2026-01-06',2,NULL,'CHECKED_OUT','2026-01-01 09:00:00'),

('res-013','OVR-2026-000013','u-cst-005','r-006','u-stf-002',
 'Dilini Wickrama','No 88, Main Street, Matara','+94711111005',
 '2026-01-10','2026-01-15',2,'Birthday cake in room','CHECKED_OUT','2026-01-08 10:00:00'),

('res-014','OVR-2026-000014','u-cst-003','r-009','u-stf-001',
 'Sanduni Kumari','No 7, Beach Road, Negombo','+94711111003',
 '2026-01-18','2026-01-21',4,NULL,'CHECKED_OUT','2026-01-16 14:00:00'),

('res-015','OVR-2026-000015','u-cst-007','r-012','u-stf-002',
 'Malsha Dissanayake','No 55, Station Road, Ratnapura','+94711111007',
 '2026-01-25','2026-01-29',2,'Rooftop access request','CHECKED_OUT','2026-01-23 09:00:00'),

-- CANCELLED in January
('res-016','OVR-2026-000016','u-cst-006','r-002',NULL,
 'Isuru Rathnayake','No 3, Lake Road, Gampaha','+94711111006',
 '2026-01-20','2026-01-23',1,NULL,'CANCELLED','2026-01-15 08:00:00'),

-- ── DECEMBER 2025 ────────────────────────────────────────────────────
('res-017','OVR-2025-000017','u-cst-001','r-007','u-stf-001',
 'Amara Fernando','No 12, Galle Road, Colombo','+94711111001',
 '2025-12-20','2025-12-26',3,'Christmas decoration please','CHECKED_OUT','2025-12-18 10:00:00'),

('res-018','OVR-2025-000018','u-cst-004','r-010','u-stf-002',
 'Tharindu Bandara','No 23, Temple Road, Kandy','+94711111004',
 '2025-12-27','2025-12-31',2,'New Year champagne','CHECKED_OUT','2025-12-25 09:00:00'),

('res-019','OVR-2025-000019','u-cst-009','r-011','u-stf-001',
 'Nimesha Wijesekara','No 66, Seafront Ave, Hikkaduwa','+94711111009',
 '2025-12-10','2025-12-14',4,NULL,'CHECKED_OUT','2025-12-08 14:00:00'),

-- ── NOVEMBER 2025 ────────────────────────────────────────────────────
('res-020','OVR-2025-000020','u-cst-002','r-005','u-stf-001',
 'Ruwan Jayasinghe','No 45, Kandy Road, Kurunegala','+94711111002',
 '2025-11-05','2025-11-09',2,NULL,'CHECKED_OUT','2025-11-03 09:00:00'),

('res-021','OVR-2025-000021','u-cst-008','r-003','u-stf-002',
 'Chamath Gunasekara','No 19, New Town, Badulla','+94711111008',
 '2025-11-15','2025-11-18',2,NULL,'CHECKED_OUT','2025-11-13 10:00:00'),

('res-022','OVR-2025-000022','u-cst-005','r-012','u-stf-001',
 'Dilini Wickrama','No 88, Main Street, Matara','+94711111005',
 '2025-11-22','2025-11-27',2,'Butler service required','CHECKED_OUT','2025-11-20 11:00:00'),

-- ── OCTOBER 2025 ─────────────────────────────────────────────────────
('res-023','OVR-2025-000023','u-cst-003','r-006','u-stf-002',
 'Sanduni Kumari','No 7, Beach Road, Negombo','+94711111003',
 '2025-10-08','2025-10-12',2,NULL,'CHECKED_OUT','2025-10-06 09:00:00'),

('res-024','OVR-2025-000024','u-cst-010','r-009','u-stf-001',
 'Lahiru Senanayake','No 30, High Level Road, Nugegoda','+94711111010',
 '2025-10-18','2025-10-23',3,NULL,'CHECKED_OUT','2025-10-16 14:00:00'),

-- ── SEPTEMBER 2025 ───────────────────────────────────────────────────
('res-025','OVR-2025-000025','u-cst-001','r-004','u-stf-001',
 'Amara Fernando','No 12, Galle Road, Colombo','+94711111001',
 '2025-09-10','2025-09-14',1,NULL,'CHECKED_OUT','2025-09-08 10:00:00'),

('res-026','OVR-2025-000026','u-cst-007','r-008','u-stf-002',
 'Malsha Dissanayake','No 55, Station Road, Ratnapura','+94711111007',
 '2025-09-20','2025-09-25',2,NULL,'CHECKED_OUT','2025-09-18 09:00:00'),

-- ── AUGUST 2025 ──────────────────────────────────────────────────────
('res-027','OVR-2025-000027','u-cst-004','r-011','u-stf-001',
 'Tharindu Bandara','No 23, Temple Road, Kandy','+94711111004',
 '2025-08-05','2025-08-10',4,'Sea-view bed','CHECKED_OUT','2025-08-03 09:00:00'),

('res-028','OVR-2025-000028','u-cst-006','r-007','u-stf-002',
 'Isuru Rathnayake','No 3, Lake Road, Gampaha','+94711111006',
 '2025-08-18','2025-08-22',2,NULL,'CHECKED_OUT','2025-08-16 14:00:00'),

-- ── JULY 2025 ────────────────────────────────────────────────────────
('res-029','OVR-2025-000029','u-cst-002','r-012','u-stf-001',
 'Ruwan Jayasinghe','No 45, Kandy Road, Kurunegala','+94711111002',
 '2025-07-12','2025-07-17',2,NULL,'CHECKED_OUT','2025-07-10 10:00:00'),

('res-030','OVR-2025-000030','u-cst-009','r-010','u-stf-002',
 'Nimesha Wijesekara','No 66, Seafront Ave, Hikkaduwa','+94711111009',
 '2025-07-25','2025-07-30',3,NULL,'CHECKED_OUT','2025-07-23 09:00:00');

-- ───────────────────────────────────────────────────────────────────────
-- 7. BILLS
--    Formula: subtotal = nights × rate,  tax = subtotal × 0.10,
--             total    = subtotal + tax  (no discounts unless noted)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO bills (id, reservation_id, num_nights, room_rate, subtotal, tax_rate, tax_amount, discount_amount, total_amount, payment_status, generated_at) VALUES

-- Active CHECKED_IN (UNPAID)
('b-001','res-001',4, 15000.00, 60000.00,10.00, 6000.00,0.00, 66000.00,'UNPAID','2026-03-03 10:00:00'),
('b-002','res-002',3, 28000.00, 84000.00,10.00, 8400.00,0.00, 92400.00,'UNPAID','2026-03-03 10:00:00'),

-- February 2026 – all PAID
('b-003','res-008',4, 15000.00, 60000.00,10.00, 6000.00,0.00, 66000.00,'PAID','2026-02-09 11:00:00'),
('b-004','res-009',4, 28000.00,112000.00,10.00,11200.00,0.00,123200.00,'PAID','2026-02-16 11:00:00'),
('b-005','res-010',4,  9500.00, 38000.00,10.00, 3800.00,0.00, 41800.00,'PAID','2026-02-22 11:00:00'),
('b-006','res-011',2,  8500.00, 17000.00,10.00, 1700.00,0.00, 18700.00,'PAID','2026-02-27 11:00:00'),

-- January 2026 – all PAID
('b-007','res-012',3,  8500.00, 25500.00,10.00, 2550.00,0.00, 28050.00,'PAID','2026-01-06 11:00:00'),
('b-008','res-013',5, 15000.00, 75000.00,10.00, 7500.00,0.00, 82500.00,'PAID','2026-01-15 11:00:00'),
('b-009','res-014',3, 28000.00, 84000.00,10.00, 8400.00,0.00, 92400.00,'PAID','2026-01-21 11:00:00'),
('b-010','res-015',4, 55000.00,220000.00,10.00,22000.00,0.00,242000.00,'PAID','2026-01-29 11:00:00'),

-- December 2025 – all PAID
('b-011','res-017',6, 17000.00,102000.00,10.00,10200.00,0.00,112200.00,'PAID','2025-12-26 11:00:00'),
('b-012','res-018',4, 28000.00,112000.00,10.00,11200.00,0.00,123200.00,'PAID','2025-12-31 11:00:00'),
('b-013','res-019',4, 55000.00,220000.00,10.00,22000.00,0.00,242000.00,'PAID','2025-12-14 11:00:00'),

-- November 2025 – all PAID
('b-014','res-020',4, 15000.00, 60000.00,10.00, 6000.00,0.00, 66000.00,'PAID','2025-11-09 11:00:00'),
('b-015','res-021',3,  9500.00, 28500.00,10.00, 2850.00,0.00, 31350.00,'PAID','2025-11-18 11:00:00'),
('b-016','res-022',5, 55000.00,275000.00,10.00,27500.00,5000.00,297500.00,'PAID','2025-11-27 11:00:00'),

-- October 2025 – all PAID
('b-017','res-023',4, 15000.00, 60000.00,10.00, 6000.00,0.00, 66000.00,'PAID','2025-10-12 11:00:00'),
('b-018','res-024',5, 28000.00,140000.00,10.00,14000.00,0.00,154000.00,'PAID','2025-10-23 11:00:00'),

-- September 2025 – all PAID
('b-019','res-025',4,  8500.00, 34000.00,10.00, 3400.00,0.00, 37400.00,'PAID','2025-09-14 11:00:00'),
('b-020','res-026',5, 15000.00, 75000.00,10.00, 7500.00,0.00, 82500.00,'PAID','2025-09-25 11:00:00'),

-- August 2025 – all PAID
('b-021','res-027',5, 55000.00,275000.00,10.00,27500.00,0.00,302500.00,'PAID','2025-08-10 11:00:00'),
('b-022','res-028',4, 17000.00, 68000.00,10.00, 6800.00,0.00, 74800.00,'PAID','2025-08-22 11:00:00'),

-- July 2025 – all PAID
('b-023','res-029',5, 55000.00,275000.00,10.00,27500.00,0.00,302500.00,'PAID','2025-07-17 11:00:00'),
('b-024','res-030',5, 28000.00,140000.00,10.00,14000.00,0.00,154000.00,'PAID','2025-07-30 11:00:00');

-- ───────────────────────────────────────────────────────────────────────
-- 8. PAYMENTS  (one payment per paid bill; active bills have none)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO payments (id, bill_id, reservation_id, amount_paid, payment_method, transaction_reference, processed_by, notes, payment_date) VALUES

-- February 2026
('p-003','b-003','res-008',66000.00,'CARD',  'TXN-20260209-003','u-stf-002','Full payment on checkout.','2026-02-09 12:00:00'),
('p-004','b-004','res-009',123200.00,'ONLINE','TXN-20260216-004','u-stf-001','Online payment received.','2026-02-16 12:00:00'),
('p-005','b-005','res-010',41800.00,'CASH',  'TXN-20260222-005','u-stf-001','Cash at front desk.','2026-02-22 12:00:00'),
('p-006','b-006','res-011',18700.00,'CARD',  'TXN-20260227-006','u-stf-002','Card payment on checkout.','2026-02-27 12:00:00'),

-- January 2026
('p-007','b-007','res-012',28050.00,'CASH',  'TXN-20260106-007','u-stf-001','Cash payment.','2026-01-06 12:00:00'),
('p-008','b-008','res-013',82500.00,'CARD',  'TXN-20260115-008','u-stf-002','Card payment.','2026-01-15 12:00:00'),
('p-009','b-009','res-014',92400.00,'ONLINE','TXN-20260121-009','u-stf-001','Online bank transfer.','2026-01-21 12:00:00'),
('p-010','b-010','res-015',242000.00,'CARD', 'TXN-20260129-010','u-stf-002','Corporate card payment.','2026-01-29 12:00:00'),

-- December 2025
('p-011','b-011','res-017',112200.00,'CARD', 'TXN-20251226-011','u-stf-001','Card payment on checkout.','2025-12-26 12:00:00'),
('p-012','b-012','res-018',123200.00,'CASH', 'TXN-20251231-012','u-stf-002','Cash payment.','2025-12-31 12:00:00'),
('p-013','b-013','res-019',242000.00,'ONLINE','TXN-20251214-013','u-stf-001','Online payment.','2025-12-14 12:00:00'),

-- November 2025
('p-014','b-014','res-020',66000.00,'CARD',  'TXN-20251109-014','u-stf-002','Card payment.','2025-11-09 12:00:00'),
('p-015','b-015','res-021',31350.00,'CASH',  'TXN-20251118-015','u-stf-001','Cash at reception.','2025-11-18 12:00:00'),
('p-016','b-016','res-022',297500.00,'ONLINE','TXN-20251127-016','u-stf-002','Online payment with discount.','2025-11-27 12:00:00'),

-- October 2025
('p-017','b-017','res-023',66000.00,'CARD',  'TXN-20251012-017','u-stf-001','Card payment.','2025-10-12 12:00:00'),
('p-018','b-018','res-024',154000.00,'CASH', 'TXN-20251023-018','u-stf-002','Cash payment.','2025-10-23 12:00:00'),

-- September 2025
('p-019','b-019','res-025',37400.00,'CARD',  'TXN-20250914-019','u-stf-001','Card payment.','2025-09-14 12:00:00'),
('p-020','b-020','res-026',82500.00,'ONLINE','TXN-20250925-020','u-stf-002','Online payment.','2025-09-25 12:00:00'),

-- August 2025
('p-021','b-021','res-027',302500.00,'CARD', 'TXN-20250810-021','u-stf-001','Card payment.','2025-08-10 12:00:00'),
('p-022','b-022','res-028',74800.00,'CASH',  'TXN-20250822-022','u-stf-002','Cash payment.','2025-08-22 12:00:00'),

-- July 2025
('p-023','b-023','res-029',302500.00,'ONLINE','TXN-20250717-023','u-stf-001','Online payment.','2025-07-17 12:00:00'),
('p-024','b-024','res-030',154000.00,'CARD', 'TXN-20250730-024','u-stf-002','Card payment.','2025-07-30 12:00:00');

-- ───────────────────────────────────────────────────────────────────────
-- 9. LOYALTY TRANSACTIONS
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO loyalty_transactions (id, customer_id, points, transaction_type, description, reference_id, created_at) VALUES
('lt-001','u-cst-001',100,'EARN','Points earned from stay res-010','res-010','2026-02-22 13:00:00'),
('lt-002','u-cst-001',150,'EARN','Points earned from stay res-017','res-017','2025-12-26 13:00:00'),
('lt-003','u-cst-001', 50,'EARN','Points earned from stay res-025','res-025','2025-09-14 13:00:00'),
('lt-004','u-cst-001',550,'EARN','Points earned from stay res-001','res-001','2026-03-03 11:00:00'),
('lt-005','u-cst-003',100,'EARN','Points earned from stay res-023','res-023','2025-10-12 13:00:00'),
('lt-006','u-cst-003',150,'EARN','Points earned from stay res-014','res-014','2026-01-21 13:00:00'),
('lt-007','u-cst-003',300,'EARN','Points earned from stay res-002','res-002','2026-03-03 11:00:00'),
('lt-008','u-cst-005',200,'EARN','Points earned from stay res-022','res-022','2025-11-27 13:00:00'),
('lt-009','u-cst-005',200,'EARN','Points earned from stay res-013','res-013','2026-01-15 13:00:00'),
('lt-010','u-cst-005',340,'EARN','Points earned from stay res-003','res-003','2026-03-01 12:00:00'),
('lt-011','u-cst-002',100,'EARN','Points earned from stay res-029','res-029','2025-07-17 13:00:00'),
('lt-012','u-cst-002',100,'EARN','Points earned from stay res-020','res-020','2025-11-09 13:00:00'),
('lt-013','u-cst-002',120,'EARN','Points earned from stay res-012','res-012','2026-01-06 13:00:00'),
('lt-014','u-cst-009',150,'EARN','Points earned from stay res-030','res-030','2025-07-30 13:00:00'),
('lt-015','u-cst-009',200,'EARN','Points earned from stay res-009','res-009','2026-02-16 13:00:00'),
('lt-016','u-cst-009',100,'EARN','Points earned from stay res-019','res-019','2025-12-14 13:00:00'),
('lt-017','u-cst-004',150,'EARN','Points earned from stay res-027','res-027','2025-08-10 13:00:00'),
('lt-018','u-cst-004',120,'EARN','Points earned from stay res-008','res-008','2026-02-09 13:00:00'),
('lt-019','u-cst-007',120,'EARN','Points earned from stay res-015','res-015','2026-01-29 13:00:00'),
('lt-020','u-cst-008', 80,'EARN','Points earned from stay res-021','res-021','2025-11-18 13:00:00'),
('lt-021','u-cst-008',190,'EARN','Points earned from stay res-011','res-011','2026-02-27 13:00:00'),
('lt-022','u-cst-010',90, 'EARN','Points earned from stay res-024','res-024','2025-10-23 13:00:00');
