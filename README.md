# 🎓 Smart Student Hub
### Complete Academic ERP System — Java Swing + MySQL

---

## 📋 Overview

Smart Student Hub is a fully-featured desktop-based Academic ERP system built with **Java Swing** and **MySQL**. It supports three role-based portals — Administrator, Teacher, and Student — each with dedicated dashboards and access-controlled modules.

---

## ⚙️ Technology Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 17+                            |
| GUI          | Java Swing (custom UITheme)         |
| Database     | MySQL 8.x                           |
| Connectivity | JDBC (mysql-connector-java)         |
| Architecture | MVC (Model / DAO / UI layers)       |
| Security     | SHA-256 password hashing            |
| Build        | Shell script / Windows BAT          |

---

## 🗂️ Project Structure

```
SmartStudentHub/
├── sql/
│   ├── 01_schema.sql          ← Full normalized DB schema (22 tables)
│   └── 02_sample_data.sql     ← 10 students, 5 teachers, sample records
│
├── lib/
│   └── mysql-connector-java.jar   ← (you provide this)
│
├── src/main/java/com/smartstudent/
│   ├── Main.java                  ← Entry point
│   ├── config/
│   │   └── DatabaseConfig.java    ← JDBC connection singleton
│   ├── model/
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Teacher.java
│   │   └── Models.java            ← Course, Subject, Attendance, etc.
│   ├── dao/
│   │   ├── UserDAO.java           ← Auth + user management
│   │   ├── StudentDAO.java        ← Full CRUD + search
│   │   ├── TeacherDAO.java        ← Full CRUD + search
│   │   ├── AttendanceDAO.java     ← Mark, report, warning
│   │   ├── FeeDAO.java            ← Payment, dues, receipts
│   │   ├── ResultDAO.java         ← Marks, grades, toppers
│   │   ├── NoticeDAO.java         ← Post, view, expire
│   │   ├── LibraryDAO.java        ← Issue, return, fine
│   │   └── AssignmentDAO.java     ← CRUD assignments
│   ├── util/
│   │   ├── UITheme.java           ← Colors, fonts, factory methods
│   │   └── PasswordUtil.java      ← SHA-256 hash/verify
│   └── ui/
│       ├── common/
│       │   └── LoginScreen.java   ← Role-based login
│       ├── admin/
│       │   ├── AdminDashboard.java
│       │   ├── StudentManagementPanel.java
│       │   ├── TeacherManagementPanel.java
│       │   └── AdminPanels.java   ← Attendance, Fees, Library,
│       │                            Exams, Results, Timetable,
│       │                            Notices, Leave, Backup
│       ├── teacher/
│       │   └── TeacherDashboard.java
│       └── student/
│           └── StudentDashboard.java
│
├── build.sh      ← Linux/Mac build & run
├── build.bat     ← Windows build & run
└── README.md
```

---

## 🚀 Setup Instructions

### Step 1 — Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Run schema creation
SOURCE /path/to/SmartStudentHub/sql/01_schema.sql;

# Load sample data
SOURCE /path/to/SmartStudentHub/sql/02_sample_data.sql;
```

### Step 2 — Configure DB Credentials

Edit `src/main/java/com/smartstudent/config/DatabaseConfig.java`:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/smart_student_hub?...";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password";
```

Also update the password in `build.sh` / `build.bat` for the backup feature.

### Step 3 — Add MySQL JDBC Driver

Download **mysql-connector-java-8.x.x.jar** from:
> https://dev.mysql.com/downloads/connector/j/

Place it in the `lib/` folder:
```
SmartStudentHub/lib/mysql-connector-java.jar
```

### Step 4 — Build & Run

**Linux / macOS:**
```bash
chmod +x build.sh
./build.sh
```

**Windows:**
```
Double-click build.bat
```

**Or compile manually:**
```bash
# Compile
javac -cp lib/mysql-connector-java.jar -d out -encoding UTF-8 \
      $(find src -name "*.java")

# Run
java -cp "out:lib/mysql-connector-java.jar" com.smartstudent.Main
```

---

## 🔐 Demo Login Credentials

| Role          | Username   | Password     |
|---------------|------------|--------------|
| Administrator | `admin`    | `Admin@123`  |
| Teacher       | `meena896` | `Teacher@123`|
| Teacher       | `rajesh_t` | `Teacher@123`|
| Student       | `mohan189` | `Student@123`|
| Student       | `anjali_k` | `Student@123`|

---

## 🏗️ Modules

### 👤 Admin Dashboard (20 Modules)
| Module                      | Features                                                              |
|-----------------------------|-----------------------------------------------------------------------|
| Student Management          | Add/Edit/Delete/Search students, auto admission no, profile view      |
| Teacher Management          | Add/Edit/Delete/Search teachers, dept assignment, auto employee ID    |
| Attendance Management       | Mark by subject+date, save batch, view history                        |
| Fee Management              | Record payments, pending dues, receipt generation, fine calculation   |
| Examination Management      | Add exams, enter marks, grade calculation (A+/A/B+/B/C/D/F)          |
| Results Management          | View by exam, topper list, pass/fail status                           |
| Timetable Management        | View/add class schedules with clash detection                         |
| Library Management          | Issue/return books, overdue fine (₹2/day), inventory search          |
| Notice Board                | Post notices by role (All/Student/Teacher), mark important            |
| Leave Management            | Approve/reject leave applications from students & teachers            |
| Course Management           | View courses, subjects, semesters (placeholder, extendable)           |
| Event Management            | (placeholder — easily extensible)                                     |
| Hostel Management           | (placeholder — easily extensible)                                     |
| Assignment Management       | (placeholder — easily extensible)                                     |
| Notifications               | (placeholder — easily extensible)                                     |
| Backup & Restore            | mysqldump backup, restore from file, backup log                       |

### 👩‍🏫 Teacher Dashboard
- My Profile, Mark Attendance, Manage Assignments
- Enter Student Marks, My Timetable, View Results
- Attendance Report (with low-attendance warning)
- Notice Board, Apply for Leave

### 👨‍🎓 Student Dashboard
- My Profile, My Attendance (monthly, colour-coded warnings)
- My Results (all exams), Class Timetable
- Assignments, Fee Details (history + pending dues)
- Library Search, Notice Board (with full content view)
- Apply for Leave, Events, Notifications

---

## 🗄️ Database Schema (22 Tables)

```
roles            users            departments       courses
subjects         teachers         teacher_subjects  teacher_attendance
students         enrollments      attendance        fee_structure
fee_payments     exams            results           classrooms
timetable        library_books    issued_books      notices
leaves           assignments      submissions       hostel_rooms
hostel_allotments events          event_participants notifications
backup_log
```

---

## 🔒 Security Features

- Passwords stored as **SHA-256** hashes (never plaintext)
- **PreparedStatement** used everywhere — SQL injection prevention
- Role-based access control — each role sees only its dashboard
- User accounts deactivated (soft delete) rather than hard deleted

---

## 🎨 UI Design

- Modern flat design with **navy blue** primary palette
- Sidebar with hover/active state animation
- Alternating table row colours for readability
- Red highlight for low-attendance rows
- Stat cards on all dashboards
- Responsive layout (maximized by default)

---

## 📦 Extending the System

Each module follows the same pattern:
1. **Model** — POJO in `model/`
2. **DAO** — SQL queries with PreparedStatement in `dao/`
3. **UI Panel** — Swing `JPanel` in `ui/admin|teacher|student/`
4. **Register** — Add sidebar button → `setContent(new YourPanel())`

Placeholder modules (Events, Hostel, Assignments UI, Notifications) can be converted to full panels following the same pattern as `StudentManagementPanel`.

---

## 📝 Notes

- **Java Version:** Java 17+ recommended (uses text blocks and records optionally)
- **MySQL Version:** 8.0+ required for `CURDATE()` in `DEFAULT` clauses
- **Screen Resolution:** Optimized for 1366×768 and above
- For production use, replace plaintext DB credentials with a `.properties` config file
