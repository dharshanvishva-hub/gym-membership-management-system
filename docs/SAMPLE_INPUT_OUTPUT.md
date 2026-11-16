# Sample Inputs and Execution Outputs
## Gym Membership Management and Fitness Activity Analysis System

**Student Name:** V. Dharshan | **Reg No:** 732925ITR027 | **Class:** 25IT3A  
**Institution:** Velalar College of Engineering and Technology, Erode - 12  

---

## 1. Sample Member Registration & Input Validation

### Sample Input (Adding New Member)
```text
Enter Member Name: V. Dharshan
Enter Age: 20
Enter Gender (Male/Female/Other): Male
Enter Contact Number (10 digits): 9842123456
Enter Email Address: dharshan.v@fitzone.com
```

### System Output
```text
[SUCCESS] Member registered with ID: 21
```

### Sample Input (Boundary Value Validation Failure - Negative Age)
```text
Enter Member Name: Test User
Enter Age: 11
Enter Gender: Male
Enter Contact Number: 9876543210
Enter Email Address: test@gmail.com
```

### System Output
```text
[ERROR] Validation Failed: Member age must be between 14 and 90.
```

---

## 2. Sample Membership Subscription & Renewal

### Sample Input (Assign Plan to Member)
```text
Enter Member ID: 21
Enter Plan ID: 4
```

### System Output
```text
[SUCCESS] Subscription registered! Expiry Date: 2027-11-16 | Amount: ₹13500.00
```

---

## 3. Sample Attendance Logging

### Sample Input (Member Check-in)
```text
Enter Member ID: 1
Enter Workout Type: Weight Training
```

### System Output
```text
[SUCCESS] Check-in logged at 06:15:00 for Arun Prasath
```

### Sample Input (Member Check-out)
```text
Enter Member ID: 1
```

### System Output
```text
[SUCCESS] Check-out recorded successfully.
```

---

## 4. Sample Status Tracking & Renewal Alert Report

### System Output
```text
========================================================================================
                MEMBERSHIPS EXPIRING WITHIN 7 DAYS (RENEWAL ALERTS)             
========================================================================================
SubID  | Member Name          | Plan               | Expiry Date  | Days Left   
----------------------------------------------------------------------------------------
7      | Gowtham Raj          | Bronze Monthly     | 2026-11-18   | 2           
4      | Kavitha Ramasamy     | Bronze Monthly     | 2026-11-20   | 4           
11     | Harish Balaji        | Bronze Monthly     | 2026-11-22   | 6           
========================================================================================
```

---

## 5. Sample Financial Revenue Report (Java Stream Aggregation)

### System Output
```text
========================================================================================
                        FITNESS CENTRE REVENUE & SUBSCRIPTION REPORT                    
========================================================================================
SubID  | Member Name          | Plan Name          | Start Date   | Expiry Date  | Amount (₹)
----------------------------------------------------------------------------------------
1      | Arun Prasath         | Platinum Annual    | 2026-08-01   | 2027-08-01   |   13500.00
2      | Divya Bharathi       | Silver Quarterly   | 2026-08-15   | 2026-11-15   |    4000.00
3      | Senthil Nathan       | Gold Half-Yearly   | 2026-09-01   | 2027-03-01   |    7500.00
4      | Kavitha Ramasamy     | Bronze Monthly     | 2026-10-20   | 2026-11-20   |    1500.00
5      | Manoj Kumar          | Silver Quarterly   | 2026-09-15   | 2026-12-15   |    4000.00
6      | Deepa Sundaram       | Gold Half-Yearly   | 2026-09-20   | 2027-03-20   |    7500.00
7      | Gowtham Raj          | Bronze Monthly     | 2026-10-18   | 2026-11-18   |    1500.00
8      | Sneha Krishnan       | Platinum Annual    | 2026-10-05   | 2027-10-05   |   13500.00
9      | Praveen Vijay        | Silver Quarterly   | 2026-10-10   | 2027-01-10   |    4000.00
10     | Revathi S            | Weekend Warrior    | 2026-10-12   | 2026-11-12   |    1000.00
11     | Harish Balaji        | Bronze Monthly     | 2026-10-22   | 2026-11-22   |    1500.00
12     | Meenakshi N          | Gold Half-Yearly   | 2026-10-18   | 2027-04-18   |    7500.00
13     | Suresh Babu          | Silver Quarterly   | 2026-10-20   | 2027-01-20   |    4000.00
14     | Nandhini Devi        | Platinum Annual    | 2026-10-25   | 2027-10-25   |   13500.00
15     | Vigneshwaran M       | Bronze Monthly     | 2026-10-28   | 2026-11-28   |    1500.00
16     | Swetha Anand         | Silver Quarterly   | 2026-11-01   | 2027-02-01   |    4000.00
17     | Kishore Kumar        | Gold Half-Yearly   | 2026-11-02   | 2027-05-02   |    7500.00
18     | Aishwarya R          | Bronze Monthly     | 2026-11-03   | 2026-12-03   |    1500.00
19     | Dinesh Karthik       | Platinum Annual    | 2026-11-05   | 2027-11-05   |   13500.00
20     | Bhavani Shankar      | Weekend Warrior    | 2026-11-08   | 2026-12-08   |    1000.00
----------------------------------------------------------------------------------------
TOTAL ACCUMULATED REVENUE: ₹108,000.00 across 20 active/recorded subscriptions.
========================================================================================
```

---

## 6. Sample Python Preprocessing and Data Science Output

### Execution Command
```bash
python python/visualize_activity.py
```

### Console Output
```text
============================================================
Generating Visualizations for Gym Activity Analysis...
============================================================
[DB Retrieval] Loaded 'members': 20 rows, 8 columns
[DB Retrieval] Loaded 'membership_plans': 5 rows, 5 columns
[DB Retrieval] Loaded 'trainers': 5 rows, 7 columns
[DB Retrieval] Loaded 'member_subscriptions': 20 rows, 7 columns
[DB Retrieval] Loaded 'trainer_assignments': 13 rows, 6 columns
[DB Retrieval] Loaded 'attendance': 62 rows, 6 columns
[Preprocessing] Member Age Stats - Mean: 29.3, Median: 27.0, IQR Outliers count: 1
[Preprocessing] All datasets cleaned and feature engineered successfully.
[Chart Saved] -> D:\Project\visualizations\01_attendance_by_day_of_week.png
[Chart Saved] -> D:\Project\visualizations\02_plan_popularity_and_revenue.png
[Chart Saved] -> D:\Project\visualizations\03_trainer_workload_distribution.png
[Chart Saved] -> D:\Project\visualizations\04_workout_type_preference_pie.png
[Chart Saved] -> D:\Project\visualizations\05_hourly_checkin_heatmap.png
[Chart Saved] -> D:\Project\visualizations\06_member_age_distribution_boxplot.png

[SUCCESS] All 6 visualizations created successfully in 'visualizations/' folder.
```
