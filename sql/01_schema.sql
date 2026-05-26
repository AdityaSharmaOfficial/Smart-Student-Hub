-- ============================================================
-- Smart Student Hub - Complete Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_student_hub
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_student_hub;

-- ============================================================
-- ROLES & USERS
-- ============================================================

CREATE TABLE roles (
    role_id   INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       INT NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- ============================================================
-- DEPARTMENTS & COURSES & SUBJECTS
-- ============================================================

CREATE TABLE departments (
    dept_id   INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    dept_code VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_name  VARCHAR(150) NOT NULL,
    course_code  VARCHAR(30)  NOT NULL UNIQUE,
    dept_id      INT NOT NULL,
    duration_yrs INT DEFAULT 3,
    total_semesters INT DEFAULT 6,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

CREATE TABLE subjects (
    subject_id      INT AUTO_INCREMENT PRIMARY KEY,
    subject_name    VARCHAR(150) NOT NULL,
    subject_code    VARCHAR(30)  NOT NULL UNIQUE,
    course_id       INT NOT NULL,
    semester        INT NOT NULL,
    credits         INT DEFAULT 3,
    is_elective     BOOLEAN DEFAULT FALSE,
    prerequisite_id INT DEFAULT NULL,
    FOREIGN KEY (course_id)       REFERENCES courses(course_id),
    FOREIGN KEY (prerequisite_id) REFERENCES subjects(subject_id)
);

-- ============================================================
-- TEACHERS
-- ============================================================

CREATE TABLE teachers (
    teacher_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL UNIQUE,
    employee_id  VARCHAR(30) NOT NULL UNIQUE,
    full_name    VARCHAR(150) NOT NULL,
    dob          DATE,
    gender       ENUM('Male','Female','Other'),
    email        VARCHAR(150) NOT NULL UNIQUE,
    phone        VARCHAR(20),
    address      TEXT,
    dept_id      INT,
    qualification VARCHAR(200),
    joining_date DATE,
    photo_path   VARCHAR(500),
    is_active    BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id)  REFERENCES users(user_id),
    FOREIGN KEY (dept_id)  REFERENCES departments(dept_id)
);

CREATE TABLE teacher_subjects (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    subject_id INT NOT NULL,
    UNIQUE (teacher_id, subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE teacher_attendance (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id  INT NOT NULL,
    att_date    DATE NOT NULL,
    status      ENUM('Present','Absent','Leave','Half-Day') DEFAULT 'Present',
    remarks     VARCHAR(255),
    UNIQUE (teacher_id, att_date),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);

-- ============================================================
-- STUDENTS
-- ============================================================

CREATE TABLE students (
    student_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL UNIQUE,
    admission_no     VARCHAR(30) NOT NULL UNIQUE,
    full_name        VARCHAR(150) NOT NULL,
    dob              DATE,
    gender           ENUM('Male','Female','Other'),
    email            VARCHAR(150),
    phone            VARCHAR(20),
    address          TEXT,
    course_id        INT NOT NULL,
    current_semester INT DEFAULT 1,
    guardian_name    VARCHAR(150),
    guardian_phone   VARCHAR(20),
    guardian_relation VARCHAR(50),
    blood_group      VARCHAR(10),
    photo_path       VARCHAR(500),
    admission_date   DATE,
    is_active        BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id)   REFERENCES users(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ============================================================
-- ENROLLMENTS
-- ============================================================

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    subject_id    INT NOT NULL,
    semester      INT NOT NULL,
    academic_year VARCHAR(20),
    enrolled_date DATE DEFAULT (CURDATE()),
    status        ENUM('Active','Dropped','Completed') DEFAULT 'Active',
    UNIQUE (student_id, subject_id, academic_year),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- ============================================================
-- ATTENDANCE
-- ============================================================

CREATE TABLE attendance (
    att_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    att_date   DATE NOT NULL,
    status     ENUM('Present','Absent','Late','Excused') DEFAULT 'Present',
    marked_by  INT,
    UNIQUE (student_id, subject_id, att_date),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (marked_by)  REFERENCES teachers(teacher_id)
);

-- ============================================================
-- FEES
-- ============================================================

CREATE TABLE fee_structure (
    fee_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_id    INT NOT NULL,
    semester     INT NOT NULL,
    fee_type     VARCHAR(100) NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,
    academic_year VARCHAR(20),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE fee_payments (
    payment_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    fee_id        INT NOT NULL,
    amount_paid   DECIMAL(10,2) NOT NULL,
    fine_amount   DECIMAL(10,2) DEFAULT 0.00,
    payment_date  DATE DEFAULT (CURDATE()),
    due_date      DATE,
    payment_mode  ENUM('Cash','Online','DD','Cheque') DEFAULT 'Cash',
    receipt_no    VARCHAR(50) NOT NULL UNIQUE,
    remarks       VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (fee_id)     REFERENCES fee_structure(fee_id)
);

-- ============================================================
-- EXAMS & RESULTS
-- ============================================================

CREATE TABLE exams (
    exam_id    INT AUTO_INCREMENT PRIMARY KEY,
    exam_name  VARCHAR(150) NOT NULL,
    exam_type  ENUM('Internal','External','Practical','Viva') DEFAULT 'Internal',
    subject_id INT NOT NULL,
    semester   INT NOT NULL,
    exam_date  DATE,
    max_marks  INT DEFAULT 100,
    pass_marks INT DEFAULT 40,
    academic_year VARCHAR(20),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE results (
    result_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id    INT NOT NULL,
    marks      DECIMAL(6,2) NOT NULL,
    grade      VARCHAR(5),
    is_pass    BOOLEAN DEFAULT TRUE,
    remarks    VARCHAR(255),
    UNIQUE (student_id, exam_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (exam_id)    REFERENCES exams(exam_id)
);

-- ============================================================
-- TIMETABLE
-- ============================================================

CREATE TABLE classrooms (
    room_id    INT AUTO_INCREMENT PRIMARY KEY,
    room_name  VARCHAR(50) NOT NULL,
    capacity   INT DEFAULT 60,
    room_type  ENUM('Lecture','Lab','Seminar') DEFAULT 'Lecture'
);

CREATE TABLE timetable (
    tt_id      INT AUTO_INCREMENT PRIMARY KEY,
    course_id  INT NOT NULL,
    semester   INT NOT NULL,
    subject_id INT NOT NULL,
    teacher_id INT NOT NULL,
    room_id    INT,
    day_of_week ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'),
    start_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    academic_year VARCHAR(20),
    FOREIGN KEY (course_id)  REFERENCES courses(course_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (room_id)    REFERENCES classrooms(room_id)
);

-- ============================================================
-- LIBRARY
-- ============================================================

CREATE TABLE library_books (
    book_id      INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    author       VARCHAR(200),
    isbn         VARCHAR(50)  UNIQUE,
    category     VARCHAR(100),
    publisher    VARCHAR(150),
    pub_year     YEAR,
    total_copies INT DEFAULT 1,
    available    INT DEFAULT 1
);

CREATE TABLE issued_books (
    issue_id    INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT NOT NULL,
    student_id  INT,
    teacher_id  INT,
    issue_date  DATE DEFAULT (CURDATE()),
    due_date    DATE,
    return_date DATE,
    fine_amount DECIMAL(8,2) DEFAULT 0.00,
    status      ENUM('Issued','Returned','Overdue') DEFAULT 'Issued',
    FOREIGN KEY (book_id)    REFERENCES library_books(book_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);

-- ============================================================
-- NOTICES & ANNOUNCEMENTS
-- ============================================================

CREATE TABLE notices (
    notice_id   INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    posted_by   INT NOT NULL,
    target_role VARCHAR(50) DEFAULT 'All',
    is_important BOOLEAN DEFAULT FALSE,
    posted_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at  DATE,
    FOREIGN KEY (posted_by) REFERENCES users(user_id)
);

-- ============================================================
-- LEAVES
-- ============================================================

CREATE TABLE leaves (
    leave_id    INT AUTO_INCREMENT PRIMARY KEY,
    applicant_id INT NOT NULL,
    applicant_role ENUM('Student','Teacher') NOT NULL,
    leave_type  VARCHAR(50) DEFAULT 'Personal',
    from_date   DATE NOT NULL,
    to_date     DATE NOT NULL,
    reason      TEXT,
    status      ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    reviewed_by INT,
    applied_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

-- ============================================================
-- ASSIGNMENTS
-- ============================================================

CREATE TABLE assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    subject_id    INT NOT NULL,
    teacher_id    INT NOT NULL,
    due_date      DATE,
    max_marks     INT DEFAULT 10,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);

CREATE TABLE submissions (
    submission_id  INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id  INT NOT NULL,
    student_id     INT NOT NULL,
    submitted_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_path      VARCHAR(500),
    marks_obtained DECIMAL(5,2),
    feedback       TEXT,
    status         ENUM('Submitted','Late','Graded') DEFAULT 'Submitted',
    UNIQUE (assignment_id, student_id),
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id),
    FOREIGN KEY (student_id)    REFERENCES students(student_id)
);

-- ============================================================
-- HOSTEL
-- ============================================================

CREATE TABLE hostel_rooms (
    room_id   INT AUTO_INCREMENT PRIMARY KEY,
    room_no   VARCHAR(20) NOT NULL UNIQUE,
    room_type ENUM('Single','Double','Triple') DEFAULT 'Double',
    capacity  INT DEFAULT 2,
    occupied  INT DEFAULT 0,
    block      VARCHAR(50)
);

CREATE TABLE hostel_allotments (
    allot_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL UNIQUE,
    room_id    INT NOT NULL,
    allot_date DATE DEFAULT (CURDATE()),
    fee_per_month DECIMAL(8,2) DEFAULT 0.00,
    status     ENUM('Active','Vacated') DEFAULT 'Active',
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (room_id)    REFERENCES hostel_rooms(room_id)
);

-- ============================================================
-- EVENTS
-- ============================================================

CREATE TABLE events (
    event_id    INT AUTO_INCREMENT PRIMARY KEY,
    event_name  VARCHAR(255) NOT NULL,
    description TEXT,
    event_date  DATE,
    event_type  VARCHAR(100),
    venue       VARCHAR(200),
    organizer   INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer) REFERENCES users(user_id)
);

CREATE TABLE event_participants (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    event_id   INT NOT NULL,
    student_id INT NOT NULL,
    UNIQUE (event_id, student_id),
    FOREIGN KEY (event_id)   REFERENCES events(event_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ============================================================
-- NOTIFICATIONS
-- ============================================================

CREATE TABLE notifications (
    notif_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    message     TEXT NOT NULL,
    notif_type  VARCHAR(50),
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ============================================================
-- BACKUP LOG
-- ============================================================

CREATE TABLE backup_log (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    backup_file VARCHAR(500),
    created_by  INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);
