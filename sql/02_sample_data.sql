-- ============================================================
-- Smart Student Hub - Sample Data
-- Passwords are SHA-256 hashed: "password123" -> hash below
-- ============================================================

USE smart_student_hub;

-- ROLES
INSERT INTO roles (role_name) VALUES ('Administrator'), ('Teacher'), ('Student');

-- DEPARTMENTS
INSERT INTO departments (dept_name, dept_code) VALUES
('Computer Science', 'CS'),
('Electronics', 'EC'),
('Mechanical', 'ME'),
('Civil Engineering', 'CE'),
('Business Administration', 'BA');

-- COURSES
INSERT INTO courses (course_name, course_code, dept_id, duration_yrs, total_semesters) VALUES
('B.Sc Computer Science', 'BSC-CS', 1, 3, 6),
('B.Tech Electronics', 'BTECH-EC', 2, 4, 8),
('B.Tech Mechanical', 'BTECH-ME', 3, 4, 8),
('BBA', 'BBA', 5, 3, 6),
('B.Tech Civil', 'BTECH-CE', 4, 4, 8);

-- SUBJECTS (BSc CS - Sem 1-6)
INSERT INTO subjects (subject_name, subject_code, course_id, semester, credits) VALUES
('Introduction to Programming', 'CS101', 1, 1, 4),
('Mathematics I', 'MATH101', 1, 1, 3),
('Digital Electronics', 'CS102', 1, 1, 3),
('Data Structures', 'CS201', 1, 2, 4),
('Mathematics II', 'MATH201', 1, 2, 3),
('Database Management Systems', 'CS301', 1, 3, 4),
('Operating Systems', 'CS302', 1, 3, 4),
('Computer Networks', 'CS401', 1, 4, 4),
('Software Engineering', 'CS402', 1, 4, 3),
('Artificial Intelligence', 'CS501', 1, 5, 4),
('Machine Learning', 'CS502', 1, 5, 4),
('Cloud Computing', 'CS601', 1, 6, 3),
('Cyber Security', 'CS602', 1, 6, 3);

-- CLASSROOMS
INSERT INTO classrooms (room_name, capacity, room_type) VALUES
('Room A101', 60, 'Lecture'),
('Room A102', 60, 'Lecture'),
('Lab L101', 30, 'Lab'),
('Lab L102', 30, 'Lab'),
('Seminar Hall', 100, 'Seminar');

-- USERS - Admin
-- Password for all: "Admin@123" (SHA-256)
INSERT INTO users (username, password_hash, role_id) VALUES
('admin', SHA2('Admin@123', 256), 1);

-- USERS - Teachers
INSERT INTO users (username, password_hash, role_id) VALUES
('meena896',   SHA2('Teacher@123', 256), 2),
('rajesh_t',   SHA2('Teacher@123', 256), 2),
('priya_s',    SHA2('Teacher@123', 256), 2),
('arjun_m',    SHA2('Teacher@123', 256), 2),
('kavitha_r',  SHA2('Teacher@123', 256), 2);

-- TEACHERS
INSERT INTO teachers (user_id, employee_id, full_name, dob, gender, email, phone, address, dept_id, qualification, joining_date) VALUES
(2, 'EMP001', 'Meena Sharma', '1985-06-15', 'Female', 'meena.sharma@ssh.edu', '9876543210', '12 Park Street, Delhi', 1, 'M.Tech Computer Science', '2018-07-01'),
(3, 'EMP002', 'Rajesh Kumar', '1980-03-22', 'Male', 'rajesh.kumar@ssh.edu', '9876543211', '45 MG Road, Mumbai', 1, 'Ph.D Mathematics', '2015-07-01'),
(4, 'EMP003', 'Priya Singh', '1988-11-30', 'Female', 'priya.singh@ssh.edu', '9876543212', '7 Lal Bagh, Bangalore', 2, 'M.Tech Electronics', '2019-08-01'),
(5, 'EMP004', 'Arjun Mehta', '1983-07-18', 'Male', 'arjun.mehta@ssh.edu', '9876543213', '23 Civil Lines, Jaipur', 3, 'M.Tech Mechanical', '2016-07-01'),
(6, 'EMP005', 'Kavitha Reddy', '1990-01-25', 'Female', 'kavitha.reddy@ssh.edu', '9876543214', '56 Jubilee Hills, Hyderabad', 5, 'MBA', '2020-07-01');

-- USERS - Students
INSERT INTO users (username, password_hash, role_id) VALUES
('mohan189',   SHA2('Student@123', 256), 3),
('anjali_k',   SHA2('Student@123', 256), 3),
('ravi_p',     SHA2('Student@123', 256), 3),
('sunita_g',   SHA2('Student@123', 256), 3),
('vivek_m',    SHA2('Student@123', 256), 3),
('pooja_d',    SHA2('Student@123', 256), 3),
('deepak_s',   SHA2('Student@123', 256), 3),
('nisha_t',    SHA2('Student@123', 256), 3),
('rahul_v',    SHA2('Student@123', 256), 3),
('smita_j',    SHA2('Student@123', 256), 3);

-- STUDENTS
INSERT INTO students (user_id, admission_no, full_name, dob, gender, email, phone, address, course_id, current_semester, guardian_name, guardian_phone, guardian_relation, blood_group, admission_date) VALUES
(7,  'ADM2024001', 'Mohan Singh',    '2003-04-12', 'Male',   'mohan.s@student.ssh.edu',  '8765432100', '10 Ram Nagar, Delhi',     1, 3, 'Ram Singh',    '9988776655', 'Father', 'O+',  '2024-07-01'),
(8,  'ADM2024002', 'Anjali Kapoor',  '2004-08-22', 'Female', 'anjali.k@student.ssh.edu', '8765432101', '22 Shyam Colony, Agra',   1, 3, 'Suresh Kapoor','9988776656', 'Father', 'A+',  '2024-07-01'),
(9,  'ADM2024003', 'Ravi Patel',     '2003-12-05', 'Male',   'ravi.p@student.ssh.edu',   '8765432102', '5 Garden View, Surat',    1, 3, 'Mohan Patel',  '9988776657', 'Father', 'B+',  '2024-07-01'),
(10, 'ADM2024004', 'Sunita Gupta',   '2004-03-18', 'Female', 'sunita.g@student.ssh.edu', '8765432103', '7 Civil Lines, Lucknow',  1, 3, 'Anil Gupta',   '9988776658', 'Father', 'AB+', '2024-07-01'),
(11, 'ADM2024005', 'Vivek Mishra',   '2003-07-30', 'Male',   'vivek.m@student.ssh.edu',  '8765432104', '3 Shivaji Park, Pune',    1, 3, 'Arun Mishra',  '9988776659', 'Father', 'O-',  '2024-07-01'),
(12, 'ADM2024006', 'Pooja Desai',    '2004-01-14', 'Female', 'pooja.d@student.ssh.edu',  '8765432105', '9 Lake View, Bhopal',     1, 1, 'Vijay Desai',  '9988776660', 'Father', 'A-',  '2025-07-01'),
(13, 'ADM2024007', 'Deepak Sharma',  '2004-09-28', 'Male',   'deepak.s@student.ssh.edu', '8765432106', '16 New Colony, Indore',   1, 1, 'Vinod Sharma', '9988776661', 'Father', 'B-',  '2025-07-01'),
(14, 'ADM2024008', 'Nisha Tiwari',   '2003-11-20', 'Female', 'nisha.t@student.ssh.edu',  '8765432107', '4 Ashoka Road, Banaras',  1, 5, 'Ramesh Tiwari','9988776662', 'Father', 'O+',  '2023-07-01'),
(15, 'ADM2024009', 'Rahul Verma',    '2003-05-09', 'Male',   'rahul.v@student.ssh.edu',  '8765432108', '8 Sector 12, Chandigarh', 1, 5, 'Sunil Verma',  '9988776663', 'Father', 'A+',  '2023-07-01'),
(16, 'ADM2024010', 'Smita Joshi',    '2004-02-17', 'Female', 'smita.j@student.ssh.edu',  '8765432109', '2 MG Road, Nashik',       1, 1, 'Ashok Joshi',  '9988776664', 'Father', 'AB-', '2025-07-01');

-- TEACHER-SUBJECT ASSIGNMENTS
INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES
(1, 1), (1, 4), (1, 6), (1, 10), (1, 11),
(2, 2), (2, 5),
(3, 3),
(1, 7), (1, 8), (1, 9), (1, 12), (1, 13);

-- ENROLLMENTS (Semester 3 students in sem 3 subjects)
INSERT INTO enrollments (student_id, subject_id, semester, academic_year) VALUES
(1, 6, 3, '2025-26'), (1, 7, 3, '2025-26'),
(2, 6, 3, '2025-26'), (2, 7, 3, '2025-26'),
(3, 6, 3, '2025-26'), (3, 7, 3, '2025-26'),
(4, 6, 3, '2025-26'), (4, 7, 3, '2025-26'),
(5, 6, 3, '2025-26'), (5, 7, 3, '2025-26');

-- SAMPLE ATTENDANCE (last 10 days, subject DBMS)
INSERT INTO attendance (student_id, subject_id, att_date, status, marked_by) VALUES
(1, 6, '2026-05-15', 'Present', 1), (1, 6, '2026-05-16', 'Present', 1),
(1, 6, '2026-05-17', 'Absent',  1), (1, 6, '2026-05-18', 'Present', 1),
(1, 6, '2026-05-19', 'Present', 1), (1, 6, '2026-05-20', 'Present', 1),
(2, 6, '2026-05-15', 'Present', 1), (2, 6, '2026-05-16', 'Absent',  1),
(2, 6, '2026-05-17', 'Absent',  1), (2, 6, '2026-05-18', 'Present', 1),
(2, 6, '2026-05-19', 'Absent',  1), (2, 6, '2026-05-20', 'Present', 1),
(3, 6, '2026-05-15', 'Present', 1), (3, 6, '2026-05-16', 'Present', 1),
(3, 6, '2026-05-17', 'Present', 1), (3, 6, '2026-05-18', 'Present', 1),
(3, 6, '2026-05-19', 'Present', 1), (3, 6, '2026-05-20', 'Absent',  1);

-- FEE STRUCTURE
INSERT INTO fee_structure (course_id, semester, fee_type, amount, academic_year) VALUES
(1, 1, 'Tuition Fee', 25000.00, '2025-26'),
(1, 1, 'Library Fee', 2000.00,  '2025-26'),
(1, 1, 'Lab Fee',     3000.00,  '2025-26'),
(1, 2, 'Tuition Fee', 25000.00, '2025-26'),
(1, 3, 'Tuition Fee', 25000.00, '2025-26');

-- FEE PAYMENTS
INSERT INTO fee_payments (student_id, fee_id, amount_paid, fine_amount, payment_date, due_date, payment_mode, receipt_no) VALUES
(1, 1, 25000.00, 0.00,   '2025-07-10', '2025-07-15', 'Online', 'RCP2025001'),
(1, 2, 2000.00,  0.00,   '2025-07-10', '2025-07-15', 'Online', 'RCP2025002'),
(2, 1, 25000.00, 500.00, '2025-07-20', '2025-07-15', 'Cash',   'RCP2025003'),
(3, 1, 25000.00, 0.00,   '2025-07-12', '2025-07-15', 'DD',     'RCP2025004'),
(4, 1, 25000.00, 0.00,   '2025-07-14', '2025-07-15', 'Online', 'RCP2025005');

-- EXAMS
INSERT INTO exams (exam_name, exam_type, subject_id, semester, exam_date, max_marks, pass_marks, academic_year) VALUES
('DBMS Internal 1',       'Internal', 6, 3, '2026-03-10', 30,  12, '2025-26'),
('DBMS Internal 2',       'Internal', 6, 3, '2026-04-15', 30,  12, '2025-26'),
('DBMS External',         'External', 6, 3, '2026-05-20', 70,  28, '2025-26'),
('OS Internal 1',         'Internal', 7, 3, '2026-03-12', 30,  12, '2025-26'),
('Programming Internal 1','Internal', 1, 1, '2026-03-08', 30,  12, '2025-26');

-- RESULTS
INSERT INTO results (student_id, exam_id, marks, grade, is_pass) VALUES
(1, 1, 26, 'A',  TRUE), (1, 2, 24, 'A', TRUE),  (1, 3, 58, 'A',  TRUE),
(2, 1, 20, 'B',  TRUE), (2, 2, 18, 'B', TRUE),  (2, 3, 45, 'B+', TRUE),
(3, 1, 28, 'A+', TRUE), (3, 2, 27, 'A+', TRUE), (3, 3, 65, 'A+', TRUE),
(4, 1, 22, 'B+', TRUE), (4, 2, 25, 'A', TRUE),  (4, 3, 52, 'A',  TRUE),
(5, 1, 15, 'C',  TRUE), (5, 2, 14, 'C', TRUE),  (5, 3, 38, 'C',  TRUE);

-- TIMETABLE
INSERT INTO timetable (course_id, semester, subject_id, teacher_id, room_id, day_of_week, start_time, end_time, academic_year) VALUES
(1, 3, 6, 1, 1, 'Monday',    '09:00:00', '10:00:00', '2025-26'),
(1, 3, 7, 1, 2, 'Monday',    '10:00:00', '11:00:00', '2025-26'),
(1, 3, 6, 1, 1, 'Wednesday', '09:00:00', '10:00:00', '2025-26'),
(1, 3, 7, 1, 2, 'Wednesday', '10:00:00', '11:00:00', '2025-26'),
(1, 3, 6, 1, 3, 'Friday',    '11:00:00', '13:00:00', '2025-26');

-- LIBRARY BOOKS
INSERT INTO library_books (title, author, isbn, category, publisher, pub_year, total_copies, available) VALUES
('Introduction to Java', 'Herbert Schildt',    '978-0071606301', 'Programming',  'McGraw Hill',    2019, 5, 4),
('Database Systems',     'Ramez Elmasri',      '978-0133970777', 'Database',     'Pearson',        2017, 3, 3),
('Operating Systems',    'Abraham Silberschatz','978-1118063330', 'OS',           'Wiley',          2018, 4, 3),
('Data Structures',      'Mark Allen Weiss',   '978-0132576277', 'DSA',          'Pearson',        2013, 6, 5),
('Computer Networks',    'Andrew Tanenbaum',   '978-0132126953', 'Networks',     'Pearson',        2011, 3, 2),
('Artificial Intelligence','Stuart Russell',   '978-0136042594', 'AI',           'Pearson',        2020, 2, 2),
('Machine Learning',     'Tom Mitchell',       '978-0070428072', 'ML',           'McGraw Hill',    1997, 2, 2),
('Software Engineering', 'Ian Sommerville',    '978-0133943030', 'SE',           'Pearson',        2015, 4, 4);

-- ISSUED BOOKS
INSERT INTO issued_books (book_id, student_id, issue_date, due_date, status) VALUES
(1, 1, '2026-05-01', '2026-05-15', 'Overdue'),
(3, 2, '2026-05-10', '2026-05-24', 'Issued'),
(5, 3, '2026-04-20', '2026-05-04', 'Overdue');

-- NOTICES
INSERT INTO notices (title, content, posted_by, target_role, is_important, expires_at) VALUES
('Semester Exams Schedule', 'End semester exams for all courses will begin from June 1, 2026. Detailed schedule is available on the notice board.', 1, 'All', TRUE,  '2026-06-30'),
('Fee Payment Deadline',    'Last date for fee payment without fine is May 31, 2026. Late payment will attract a fine of Rs. 50 per day.', 1, 'Student', TRUE, '2026-05-31'),
('Faculty Meeting',         'All faculty members are requested to attend the staff meeting on May 28, 2026 at 11:00 AM in the conference room.', 1, 'Teacher', FALSE, '2026-05-28'),
('Library Book Return',     'All issued library books must be returned by May 30, 2026 before exams commence.', 1, 'Student', FALSE, '2026-05-30'),
('Annual Sports Day',       'Annual Sports Day will be held on June 15, 2026. Students interested in participating may register at the sports office.', 1, 'All', FALSE, '2026-06-15');

-- ASSIGNMENTS
INSERT INTO assignments (title, description, subject_id, teacher_id, due_date, max_marks) VALUES
('DBMS ER Diagram Assignment', 'Design an ER diagram for a hospital management system with all entities and relationships.', 6, 1, '2026-05-30', 10),
('OS Process Scheduling',      'Implement FCFS, SJF, and Round Robin scheduling algorithms in C with Gantt chart output.', 7, 1, '2026-06-05', 10);

-- SUBMISSIONS
INSERT INTO submissions (assignment_id, student_id, marks_obtained, status) VALUES
(1, 1, 9,   'Graded'),
(1, 2, 7.5, 'Graded'),
(1, 3, 10,  'Graded'),
(1, 4, 8,   'Graded'),
(2, 1, NULL,'Submitted');

-- EVENTS
INSERT INTO events (event_name, description, event_date, event_type, venue, organizer) VALUES
('Tech Fest 2026',    'Annual college technology festival with hackathons, project expo, and guest lectures.',         '2026-06-20', 'Festival',  'College Ground', 1),
('Workshop on AI/ML', 'Two-day hands-on workshop on Artificial Intelligence and Machine Learning fundamentals.',       '2026-06-10', 'Workshop',  'Seminar Hall',   1),
('Cultural Night',    'Annual cultural evening featuring performances by students from all departments.',              '2026-06-25', 'Cultural',  'Auditorium',     1),
('Career Fair 2026',  'Campus recruitment fair with top companies visiting for placement drives.',                     '2026-07-05', 'Placement', 'College Ground', 1);

-- HOSTEL
INSERT INTO hostel_rooms (room_no, room_type, capacity, occupied, block) VALUES
('A101', 'Double', 2, 2, 'Block A'),
('A102', 'Double', 2, 1, 'Block A'),
('B101', 'Triple', 3, 3, 'Block B'),
('B102', 'Single', 1, 1, 'Block B'),
('C101', 'Double', 2, 0, 'Block C');

INSERT INTO hostel_allotments (student_id, room_id, allot_date, fee_per_month) VALUES
(1, 1, '2024-07-01', 3500.00),
(2, 1, '2024-07-01', 3500.00),
(3, 2, '2024-07-01', 3500.00);

-- NOTIFICATIONS
INSERT INTO notifications (user_id, message, notif_type, is_read) VALUES
(7,  'Your attendance in DBMS has fallen below 75%. Please attend classes regularly.',       'Attendance', FALSE),
(8,  'Fee payment overdue. Please clear pending dues immediately.',                          'Fee',        FALSE),
(7,  'Exam schedule for Semester 3 has been published.',                                     'Exam',       TRUE),
(14, 'Your result for Semester 4 has been published. Login to view.',                        'Result',     FALSE),
(15, 'Reminder: Assignment submission deadline is May 30.',                                  'Assignment', FALSE);

-- TEACHER ATTENDANCE SAMPLES
INSERT INTO teacher_attendance (teacher_id, att_date, status) VALUES
(1, '2026-05-20', 'Present'), (1, '2026-05-21', 'Present'),
(1, '2026-05-22', 'Present'), (1, '2026-05-23', 'Absent'),
(2, '2026-05-20', 'Present'), (2, '2026-05-21', 'Present'),
(2, '2026-05-22', 'Leave'),   (2, '2026-05-23', 'Present');

-- LEAVE APPLICATIONS
INSERT INTO leaves (applicant_id, applicant_role, leave_type, from_date, to_date, reason, status) VALUES
(7,  'Student', 'Medical',  '2026-05-22', '2026-05-23', 'High fever and doctor advised rest', 'Approved'),
(8,  'Student', 'Personal', '2026-05-25', '2026-05-25', 'Family function',                    'Pending'),
(3,  'Teacher', 'Personal', '2026-05-28', '2026-05-28', 'Personal work',                      'Pending');
