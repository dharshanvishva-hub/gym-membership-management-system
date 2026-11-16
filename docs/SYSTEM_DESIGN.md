# System Design and Architecture Document
## Gym Membership Management and Fitness Activity Analysis System

**Student Name:** V. Dharshan  
**Register Number:** 732925ITR027  
**Class:** 25IT3A  
**Course:** 25ITT33 - Software Engineering  
**Academic Year:** 2026 - 27 (ODD)  
**Institution:** Velalar College of Engineering and Technology, Erode - 12  
**Department:** Department of Information Technology  

---

## 1. System Architecture

The system adopts a modular **N-Tier Layered Architecture** with distinct separation between Presentation, Service / Business Logic, Data Access (DAO), and Persistence tiers, coupled with an analytical pipeline in Python.

```mermaid
graph TD
    subgraph Presentation_Tier["Presentation Tier (UI)"]
        CLI["Interactive Console Menu (ConsoleUI)"]
        GUI["Desktop GUI (GymAppGUI Swing)"]
    end

    subgraph Service_Tier["Business Logic & Service Tier"]
        GMS["GymManagementService<br/>- Registration & Validation<br/>- Status Tracking & Streams<br/>- Business Aggregations"]
        RGS["ReportGeneratorService<br/>- Asynchronous Worker Thread<br/>- Formatted Summary Reports"]
    end

    subgraph DAO_Tier["Data Access Tier (DAO Pattern)"]
        CR["<<interface>> CrudRepository<T>"]
        MDAO["MemberDAO"]
        PDAO["PlanDAO"]
        TDAO["TrainerDAO"]
        SDAO["SubscriptionDAO"]
        TADAO["TrainerAssignmentDAO"]
        ADAO["AttendanceDAO"]
        DBC["DatabaseConnection (Singleton)"]
    end

    subgraph Persistence_Tier["Database Tier (RDBMS)"]
        DB[("SQLite Database (gym.db)")]
    end

    subgraph Analytics_Tier["Python Data Science & Visualization Tier"]
        DBR["db_retrieval.py (sqlite3 -> Pandas)"]
        DPP["data_preprocessing.py (Cleaning, IQR, Feature Engineering)"]
        DVA["visualize_activity.py (Matplotlib & Seaborn)"]
        NB["gym_activity_analysis.ipynb (Jupyter Notebook)"]
        PNG["Saved Charts (.png) in visualizations/"]
    end

    CLI --> GMS
    GUI --> GMS
    CLI --> RGS
    GUI --> RGS
    RGS --> GMS

    GMS --> MDAO
    GMS --> PDAO
    GMS --> TDAO
    GMS --> SDAO
    GMS --> TADAO
    GMS --> ADAO

    MDAO -.-> CR
    PDAO -.-> CR
    TDAO -.-> CR
    SDAO -.-> CR
    TADAO -.-> CR
    ADAO -.-> CR

    MDAO --> DBC
    PDAO --> DBC
    TDAO --> DBC
    SDAO --> DBC
    TADAO --> DBC
    ADAO --> DBC

    DBC --> DB

    DBR --> DB
    DPP --> DBR
    DVA --> DPP
    DVA --> PNG
    NB --> DB
    NB --> PNG
```

---

## 2. Use Case Diagram

The primary actors interacting with the system are:
1. **Gym Administrator / Receptionist:** Handles daily gym operations.
2. **Fitness Trainer:** Observes training assignments and schedules.
3. **Data Analyst / Gym Manager:** Evaluates facility usage and revenue trends.

```mermaid
graph LR
    Admin["Gym Administrator"]
    Trainer["Fitness Trainer"]
    Analyst["Data Analyst / Manager"]

    subgraph Gym_System["Gym Management & Activity Analysis System"]
        UC1["Manage Member Details (Add, Update, Search)"]
        UC2["Configure Membership Plans"]
        UC3["Register Subscriptions & Renewals"]
        UC4["Record Attendance (Check-in / Check-out)"]
        UC5["Assign Members to Trainers"]
        UC6["Track Membership Status (Active/Expired)"]
        UC7["Generate Financial & Workload Reports"]
        UC8["Run Background Multithreaded Audit"]
        UC9["Perform Python Preprocessing & IQR Outlier Analysis"]
        UC10["Generate Activity & Peak Hour Visualizations"]
    end

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8

    Trainer --> UC4
    Trainer --> UC5

    Analyst --> UC7
    Analyst --> UC9
    Analyst --> UC10
```

---

## 3. Class Diagram

The class structure adheres strictly to Object-Oriented principles, leveraging inheritance, encapsulation, generics, and interface contracts.

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        <<abstract>>
        #int id
        +getId() int
        +setId(int id) void
    }

    class Member {
        -String name
        -int age
        -String gender
        -String contactNumber
        -String email
        -LocalDate registrationDate
        -String status
        +getMemberId() int
        +getName() String
        +getAge() int
        +getContactNumber() String
        +getStatus() String
    }

    class MembershipPlan {
        -String planName
        -int durationMonths
        -double fee
        -String facilitiesOffered
        +getPlanId() int
        +getPlanName() String
        +getDurationMonths() int
        +getFee() double
    }

    class Trainer {
        -String name
        -String specialization
        -String contactNumber
        -String email
        -int experienceYears
        -double hourlyRate
        +getTrainerId() int
        +getSpecialization() String
    }

    class Subscription {
        -int memberId
        -int planId
        -LocalDate startDate
        -LocalDate expiryDate
        -double amountPaid
        -String paymentStatus
        +getDaysRemaining() long
        +getStatus() String
    }

    class TrainerAssignment {
        -int memberId
        -int trainerId
        -LocalDate assignedDate
        -String fitnessGoal
        -String status
    }

    class Attendance {
        -int memberId
        -LocalDate attendanceDate
        -LocalTime checkInTime
        -LocalTime checkOutTime
        -String workoutType
    }

    class CrudRepository~T~ {
        <<interface>>
        +findAll() List~T~
        +findById(int id) Optional~T~
        +save(T entity) boolean
        +update(T entity) boolean
        +delete(int id) boolean
    }

    class GymManagementService {
        -MemberDAO memberDAO
        -PlanDAO planDAO
        -TrainerDAO trainerDAO
        -SubscriptionDAO subscriptionDAO
        -AttendanceDAO attendanceDAO
        +registerMember(...) Member
        +subscribeMember(...) Subscription
        +recordCheckIn(...) Attendance
        +recordCheckOut(...) boolean
        +calculateTotalRevenue() double
        +getSubscriptionsCategorized() Map
    }

    class ReportGeneratorService {
        -GymManagementService gymService
        +generateRevenueReport() String
        +generateExpiringMembershipsReport(int days) String
        +generateAsyncAuditReport(Runnable onComplete) void
    }

    BaseEntity <|-- Member
    BaseEntity <|-- MembershipPlan
    BaseEntity <|-- Trainer
    BaseEntity <|-- Subscription
    BaseEntity <|-- TrainerAssignment
    BaseEntity <|-- Attendance

    CrudRepository <|.. MemberDAO
    CrudRepository <|.. PlanDAO
    CrudRepository <|.. TrainerDAO
    CrudRepository <|.. SubscriptionDAO
    CrudRepository <|.. AttendanceDAO

    GymManagementService --> MemberDAO
    GymManagementService --> PlanDAO
    GymManagementService --> TrainerDAO
    GymManagementService --> SubscriptionDAO
    GymManagementService --> AttendanceDAO
    ReportGeneratorService --> GymManagementService
```

---

## 4. Component Interaction Sequence

### Member Registration and Subscription Workflow
```mermaid
sequenceDiagram
    autonumber
    actor Admin as Gym Admin
    participant UI as ConsoleUI / SwingGUI
    participant Service as GymManagementService
    participant MemberDAO as MemberDAO
    participant SubDAO as SubscriptionDAO
    participant DB as SQLite DB

    Admin->>UI: Enter Member Details (Name, Age, Contact, Email)
    UI->>Service: registerMember(name, age, gender, contact, email)
    Service->>Service: validateMemberInput()
    Service->>MemberDAO: save(Member)
    MemberDAO->>DB: INSERT INTO members (...)
    DB-->>MemberDAO: Return Generated member_id
    MemberDAO-->>Service: Return Member
    Service-->>UI: Member Registered (ID: X)

    Admin->>UI: Select Membership Plan & Confirm Subscription
    UI->>Service: subscribeMember(memberId, planId, startDate)
    Service->>Service: Compute expiryDate = start + plan.duration
    Service->>SubDAO: save(Subscription)
    SubDAO->>DB: INSERT INTO member_subscriptions (...)
    DB-->>SubDAO: Success
    SubDAO-->>Service: Return Subscription
    Service-->>UI: Subscription Confirmed
    UI-->>Admin: Display Confirmation & Expiry Date
```
