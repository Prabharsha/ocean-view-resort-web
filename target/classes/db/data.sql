-- ═══════════════════════════════════════════════════════════════════════
-- Ocean View Resort – Seed Data
-- Run this AFTER schema.sql to populate demo data
-- Passwords are BCrypt hashed. Default: Manager@123, Staff@123, Customer@123
-- ═══════════════════════════════════════════════════════════════════════

USE ocean_view_resort;

-- ── MANAGER ──
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u001', 'pansilu', '$2a$12$LJ3m4ys3Gzf1JRqGCwYQFOkTz1Y6546q5hLB8PMpv7TzVq/vMhcIy', 'Pansilu', 'Kaushalya', 'pansilu@oceanview.lk', '+94771234567', 'MANAGER', TRUE);
INSERT INTO staff (id, department, employee_id) VALUES ('u001', 'Management', 'EMP001');
INSERT INTO managers (id) VALUES ('u001');

-- ── STAFF ──
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u002', 'staff1', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nimal', 'Perera', 'nimal@oceanview.lk', '+94772345678', 'STAFF', TRUE),
('u003', 'staff2', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Kumari', 'Silva', 'kumari@oceanview.lk', '+94773456789', 'STAFF', TRUE);
INSERT INTO staff (id, department, employee_id) VALUES ('u002', 'Front Desk', 'EMP002'), ('u003', 'Front Desk', 'EMP003');

-- ── CUSTOMERS ──
INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) VALUES
('u004', 'customer1', '$2a$12$TjLwCNYNh8gNFkBV7fS2W.i1EPUfS0QXXhNhO5p6kqjPjDBr7iyp.', 'Saman', 'Kumara', 'saman@email.com', '+94774567890', 'CUSTOMER', TRUE),
('u005', 'customer2', '$2a$12$TjLwCNYNh8gNFkBV7fS2W.i1EPUfS0QXXhNhO5p6kqjPjDBr7iyp.', 'Dilani', 'Fernando', 'dilani@email.com', '+94775678901', 'CUSTOMER', TRUE),
('u006', 'customer3', '$2a$12$TjLwCNYNh8gNFkBV7fS2W.i1EPUfS0QXXhNhO5p6kqjPjDBr7iyp.', 'Ruwan', 'Jayasinghe', 'ruwan@email.com', '+94776789012', 'CUSTOMER', TRUE);
INSERT INTO customers (id, address, loyalty_points) VALUES
('u004', '123 Galle Road, Colombo 03', 150),
('u005', '45 Kandy Road, Peradeniya', 75),
('u006', '789 Beach Road, Negombo', 200);

-- ── ROOMS ──
INSERT INTO rooms (id, room_number, room_type, floor_number, capacity, rate_per_night, description, is_available, amenities) VALUES
('r001', '101', 'STANDARD', 1, 2, 15000.00, 'Cozy standard room with ocean view balcony', TRUE, '["WiFi","AC","TV","Mini Bar"]'),
('r002', '102', 'STANDARD', 1, 2, 15000.00, 'Standard room with garden view', TRUE, '["WiFi","AC","TV"]'),
('r003', '201', 'DELUXE', 2, 3, 25000.00, 'Spacious deluxe room with panoramic ocean view', TRUE, '["WiFi","AC","TV","Mini Bar","Jacuzzi","Room Service"]'),
('r004', '202', 'DELUXE', 2, 3, 25000.00, 'Deluxe room with private terrace', TRUE, '["WiFi","AC","TV","Mini Bar","Terrace"]'),
('r005', '301', 'SUITE', 3, 4, 45000.00, 'Luxury suite with separate living area and ocean view', TRUE, '["WiFi","AC","TV","Mini Bar","Jacuzzi","Room Service","Butler"]'),
('r006', '302', 'SUITE', 3, 4, 45000.00, 'Premium suite with dining area', TRUE, '["WiFi","AC","TV","Mini Bar","Room Service","Kitchen"]'),
('r007', '401', 'PENTHOUSE', 4, 6, 85000.00, 'Presidential penthouse with 360° ocean views, private pool', TRUE, '["WiFi","AC","TV","Mini Bar","Jacuzzi","Room Service","Butler","Private Pool","Rooftop Terrace"]'),
('r008', '103', 'STANDARD', 1, 2, 12000.00, 'Economy standard room', TRUE, '["WiFi","AC","TV"]'),
('r009', '203', 'DELUXE', 2, 2, 22000.00, 'Compact deluxe room', TRUE, '["WiFi","AC","TV","Mini Bar"]'),
('r010', '303', 'SUITE', 3, 5, 50000.00, 'Family suite with two bedrooms', TRUE, '["WiFi","AC","TV","Mini Bar","Jacuzzi","Room Service","Kitchen"]'),
('r011', '104', 'STANDARD', 1, 2, 14000.00, 'Standard room with pool access', TRUE, '["WiFi","AC","TV","Pool Access"]'),
('r012', '204', 'DELUXE', 2, 3, 28000.00, 'Premium deluxe with ocean view', TRUE, '["WiFi","AC","TV","Mini Bar","Ocean View","Balcony"]');

-- ── SAMPLE RESERVATIONS ──
INSERT INTO reservations (id, reservation_number, customer_id, room_id, staff_id, guest_name, guest_contact, check_in_date, check_out_date, num_guests, status, special_requests) VALUES
('res001', 'OVR-2026-000001', 'u004', 'r003', 'u002', 'Saman Kumara', '+94774567890', '2026-03-10', '2026-03-14', 2, 'CONFIRMED', 'Late check-in after 8pm'),
('res002', 'OVR-2026-000002', 'u005', 'r005', 'u002', 'Dilani Fernando', '+94775678901', '2026-03-12', '2026-03-15', 3, 'PENDING', 'Extra pillows please'),
('res003', 'OVR-2026-000003', 'u006', 'r001', 'u003', 'Ruwan Jayasinghe', '+94776789012', '2026-03-01', '2026-03-05', 2, 'CHECKED_OUT', NULL),
('res004', 'OVR-2026-000004', 'u004', 'r007', 'u002', 'Saman Kumara', '+94774567890', '2026-03-20', '2026-03-25', 4, 'PENDING', 'Anniversary celebration - cake & flowers');

