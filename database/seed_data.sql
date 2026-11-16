-- ============================================================================
-- Scenario 27: Seed Data for Gym Membership Management & Activity Analysis
-- Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
-- Velalar College of Engineering and Technology, Erode - 12
-- ============================================================================

-- 1. Insert Membership Plans
INSERT INTO membership_plans (plan_id, plan_name, duration_months, fee, facilities_offered) VALUES
(1, 'Bronze Monthly', 1, 1500.00, 'Gym Floor, Cardio Zone, Locker Room'),
(2, 'Silver Quarterly', 3, 4000.00, 'Gym Floor, Cardio Zone, Locker Room, Steam Bath (1x/mo)'),
(3, 'Gold Half-Yearly', 6, 7500.00, 'All Equipment, Steam Bath, Diet Consultation, Locker'),
(4, 'Platinum Annual', 12, 13500.00, 'All Access, Unlimited Steam Bath, Dietician Support, Guest Passes'),
(5, 'Weekend Warrior', 1, 1000.00, 'Access on Saturdays & Sundays only, Locker Room');

-- 2. Insert Trainers
INSERT INTO trainers (trainer_id, name, specialization, contact_number, email, experience_years, hourly_rate) VALUES
(1, 'Rajesh Kumar', 'Strength & Powerlifting', '9876543210', 'rajesh.k@fitzone.com', 7, 500.00),
(2, 'Ananya Sharma', 'Yoga & Functional Mobility', '9876543211', 'ananya.s@fitzone.com', 5, 450.00),
(3, 'Karthik Raja', 'CrossFit & HIIT', '9876543212', 'karthik.r@fitzone.com', 4, 400.00),
(4, 'Priya Mohan', 'Cardio & Weight Management', '9876543213', 'priya.m@fitzone.com', 6, 450.00),
(5, 'Vikram Chandran', 'Bodybuilding & Nutrition', '9876543214', 'vikram.c@fitzone.com', 8, 600.00);

-- 3. Insert Members (20 members)
INSERT INTO members (member_id, name, age, gender, contact_number, email, registration_date, status) VALUES
(1, 'Arun Prasath', 24, 'Male', '9123456780', 'arun.p@gmail.com', '2026-08-01', 'Active'),
(2, 'Divya Bharathi', 22, 'Female', '9123456781', 'divya.b@gmail.com', '2026-08-15', 'Active'),
(3, 'Senthil Nathan', 35, 'Male', '9123456782', 'senthil.n@gmail.com', '2026-09-01', 'Active'),
(4, 'Kavitha Ramasamy', 28, 'Female', '9123456783', 'kavitha.r@gmail.com', '2026-09-10', 'Active'),
(5, 'Manoj Kumar', 21, 'Male', '9123456784', 'manoj.k@gmail.com', '2026-09-15', 'Active'),
(6, 'Deepa Sundaram', 30, 'Female', '9123456785', 'deepa.s@gmail.com', '2026-09-20', 'Active'),
(7, 'Gowtham Raj', 26, 'Male', '9123456786', 'gowtham.r@gmail.com', '2026-10-01', 'Active'),
(8, 'Sneha Krishnan', 23, 'Female', '9123456787', 'sneha.k@gmail.com', '2026-10-05', 'Active'),
(9, 'Praveen Vijay', 29, 'Male', '9123456788', 'praveen.v@gmail.com', '2026-10-10', 'Active'),
(10, 'Revathi S', 32, 'Female', '9123456789', 'revathi.s@gmail.com', '2026-10-12', 'Active'),
(11, 'Harish Balaji', 20, 'Male', '9123456790', 'harish.b@gmail.com', '2026-10-15', 'Active'),
(12, 'Meenakshi N', 41, 'Female', '9123456791', 'meenakshi.n@gmail.com', '2026-10-18', 'Active'),
(13, 'Suresh Babu', 45, 'Male', '9123456792', 'suresh.b@gmail.com', '2026-10-20', 'Active'),
(14, 'Nandhini Devi', 25, 'Female', '9123456793', 'nandhini.d@gmail.com', '2026-10-25', 'Active'),
(15, 'Vigneshwaran M', 27, 'Male', '9123456794', 'vicky.m@gmail.com', '2026-10-28', 'Active'),
(16, 'Swetha Anand', 23, 'Female', '9123456795', 'swetha.a@gmail.com', '2026-11-01', 'Active'),
(17, 'Kishore Kumar', 34, 'Male', '9123456796', 'kishore.k@gmail.com', '2026-11-02', 'Active'),
(18, 'Aishwarya R', 27, 'Female', '9123456797', 'aishu.r@gmail.com', '2026-11-03', 'Active'),
(19, 'Dinesh Karthik', 22, 'Male', '9123456798', 'dinesh.k@gmail.com', '2026-11-05', 'Active'),
(20, 'Bhavani Shankar', 52, 'Male', '9123456799', 'bhavani.s@gmail.com', '2026-11-08', 'Active');

-- 4. Insert Subscriptions (Realistic status: Active, Expiring Soon, Expired)
-- Current reference date: Nov 2026
INSERT INTO member_subscriptions (subscription_id, member_id, plan_id, start_date, expiry_date, amount_paid, payment_status) VALUES
(1, 1, 4, '2026-08-01', '2027-08-01', 13500.00, 'Paid'),   -- Active Annual
(2, 2, 2, '2026-08-15', '2026-11-15', 4000.00, 'Paid'),    -- Expired recently
(3, 3, 3, '2026-09-01', '2027-03-01', 7500.00, 'Paid'),    -- Active 6 Months
(4, 4, 1, '2026-10-20', '2026-11-20', 1500.00, 'Paid'),    -- Expiring Soon (in ~4 days)
(5, 5, 2, '2026-09-15', '2026-12-15', 4000.00, 'Paid'),    -- Active Quarterly
(6, 6, 3, '2026-09-20', '2027-03-20', 7500.00, 'Paid'),    -- Active
(7, 7, 1, '2026-10-18', '2026-11-18', 1500.00, 'Paid'),    -- Expiring Soon (in ~2 days)
(8, 8, 4, '2026-10-05', '2027-10-05', 13500.00, 'Paid'),   -- Active Annual
(9, 9, 2, '2026-10-10', '2027-01-10', 4000.00, 'Paid'),    -- Active
(10, 10, 5, '2026-10-12', '2026-11-12', 1000.00, 'Paid'),  -- Expired
(11, 11, 1, '2026-10-22', '2026-11-22', 1500.00, 'Paid'),  -- Expiring Soon (in ~6 days)
(12, 12, 3, '2026-10-18', '2027-04-18', 7500.00, 'Paid'),  -- Active
(13, 13, 2, '2026-10-20', '2027-01-20', 4000.00, 'Paid'),  -- Active
(14, 14, 4, '2026-10-25', '2027-10-25', 13500.00, 'Paid'),  -- Active Annual
(15, 15, 1, '2026-10-28', '2026-11-28', 1500.00, 'Paid'),  -- Active
(16, 16, 2, '2026-11-01', '2027-02-01', 4000.00, 'Paid'),  -- Active
(17, 17, 3, '2026-11-02', '2027-05-02', 7500.00, 'Paid'),  -- Active
(18, 18, 1, '2026-11-03', '2026-12-03', 1500.00, 'Paid'),  -- Active
(19, 19, 4, '2026-11-05', '2027-11-05', 13500.00, 'Paid'),  -- Active Annual
(20, 20, 5, '2026-11-08', '2026-12-08', 1000.00, 'Paid');  -- Active Weekend

-- 5. Insert Trainer Assignments
INSERT INTO trainer_assignments (assignment_id, member_id, trainer_id, assigned_date, fitness_goal, status) VALUES
(1, 1, 1, '2026-08-02', 'Powerlifting & Bench Max', 'Active'),
(2, 2, 4, '2026-08-16', 'Weight Loss (5kg target)', 'Completed'),
(3, 3, 5, '2026-09-02', 'Bodybuilding Hypertrophy', 'Active'),
(4, 4, 2, '2026-09-11', 'Core Strength & Yoga', 'Active'),
(5, 5, 3, '2026-09-16', 'Athletic Conditioning & Agility', 'Active'),
(6, 6, 4, '2026-09-22', 'Post-pregnancy Fitness & Cardio', 'Active'),
(7, 7, 1, '2026-10-02', 'Strength Progression & Squat Form', 'Active'),
(8, 8, 2, '2026-10-06', 'Flexibility & Posture Correction', 'Active'),
(9, 9, 3, '2026-10-11', 'CrossFit Stamina', 'Active'),
(10, 11, 1, '2026-10-23', 'Deadlift & Upper Body Hypertrophy', 'Active'),
(11, 14, 5, '2026-10-26', 'Competition Prep Physique', 'Active'),
(12, 17, 5, '2026-11-03', 'Lean Mass Building', 'Active'),
(13, 19, 3, '2026-11-06', 'High-Intensity Fat Burn', 'Active');

-- 6. Insert Attendance Records (Covering morning and evening shifts, different workout types)
INSERT INTO attendance (attendance_id, member_id, attendance_date, check_in_time, check_out_time, workout_type) VALUES
(1, 1, '2026-11-01', '06:15:00', '07:45:00', 'Weight Training'),
(2, 3, '2026-11-01', '06:30:00', '08:00:00', 'Weight Training'),
(3, 5, '2026-11-01', '07:00:00', '08:15:00', 'CrossFit'),
(4, 7, '2026-11-01', '07:15:00', '08:30:00', 'Weight Training'),
(5, 2, '2026-11-01', '17:30:00', '18:45:00', 'Cardio'),
(6, 4, '2026-11-01', '18:00:00', '19:15:00', 'Yoga'),
(7, 8, '2026-11-01', '18:15:00', '19:30:00', 'Yoga'),
(8, 9, '2026-11-01', '19:00:00', '20:30:00', 'HIIT'),
(9, 14, '2026-11-01', '19:15:00', '20:45:00', 'Weight Training'),

(10, 1, '2026-11-02', '06:20:00', '07:50:00', 'Weight Training'),
(11, 5, '2026-11-02', '06:45:00', '08:00:00', 'CrossFit'),
(12, 11, '2026-11-02', '07:10:00', '08:20:00', 'Weight Training'),
(13, 13, '2026-11-02', '07:30:00', '08:30:00', 'Cardio'),
(14, 4, '2026-11-02', '17:45:00', '19:00:00', 'Yoga'),
(15, 6, '2026-11-02', '18:00:00', '19:15:00', 'Cardio'),
(16, 15, '2026-11-02', '18:30:00', '19:45:00', 'Weight Training'),
(17, 17, '2026-11-02', '19:00:00', '20:30:00', 'Weight Training'),

(18, 1, '2026-11-03', '06:10:00', '07:40:00', 'Weight Training'),
(19, 3, '2026-11-03', '06:35:00', '08:05:00', 'Weight Training'),
(20, 7, '2026-11-03', '07:05:00', '08:25:00', 'Weight Training'),
(21, 8, '2026-11-03', '17:30:00', '18:45:00', 'Yoga'),
(22, 9, '2026-11-03', '18:15:00', '19:45:00', 'HIIT'),
(23, 12, '2026-11-03', '18:30:00', '19:30:00', 'Cardio'),
(24, 16, '2026-11-03', '19:00:00', '20:15:00', 'CrossFit'),
(25, 19, '2026-11-03', '19:30:00', '20:45:00', 'HIIT'),

(26, 1, '2026-11-04', '06:30:00', '07:55:00', 'Weight Training'),
(27, 5, '2026-11-04', '06:50:00', '08:10:00', 'CrossFit'),
(28, 11, '2026-11-04', '07:15:00', '08:30:00', 'Weight Training'),
(29, 2, '2026-11-04', '17:30:00', '18:30:00', 'Cardio'),
(30, 6, '2026-11-04', '18:00:00', '19:10:00', 'Cardio'),
(31, 14, '2026-11-04', '18:45:00', '20:15:00', 'Weight Training'),
(32, 18, '2026-11-04', '19:15:00', '20:15:00', 'Cardio'),

(33, 1, '2026-11-05', '06:15:00', '07:45:00', 'Weight Training'),
(34, 3, '2026-11-05', '06:30:00', '08:00:00', 'Weight Training'),
(35, 7, '2026-11-05', '07:00:00', '08:15:00', 'Weight Training'),
(36, 13, '2026-11-05', '07:30:00', '08:45:00', 'Cardio'),
(37, 4, '2026-11-05', '17:45:00', '19:00:00', 'Yoga'),
(38, 8, '2026-11-05', '18:00:00', '19:15:00', 'Yoga'),
(39, 9, '2026-11-05', '18:30:00', '20:00:00', 'HIIT'),
(40, 15, '2026-11-05', '19:00:00', '20:15:00', 'Weight Training'),
(41, 17, '2026-11-05', '19:15:00', '20:45:00', 'Weight Training'),

(42, 10, '2026-11-07', '08:00:00', '09:30:00', 'General Workout'),
(43, 20, '2026-11-07', '08:30:00', '10:00:00', 'General Workout'),
(44, 1, '2026-11-07', '09:00:00', '10:30:00', 'Weight Training'),
(45, 5, '2026-11-07', '09:30:00', '11:00:00', 'CrossFit'),
(46, 14, '2026-11-07', '17:00:00', '18:30:00', 'Weight Training'),
(47, 19, '2026-11-07', '17:30:00', '19:00:00', 'HIIT'),

(48, 10, '2026-11-08', '08:15:00', '09:45:00', 'General Workout'),
(49, 20, '2026-11-08', '08:45:00', '10:15:00', 'General Workout'),
(50, 3, '2026-11-08', '09:15:00', '10:45:00', 'Weight Training'),

(51, 1, '2026-11-10', '06:15:00', '07:45:00', 'Weight Training'),
(52, 3, '2026-11-10', '06:30:00', '08:00:00', 'Weight Training'),
(53, 5, '2026-11-10', '07:00:00', '08:15:00', 'CrossFit'),
(54, 7, '2026-11-10', '07:15:00', '08:30:00', 'Weight Training'),
(55, 11, '2026-11-10', '07:30:00', '08:45:00', 'Weight Training'),
(56, 4, '2026-11-10', '17:45:00', '19:00:00', 'Yoga'),
(57, 8, '2026-11-10', '18:00:00', '19:15:00', 'Yoga'),
(58, 9, '2026-11-10', '18:30:00', '20:00:00', 'HIIT'),
(59, 14, '2026-11-10', '19:00:00', '20:30:00', 'Weight Training'),
(60, 16, '2026-11-10', '19:15:00', '20:30:00', 'CrossFit'),
(61, 17, '2026-11-10', '19:30:00', '21:00:00', 'Weight Training'),
(62, 19, '2026-11-10', '19:45:00', '21:00:00', 'HIIT');
