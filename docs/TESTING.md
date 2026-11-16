# Software Testing and Quality Assurance Document
## Gym Membership Management and Fitness Activity Analysis System

**Student Name:** V. Dharshan  
**Register Number:** 732925ITR027  
**Class:** 25IT3A  
**Course:** 25ITT33 - Software Engineering  
**Academic Year:** 2026 - 27 (ODD)  
**Institution:** Velalar College of Engineering and Technology, Erode - 12  
**Department:** Department of Information Technology  

---

## 1. Testing Strategy and Objectives

Software testing verifies and validates that the system conforms to specified functional and non-functional requirements while discovering software faults. The testing strategy encompasses:

1. **Unit Testing:** Validating individual classes, validation routines, and entity methods (e.g. `validateMemberInput()`, `Subscription.getStatus()`).
2. **Integration Testing:** Verifying communication between layers: Presentation $\rightarrow$ Service $\rightarrow$ DAO $\rightarrow$ SQLite Database.
3. **Black-Box Testing:** Testing system inputs against expected outputs without examining underlying code, using Equivalence Partitioning (EP) and Boundary Value Analysis (BVA).
4. **System & Validation Testing:** Verifying end-to-end workflows including attendance check-in/out, trainer assignments, background audit worker threads, and Python data preprocessing.

---

## 2. Test Cases and Execution Results

| Test Case ID | Test Category | Module / Feature | Test Scenario & Input Data | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MEM-01** | Unit / Validation | Member Management | Register member with valid details: `Name="Arun Prasath"`, `Age=24`, `Gender="Male"`, `Contact="9123456780"`, `Email="arun.p@gmail.com"` | Member record inserted with generated ID; status set to "Active". | Record inserted with Member ID 1. Status returned "Active". | **PASS** |
| **TC-MEM-02** | Black-Box / BVA | Member Validation | Register member with invalid age boundary: `Age=10` (< 14 allowed) | `ValidationException` thrown: "Member age must be between 14 and 90." | `ValidationException` caught and rejected. No DB record added. | **PASS** |
| **TC-MEM-03** | Black-Box / EP | Member Validation | Register member with malformed contact number: `Contact="98765"` (5 digits) | `ValidationException` thrown: "Contact number must be a valid 10-digit number." | Validation failed; prompted user for corrected 10-digit contact. | **PASS** |
| **TC-MEM-04** | Integration | Member Search | Search for member with keyword `"Dharshan"` or `"9123456780"` | Returns list of matching members containing substring in name or phone. | Query executed cleanly; returned matching member row. | **PASS** |
| **TC-PLN-01** | Unit / Service | Plan Management | Add new plan: `Name="Platinum Annual"`, `Duration=12`, `Fee=13500.00`, `Facilities="All Access"` | Plan created successfully in DB with unique Plan ID. | Plan ID 4 generated and stored. | **PASS** |
| **TC-PLN-02** | Unit / Validation | Plan Validation | Add plan with negative fee: `Fee=-500.00` | Rejected with `ValidationException`: "Fee cannot be negative." | Exception thrown: "Fee cannot be negative." | **PASS** |
| **TC-SUB-01** | Integration | Membership Registration | Subscribe Member 1 to Plan 4 (12 months) starting `2026-08-01` | Subscription created with `expiry_date = 2027-08-01` and status `"Active"`. | Subscription recorded. Expiry computed as `2027-08-01`. Status "Active". | **PASS** |
| **TC-SUB-02** | Unit / Streams | Status Categorization | Query subscriptions expiring within 7 days | Identifies subscriptions with `expiry_date` between today and today+7 days. | Identified subscriptions expiring within 7 days correctly. | **PASS** |
| **TC-SUB-03** | Integration | Membership Renewal | Renew expired member membership (Member 2 with Plan 2) | New subscription created with start date beginning immediately or post-expiry. | Renewal logged with extended expiry date and amount paid recorded. | **PASS** |
| **TC-ATT-01** | Integration | Attendance Check-in | Check in Member 1 with `workout_type="Weight Training"` | Check-in timestamp logged; check-out time set to NULL. | Attendance record logged with current time. Member name joined. | **PASS** |
| **TC-ATT-02** | Unit / Validation | Concurrent Check-in | Check in Member 1 again while an open check-in exists without check-out | Prevented with `ValidationException`: "Member is already checked in today." | Blocked duplicate check-in. User notified. | **PASS** |
| **TC-ATT-03** | Integration | Attendance Check-out | Check out Member 1 from active session | `check_out_time` updated to current time. Open session closed. | Updated `check_out_time` successfully. | **PASS** |
| **TC-TRN-01** | Integration | Trainer Assignment | Assign Member 3 to Trainer 5 for `"Bodybuilding Hypertrophy"` | Record stored in `trainer_assignments` with status `"Active"`. | Assignment stored with Assignment ID 3. | **PASS** |
| **TC-REP-01** | System / Streams | Revenue Calculation | Calculate total gym revenue across subscriptions | Sum of all `amount_paid` matches expected total revenue. | Returned accumulated total revenue (₹108,000.00). | **PASS** |
| **TC-REP-02** | Multithreading | Background Audit | Trigger background audit worker thread | Worker runs concurrently on daemon thread, sleeps 1.2s, formats report, fires callback. | Output: `[Multithreaded Worker] Background report ready!` without blocking main UI. | **PASS** |
| **TC-DS-01** | Python Integration | Database Extraction | Run `db_retrieval.py` against `database/gym.db` | All 6 tables loaded into Pandas DataFrames with correct column structures. | All 6 tables retrieved successfully with 0 missing attributes. | **PASS** |
| **TC-DS-02** | Python / IQR | Data Preprocessing | Execute `data_preprocessing.py` | Computes workout durations in minutes; flags member age outliers via IQR method. | Calculated workout durations; detected 1 age outlier; segmented into age groups. | **PASS** |
| **TC-DS-03** | Python / Viz | Visualization Output | Run `visualize_activity.py` | Generates 6 high-resolution PNG charts in `visualizations/` folder. | 6 PNG images generated and saved successfully. | **PASS** |
| **TC-GUI-01** | System / UI | Swing Desktop Interface | Launch application with `--gui` argument | Swing GUI renders dashboard metrics, members table, plans, attendance tabs without crashing. | Swing window displayed properly with all 6 tabs functioning. | **PASS** |

---

## 3. Test Execution Summary

- **Total Test Cases Defined:** 19
- **Test Cases Passed:** 19 (100%)
- **Test Cases Failed:** 0 (0%)
- **System Defects Observed:** None
- **Overall Assessment:** The system satisfies all functional requirements defined in Scenario 27 and complies with syllabus standards across DBMS, Java, Software Engineering, and Data Science.
