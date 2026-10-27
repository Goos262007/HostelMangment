-- ====================================================================
-- HOSTEL MANAGEMENT SYSTEM DATABASE SCHEMA
-- B.Tech CSE Project-Based Learning (PBL)
-- Target RDBMS: MySQL 8.0+
-- Database: hostel_db
-- ====================================================================

CREATE DATABASE IF NOT EXISTS hostel_db;
USE hostel_db;

-- Drop tables in reverse foreign-key order
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS fees;
DROP TABLE IF EXISTS allocations;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

-- 1. USERS TABLE (Admin & Staff Authentication)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ROOMS TABLE
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    room_type ENUM('Single', 'Double', 'Triple', 'Deluxe') NOT NULL DEFAULT 'Double',
    capacity INT NOT NULL DEFAULT 2,
    occupied_beds INT NOT NULL DEFAULT 0,
    monthly_fee DECIMAL(10, 2) NOT NULL DEFAULT 6500.00,
    status ENUM('Available', 'Occupied', 'Maintenance') NOT NULL DEFAULT 'Available',
    description VARCHAR(255) DEFAULT 'Standard furnished student room',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. STUDENTS TABLE
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(30) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    year_of_study VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    parent_contact VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    assigned_room_id INT NULL,
    status ENUM('Active', 'Inactive', 'Graduated') NOT NULL DEFAULT 'Active',
    admission_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_room_id) REFERENCES rooms(id) ON DELETE SET NULL
);

-- 4. ALLOCATIONS TABLE (Audit trail of room assignments)
CREATE TABLE allocations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    allocation_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    vacating_date DATE NULL,
    status ENUM('Active', 'Vacated') NOT NULL DEFAULT 'Active',
    remarks VARCHAR(255) DEFAULT 'Regular semester allotment',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- 5. FEES TABLE
CREATE TABLE fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_no VARCHAR(40) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    pending_amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    payment_method ENUM('Cash', 'UPI', 'Net Banking', 'Credit/Debit Card', 'Cheque') NOT NULL DEFAULT 'UPI',
    transaction_id VARCHAR(100) DEFAULT 'N/A',
    payment_status ENUM('Paid', 'Partially Paid', 'Pending') NOT NULL DEFAULT 'Pending',
    remarks VARCHAR(255) DEFAULT 'Semester Hostel & Mess Fee',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 6. COMPLAINTS TABLE
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(30) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('Plumbing', 'Electrical', 'Housekeeping', 'Wi-Fi', 'Mess', 'Other') NOT NULL,
    priority ENUM('Low', 'Medium', 'High') NOT NULL DEFAULT 'Medium',
    status ENUM('Pending', 'In Progress', 'Resolved') NOT NULL DEFAULT 'Pending',
    reported_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    resolved_date DATE NULL,
    admin_remarks VARCHAR(255) DEFAULT 'Awaiting inspection',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- ====================================================================
-- SEED DATA FOR DEMONSTRATION & PBL EVALUATION
-- ====================================================================

-- Default Admin User
INSERT INTO users (username, password, full_name, email, role)
VALUES ('admin', 'admin123', 'Prof. R. K. Sharma (Chief Warden)', 'warden@hostel.edu', 'CHIEF_WARDEN');

-- Rooms
INSERT INTO rooms (room_number, room_type, capacity, occupied_beds, monthly_fee, status, description) VALUES
('A-101', 'Single', 1, 1, 9500.00, 'Occupied', 'Deluxe single occupancy with attached washroom'),
('A-102', 'Single', 1, 0, 9500.00, 'Available', 'Single occupancy room with study desk and balcony'),
('B-201', 'Double', 2, 2, 7000.00, 'Occupied', 'Spacious double bed room with central air cooling'),
('B-202', 'Double', 2, 1, 7000.00, 'Available', 'Double sharing room on 2nd floor, facing green lawn'),
('B-203', 'Double', 2, 0, 7000.00, 'Maintenance', 'Under repainting and electrical fixture servicing'),
('C-301', 'Triple', 3, 2, 5500.00, 'Available', 'Triple sharing economy room with separate wardrobes'),
('C-302', 'Triple', 3, 3, 5500.00, 'Occupied', 'Triple sharing room near study lounge'),
('D-401', 'Deluxe', 2, 1, 11000.00, 'Available', 'Premium AC room with mini fridge and high-speed LAN');

-- Students
INSERT INTO students (student_id, full_name, roll_number, department, year_of_study, dob, gender, phone, email, parent_contact, address, assigned_room_id, status) VALUES
('STU-2024-001', 'Aarav Mehta', '21CSE014', 'Computer Science', '3rd Year', '2003-05-12', 'Male', '+91 98765 43210', 'aarav.mehta@college.edu', '+91 98765 00001', 'Flat 402, Green Valley Apts, Pune, Maharashtra', 1, 'Active'),
('STU-2024-002', 'Priya Sharma', '22IT045', 'Information Technology', '2nd Year', '2004-08-22', 'Female', '+91 98765 43211', 'priya.sharma@college.edu', '+91 98765 00002', '12B, Rose Villa, Civil Lines, Jaipur, Rajasthan', 3, 'Active'),
('STU-2024-003', 'Rohan Verma', '21ECE028', 'Electronics & Comm', '3rd Year', '2003-11-05', 'Male', '+91 98765 43212', 'rohan.verma@college.edu', '+91 98765 00003', '78 Sector 14, Chandigarh, Punjab', 3, 'Active'),
('STU-2024-004', 'Ananya Patel', '23CSE089', 'Computer Science', '1st Year', '2005-02-18', 'Female', '+91 98765 43213', 'ananya.patel@college.edu', '+91 98765 00004', 'Plot 55, Royal Residency, Ahmedabad, Gujarat', 4, 'Active'),
('STU-2024-005', 'Vikram Singh', '20ME003', 'Mechanical', '4th Year', '2002-09-30', 'Male', '+91 98765 43214', 'vikram.singh@college.edu', '+91 98765 00005', 'House 14, Lake View Road, Bhopal, MP', 6, 'Active'),
('STU-2024-006', 'Sneha Kulkarni', '22CSE056', 'Computer Science', '2nd Year', '2004-04-14', 'Female', '+91 98765 43215', 'sneha.kulkarni@college.edu', '+91 98765 00006', '45 Shivajinagar, Kolhapur, Maharashtra', 6, 'Active'),
('STU-2024-007', 'Kunal Reddy', '21CIV012', 'Civil', '3rd Year', '2003-07-25', 'Male', '+91 98765 43216', 'kunal.reddy@college.edu', '+91 98765 00007', 'H.No 3-4-102, Banjara Hills, Hyderabad, Telangana', 7, 'Active'),
('STU-2024-008', 'Tanvi Deshmukh', '23IT019', 'Information Technology', '1st Year', '2005-10-09', 'Female', '+91 98765 43217', 'tanvi.d@college.edu', '+91 98765 00008', '204 Sunrise Towers, Nagpur, Maharashtra', 7, 'Active'),
('STU-2024-009', 'Aditya Nair', '21ECE091', 'Electronics & Comm', '3rd Year', '2003-01-15', 'Male', '+91 98765 43218', 'aditya.nair@college.edu', '+91 98765 00009', 'Palace Road, Kowdiar, Trivandrum, Kerala', 7, 'Active'),
('STU-2024-010', 'Ishaan Gupta', '23CSE110', 'Computer Science', '1st Year', '2005-06-20', 'Male', '+91 98765 43219', 'ishaan.gupta@college.edu', '+91 98765 00010', 'B-34 Model Town, New Delhi', 8, 'Active'),
('STU-2024-011', 'Neha Joshi', '22ECE034', 'Electronics & Comm', '2nd Year', '2004-12-03', 'Female', '+91 98765 43220', 'neha.joshi@college.edu', '+91 98765 00011', '33 Subhash Marg, Indore, MP', NULL, 'Active'),
('STU-2024-012', 'Arjun Saxena', '20CSE005', 'Computer Science', '4th Year', '2002-03-11', 'Male', '+91 98765 43221', 'arjun.saxena@college.edu', '+91 98765 00012', '67 Gomti Nagar, Lucknow, UP', NULL, 'Active');

-- Allocations
INSERT INTO allocations (student_id, room_id, allocation_date, status, remarks) VALUES
(1, 1, '2024-08-01', 'Active', 'Allotted Single Room A-101'),
(2, 3, '2024-08-01', 'Active', 'Allotted Double Room B-201'),
(3, 3, '2024-08-02', 'Active', 'Allotted Double Room B-201'),
(4, 4, '2024-08-03', 'Active', 'Allotted Double Room B-202'),
(5, 6, '2024-08-01', 'Active', 'Allotted Triple Room C-301'),
(6, 6, '2024-08-02', 'Active', 'Allotted Triple Room C-301'),
(7, 7, '2024-08-01', 'Active', 'Allotted Triple Room C-302'),
(8, 7, '2024-08-01', 'Active', 'Allotted Triple Room C-302'),
(9, 7, '2024-08-02', 'Active', 'Allotted Triple Room C-302'),
(10, 8, '2024-08-05', 'Active', 'Allotted Deluxe Room D-401');

-- Fee Payments
INSERT INTO fees (receipt_no, student_id, total_amount, amount_paid, pending_amount, payment_date, payment_method, transaction_id, payment_status, remarks) VALUES
('REC-2024-001', 1, 57000.00, 57000.00, 0.00, '2024-08-05', 'UPI', 'UPI/2024/908129841', 'Paid', 'Full semester fee paid'),
('REC-2024-002', 2, 42000.00, 42000.00, 0.00, '2024-08-06', 'Net Banking', 'HDFC-NB-8823192', 'Paid', 'Annual maintenance and mess paid'),
('REC-2024-003', 3, 42000.00, 25000.00, 17000.00, '2024-08-07', 'UPI', 'UPI/2024/778102931', 'Partially Paid', '1st installment paid, balance due in 30 days'),
('REC-2024-004', 4, 42000.00, 42000.00, 0.00, '2024-08-08', 'Credit/Debit Card', 'TXN-CARD-449102', 'Paid', 'Card swipe at accounts counter'),
('REC-2024-005', 5, 33000.00, 33000.00, 0.00, '2024-08-09', 'UPI', 'UPI/2024/339102941', 'Paid', 'Semester 7 accommodation fees'),
('REC-2024-006', 6, 33000.00, 20000.00, 13000.00, '2024-08-10', 'Net Banking', 'ICICI-NB-1092834', 'Partially Paid', 'Part payment received'),
('REC-2024-007', 7, 33000.00, 33000.00, 0.00, '2024-08-10', 'Cash', 'CASH-REC-1082', 'Paid', 'Cash deposited at hostel cashier'),
('REC-2024-008', 8, 33000.00, 0.00, 33000.00, '2024-08-12', 'Net Banking', 'PENDING', 'Pending', 'Fee invoice generated - payment reminder sent'),
('REC-2024-009', 9, 33000.00, 33000.00, 0.00, '2024-08-12', 'UPI', 'UPI/2024/119284019', 'Paid', 'Full clearance issued'),
('REC-2024-010', 10, 66000.00, 40000.00, 26000.00, '2024-08-14', 'Cheque', 'CHQ-SBI-991204', 'Partially Paid', 'Cheque cleared, 2nd installment pending');

-- Complaints
INSERT INTO complaints (complaint_id, student_id, title, description, category, priority, status, reported_date, resolved_date, admin_remarks) VALUES
('CMP-2024-101', 1, 'Bathroom tap leaking in A-101', 'The washbasin tap has a steady drip causing water wastage.', 'Plumbing', 'Low', 'Resolved', '2024-08-15', '2024-08-16', 'Washer replaced by hostel plumber.'),
('CMP-2024-102', 4, 'Wi-Fi router frequent disconnection', '3rd floor wing B router signals drop every 15 minutes during peak hours.', 'Wi-Fi', 'High', 'In Progress', '2024-08-18', NULL, 'IT team contacted ISP and testing DNS gateway.'),
('CMP-2024-103', 2, 'Ceiling fan making clicking noise', 'Fan regulator or bearing in B-201 needs lubrication.', 'Electrical', 'Medium', 'Pending', '2024-08-20', NULL, 'Scheduled for electrician visit tomorrow.'),
('CMP-2024-104', 5, 'Daily corridor housekeeping requested', 'Dust accumulation on 3rd floor staircase after monsoon rains.', 'Housekeeping', 'Low', 'Resolved', '2024-08-17', '2024-08-18', 'Sanitation supervisor deployed cleaning staff.'),
('CMP-2024-105', 7, 'Reading lamp socket sparking in C-302', 'Power socket near bed 2 sparks when plugging laptop charger.', 'Electrical', 'High', 'Pending', '2024-08-22', NULL, 'Urgent electrician inspection assigned.');
