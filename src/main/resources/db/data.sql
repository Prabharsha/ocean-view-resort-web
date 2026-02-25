-- ═══════════════════════════════════════════════════════
-- Ocean View Resort - Sample Data
-- ═══════════════════════════════════════════════════════
-- All passwords are BCrypt hashed (strength 12).
-- Plaintext passwords for testing:
--   admin   → Admin@123
--   staff1  → Staff@123
--   customer1 → Customer@123
-- ═══════════════════════════════════════════════════════

-- ───────────────────────────────────────────────────────
-- USERS (3 sample users)
-- ───────────────────────────────────────────────────────
-- Password: Admin@123   → BCrypt hash
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active)
VALUES
('u-admin-001', 'admin', '$2a$12$LJ3m4ys3uz2YHR.hCyqOYuGHpGCkb8b1gP1QkF5a0NYTVDxmahKG6',
 'John', 'Manager', 'admin@oceanview.com', '+441234567890', 'MANAGER', TRUE);

-- Password: Staff@123   → BCrypt hash
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active)
VALUES
('u-staff-001', 'staff1', '$2a$12$i8TZOqzr/G3CQkvh3ufYQObVaV8F6CIbRxRs5RxDJeS2KYoT2sCri',
 'Jane', 'Receptionist', 'staff1@oceanview.com', '+441234567891', 'STAFF', TRUE);

-- Password: Customer@123 → BCrypt hash
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active)
VALUES
('u-cust-001', 'customer1', '$2a$12$3RhSxd9VnGs4AEAqnB9Q4OqFYvGABnxmXV9u.0dULh5pI7K9dR8q.',
 'Alice', 'Guest', 'customer1@example.com', '+441234567892', 'CUSTOMER', TRUE);

-- ───────────────────────────────────────────────────────
-- CUSTOMERS (linked to customer user)
-- ───────────────────────────────────────────────────────
INSERT INTO customers (id, user_id, address, loyalty_points)
VALUES
('c-cust-001', 'u-cust-001', '42 High Street, Cardiff, CF10 1AE, UK', 150);

-- ───────────────────────────────────────────────────────
-- ROOMS (10 rooms)
-- ───────────────────────────────────────────────────────
INSERT INTO rooms (id, room_number, room_type, floor_number, capacity, rate_per_night, description, is_available, amenities) VALUES
('r-001', '101', 'STANDARD',  1, 2, 100.00, 'Cozy standard room with ocean view.',          TRUE, '["WiFi","TV","Mini Bar","Air Conditioning"]'),
('r-002', '102', 'STANDARD',  1, 2, 100.00, 'Standard room with garden view.',               TRUE, '["WiFi","TV","Mini Bar","Air Conditioning"]'),
('r-003', '103', 'STANDARD',  1, 3, 120.00, 'Spacious standard room for families.',           TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Crib"]'),
('r-004', '201', 'DELUXE',    2, 2, 180.00, 'Deluxe room with panoramic ocean view.',         TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Jacuzzi"]'),
('r-005', '202', 'DELUXE',    2, 2, 180.00, 'Deluxe room with king-size bed.',                TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony"]'),
('r-006', '203', 'DELUXE',    2, 3, 200.00, 'Deluxe family room with extra space.',           TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Sofa Bed"]'),
('r-007', '301', 'SUITE',     3, 4, 320.00, 'Luxury suite with separate living area.',        TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Jacuzzi","Kitchenette"]'),
('r-008', '302', 'SUITE',     3, 4, 320.00, 'Oceanfront suite with premium amenities.',       TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Balcony","Jacuzzi","Kitchenette"]'),
('r-009', '401', 'PENTHOUSE', 4, 6, 550.00, 'Penthouse with 360-degree ocean views.',         TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Rooftop Terrace","Jacuzzi","Kitchenette","Butler Service"]'),
('r-010', '402', 'PENTHOUSE', 4, 6, 550.00, 'Presidential penthouse with private pool.',      TRUE, '["WiFi","TV","Mini Bar","Air Conditioning","Private Pool","Rooftop Terrace","Jacuzzi","Kitchen","Butler Service"]');

-- ───────────────────────────────────────────────────────
-- RESERVATIONS (5 sample reservations in various states)
-- ───────────────────────────────────────────────────────
INSERT INTO reservations (id, reservation_number, customer_id, room_id, staff_id, guest_name, guest_address, guest_contact, check_in_date, check_out_date, num_guests, special_requests, status) VALUES
('res-001', 'OVR-2026-000001', 'c-cust-001', 'r-001', 'u-staff-001',
 'Alice Guest', '42 High Street, Cardiff', '+441234567892',
 '2026-03-01', '2026-03-04', 2, 'Late check-in requested.', 'CONFIRMED'),

('res-002', 'OVR-2026-000002', 'c-cust-001', 'r-004', 'u-staff-001',
 'Alice Guest', '42 High Street, Cardiff', '+441234567892',
 '2026-03-10', '2026-03-15', 2, 'Anniversary – champagne in room please.', 'PENDING'),

('res-003', 'OVR-2026-000003', 'c-cust-001', 'r-007', NULL,
 'Alice Guest', '42 High Street, Cardiff', '+441234567892',
 '2026-02-20', '2026-02-24', 4, NULL, 'CHECKED_IN'),

('res-004', 'OVR-2026-000004', 'c-cust-001', 'r-002', 'u-staff-001',
 'Alice Guest', '42 High Street, Cardiff', '+441234567892',
 '2026-01-10', '2026-01-13', 1, NULL, 'CHECKED_OUT'),

('res-005', 'OVR-2026-000005', 'c-cust-001', 'r-005', NULL,
 'Alice Guest', '42 High Street, Cardiff', '+441234567892',
 '2026-04-01', '2026-04-03', 2, 'Airport transfer needed.', 'CANCELLED');

-- ───────────────────────────────────────────────────────
-- BILLS (for checked-out & checked-in reservations)
-- ───────────────────────────────────────────────────────
INSERT INTO bills (id, reservation_id, num_nights, room_rate, subtotal, tax_rate, tax_amount, discount_amount, total_amount, payment_status) VALUES
('b-001', 'res-003', 4, 320.00, 1280.00, 10.00, 128.00, 0.00, 1408.00, 'UNPAID'),
('b-002', 'res-004', 3, 100.00,  300.00, 10.00,  30.00, 0.00,  330.00, 'PAID');

-- ───────────────────────────────────────────────────────
-- PAYMENTS (for paid bill)
-- ───────────────────────────────────────────────────────
INSERT INTO payments (id, bill_id, reservation_id, amount_paid, payment_method, transaction_reference, processed_by, notes) VALUES
('p-001', 'b-002', 'res-004', 330.00, 'CARD', 'TXN-20260113-001', 'u-staff-001', 'Full payment received on checkout.');
