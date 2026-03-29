# Smart Sphere Student Hub
## Full Student Management System in Java

---

## Project Structure

```
SmartSphereStudentHub/
├── src/
│   ├── model/
│   │   ├── User.java           - User account model (Admin/Teacher/Student)
│   │   ├── Student.java        - Student profile model
│   │   ├── Admission.java      - Admission record model
│   │   ├── Attendance.java     - Attendance record model
│   │   ├── Fee.java            - Fee record model
│   │   ├── Result.java         - Marks/Result model (auto-computes grade/GPA)
│   │   └── Notice.java         - Notice/Announcement model
│   │
│   ├── filehandler/
│   │   └── FileHandler.java    - ALL file I/O (BufferedReader/Writer)
│   │
│   ├── service/
│   │   ├── LoginService.java   - Authentication, user management
│   │   ├── StudentService.java - Student CRUD operations
│   │   ├── AdmissionService.java - Admission management
│   │   ├── AttendanceService.java - Attendance + percentage calculation
│   │   ├── FeesService.java    - Fee records, payments, pending calc
│   │   ├── ResultService.java  - Marks, grade/GPA, report generation
│   │   └── NoticeService.java  - Notice board management
│   │
│   ├── ui/
│   │   └── MenuUI.java         - Console UI (ALL Scanner/System.out here)
│   │
│   └── main/
│       └── Main.java           - Entry point, wires all layers together
│
├── data/                       - Created automatically at runtime
│   ├── users.txt
│   ├── students.txt
│   ├── admissions.txt
│   ├── attendance.txt
│   ├── fees.txt
│   ├── results.txt
│   └── notices.txt
│
├── out/                        - Compiled .class files (create before compiling)
├── README.md
└── build.sh / build.bat
```

---

## How to Compile and Run

### Prerequisites
- Java JDK 11 or later installed
- Terminal / Command Prompt

### Linux / macOS

```bash
cd SmartSphereStudentHub
mkdir -p out

javac -d out \
  src/model/*.java \
  src/filehandler/*.java \
  src/service/*.java \
  src/ui/*.java \
  src/main/*.java

java -cp out main.Main
```

### Windows (Command Prompt)

```cmd
cd SmartSphereStudentHub
mkdir out

javac -d out ^
  src\model\*.java ^
  src\filehandler\*.java ^
  src\service\*.java ^
  src\ui\*.java ^
  src\main\*.java

java -cp out main.Main
```

---

## Default Login Credentials

| Role    | Username  | Password  |
|---------|-----------|-----------|
| Admin   | admin     | admin123  |
| Teacher | teacher1  | teach123  |
| Student | student1  | stu123    |

> The student1 account is linked to ID "STU001". Add a student first via admin, then link their ID when creating the student user account.

---

## Features

### Admin Dashboard
1. **Student Management** - Add, view, search, update, delete students
2. **Admission Management** - Admit students, assign course/branch, generate admission ID
3. **Attendance Management** - Mark, view, report, calculate attendance %
4. **Fees Management** - Add fee records, record payments, view pending fees
5. **Result/Marks Management** - Add marks, auto-calculate grade & GPA, full report
6. **Notice Board** - Post notices for ALL/STUDENT/TEACHER
7. **User Account Management** - Create, view, delete user accounts

### Teacher Dashboard
- View all students
- Manage attendance
- Add/update results and marks
- View notices

### Student Dashboard
- View own profile
- View own admission details
- View own attendance report
- View own fee status
- View own result report with CGPA
- View notices

---

## Grade & GPA Scale (10-point)

| Percentage | Grade | GPA  |
|------------|-------|------|
| >= 90      | O     | 10.0 |
| >= 80      | A+    | 9.0  |
| >= 70      | A     | 8.0  |
| >= 60      | B+    | 7.0  |
| >= 50      | B     | 6.0  |
| >= 40      | C     | 5.0  |
| < 40       | F     | 0.0  |

---

## File Storage Format

All data is stored in plain text CSV files in the `data/` directory.

| File             | Delimiter | Fields |
|------------------|-----------|--------|
| users.txt        | comma     | userId, username, password, role, linkedId |
| students.txt     | comma     | studentId, firstName, lastName, email, phone, address, dob, gender, year |
| admissions.txt   | comma     | admissionId, studentId, name, course, branch, date, year, status |
| attendance.txt   | comma     | attendanceId, studentId, name, subject, date, status |
| fees.txt         | comma     | feeId, studentId, name, total, paid, dueDate, payDate, type, semester, status |
| results.txt      | comma     | resultId, studentId, name, subject, got, total, semester, examType, grade, gpa |
| notices.txt      | pipe (|)  | noticeId, title, content, postedBy, date, targetRole |

---

## Connecting a Frontend

### Design Principle
The entire business logic lives in `service/*.java` with **no UI dependencies**.
`MenuUI.java` only calls service methods and displays results.

This means connecting any frontend is just:
1. Keep the same service objects
2. Replace `Scanner` input → form/text-field input
3. Replace `System.out` output → UI component updates

### JavaFX Example

```java
// In your JavaFX Controller class:
public class MainController {

    private StudentService studentService = new StudentService();
    private LoginService   loginService   = new LoginService();
    // ... other services

    @FXML
    private void onAddStudentClick() {
        // Get values from JavaFX text fields
        String firstName = firstNameField.getText();
        String lastName  = lastNameField.getText();
        // ...

        // Same service call as console UI
        Student s = studentService.addStudent(firstName, lastName, ...);

        // Update JavaFX TableView
        studentTable.getItems().add(s);
    }
}
```

### Spring Boot REST API Example

```java
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService = new StudentService();

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();  // exact same service call
    }

    @PostMapping
    public Student addStudent(@RequestBody StudentRequest req) {
        return studentService.addStudent(
            req.getFirstName(), req.getLastName(), req.getEmail(),
            req.getPhone(), req.getAddress(), req.getDob(),
            req.getGender(), req.getYear()
        );
    }
}
```

### Swapping File Storage for a Database

To replace file storage with a database (MySQL, PostgreSQL, etc.):
1. Create a `DatabaseHandler.java` with the same method signatures as `FileHandler.java`
2. Replace `FileHandler.*` calls in service classes with `DatabaseHandler.*` calls
3. Zero changes needed in models, UI, or Main.java

---

## OOP Concepts Used

| Concept         | Where Applied |
|-----------------|---------------|
| Encapsulation   | All model classes (private fields + getters/setters) |
| Abstraction     | Service layer hides file I/O details from UI |
| Modularity      | Each feature in its own class/file |
| Dependency Injection | Services injected into MenuUI via constructor |
| Separation of Concerns | Model / FileHandler / Service / UI layers |
| Collections     | ArrayList used throughout services |
| Exception Handling | try-catch in FileHandler for all I/O operations |

---

*Smart Sphere Student Hub — Designed for extensibility and frontend readiness.*
