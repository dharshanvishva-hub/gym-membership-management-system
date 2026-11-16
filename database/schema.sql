-- ============================================================================
-- Scenario 27: Gym Membership Management and Fitness Activity Analysis System
-- Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
-- Velalar College of Engineering and Technology, Erode - 12
-- Course: 25ITT31 - Database Management Systems
-- ============================================================================

-- Drop views if they exist
DROP VIEW IF EXISTS vw_revenue_by_plan;
DROP VIEW IF EXISTS vw_trainer_workload;
DROP VIEW IF EXISTS vw_attendance_summary;
DROP VIEW IF EXISTS vw_expiring_soon;
DROP VIEW IF EXISTS vw_active_memberships;

-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS trainer_assignments;
DROP TABLE IF EXISTS member_subscriptions;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS membership_plans;
DROP TABLE IF EXISTS members;

-- 1. Members Table
CREATE TABLE members (
    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 14 AND age <= 90),
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
    contact_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    registration_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active', 'Inactive', 'Suspended'))
);

-- 2. Membership Plans Table
CREATE TABLE membership_plans (
    plan_id INTEGER PRIMARY KEY AUTOINCREMENT,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    duration_months INTEGER NOT NULL CHECK (duration_months > 0),
    fee DECIMAL(10,2) NOT NULL CHECK (fee >= 0),
    facilities_offered TEXT NOT NULL
);

-- 3. Trainers Table
CREATE TABLE trainers (
    trainer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    experience_years INTEGER NOT NULL DEFAULT 1 CHECK (experience_years >= 0),
    hourly_rate DECIMAL(10,2) NOT NULL DEFAULT 0.0 CHECK (hourly_rate >= 0)
);

-- 4. Member Subscriptions Table (Assigns plans to members)
CREATE TABLE member_subscriptions (
    subscription_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    plan_id INTEGER NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'Paid' CHECK (payment_status IN ('Paid', 'Pending', 'Partially Paid')),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES membership_plans(plan_id) ON DELETE RESTRICT
);

-- 5. Trainer Assignments Table (Associates members with trainers)
CREATE TABLE trainer_assignments (
    assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    trainer_id INTEGER NOT NULL,
    assigned_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    fitness_goal VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active', 'Completed', 'Cancelled')),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE CASCADE
);

-- 6. Attendance Table (Records daily attendance)
CREATE TABLE attendance (
    attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    attendance_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    check_in_time TIME NOT NULL,
    check_out_time TIME,
    workout_type VARCHAR(50) NOT NULL DEFAULT 'General Workout',
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- ============================================================================
-- Database Views for Reporting & Analytics
-- ============================================================================

-- View 1: Active Memberships with Days Remaining
CREATE VIEW vw_active_memberships AS
SELECT 
    s.subscription_id,
    m.member_id,
    m.name AS member_name,
    m.contact_number,
    p.plan_name,
    s.start_date,
    s.expiry_date,
    s.amount_paid,
    CASE 
        WHEN date(s.expiry_date) < date('now') THEN 'Expired'
        WHEN date(s.expiry_date) BETWEEN date('now') AND date('now', '+7 days') THEN 'Expiring Soon'
        ELSE 'Active'
    END AS subscription_status,
    CAST((julianday(s.expiry_date) - julianday('now')) AS INTEGER) AS days_remaining
FROM member_subscriptions s
JOIN members m ON s.member_id = m.member_id
JOIN membership_plans p ON s.plan_id = p.plan_id;

-- View 2: Expiring Soon (Within 7 days)
CREATE VIEW vw_expiring_soon AS
SELECT 
    m.member_id,
    m.name AS member_name,
    m.contact_number,
    m.email,
    p.plan_name,
    s.expiry_date,
    CAST((julianday(s.expiry_date) - julianday('now')) AS INTEGER) AS days_left
FROM member_subscriptions s
JOIN members m ON s.member_id = m.member_id
JOIN membership_plans p ON s.plan_id = p.plan_id
WHERE date(s.expiry_date) BETWEEN date('now') AND date('now', '+7 days');

-- View 3: Attendance Summary per Member
CREATE VIEW vw_attendance_summary AS
SELECT 
    m.member_id,
    m.name AS member_name,
    COUNT(a.attendance_id) AS total_visits,
    MIN(a.attendance_date) AS first_visit_date,
    MAX(a.attendance_date) AS last_visit_date
FROM members m
LEFT JOIN attendance a ON m.member_id = a.member_id
GROUP BY m.member_id, m.name;

-- View 4: Trainer Workload
CREATE VIEW vw_trainer_workload AS
SELECT 
    t.trainer_id,
    t.name AS trainer_name,
    t.specialization,
    COUNT(ta.assignment_id) AS active_clients
FROM trainers t
LEFT JOIN trainer_assignments ta ON t.trainer_id = ta.trainer_id AND ta.status = 'Active'
GROUP BY t.trainer_id, t.name, t.specialization;

-- View 5: Revenue and Popularity by Plan
CREATE VIEW vw_revenue_by_plan AS
SELECT 
    p.plan_id,
    p.plan_name,
    p.duration_months,
    p.fee,
    COUNT(s.subscription_id) AS total_subscriptions,
    COALESCE(SUM(s.amount_paid), 0.0) AS total_revenue
FROM membership_plans p
LEFT JOIN member_subscriptions s ON p.plan_id = s.plan_id
GROUP BY p.plan_id, p.plan_name, p.duration_months, p.fee;
