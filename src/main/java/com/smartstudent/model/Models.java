package com.smartstudent.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ── Course ────────────────────────────────────────────────────────────────────
class Course {
    private int    courseId;
    private String courseName;
    private String courseCode;
    private int    deptId;
    private String deptName;
    private int    durationYrs;
    private int    totalSemesters;

    public Course() {}
    public int    getCourseId()       { return courseId; }
    public String getCourseName()     { return courseName; }
    public String getCourseCode()     { return courseCode; }
    public int    getDeptId()         { return deptId; }
    public String getDeptName()       { return deptName; }
    public int    getDurationYrs()    { return durationYrs; }
    public int    getTotalSemesters() { return totalSemesters; }
    public void   setCourseId(int v)         { this.courseId = v; }
    public void   setCourseName(String v)    { this.courseName = v; }
    public void   setCourseCode(String v)    { this.courseCode = v; }
    public void   setDeptId(int v)           { this.deptId = v; }
    public void   setDeptName(String v)      { this.deptName = v; }
    public void   setDurationYrs(int v)      { this.durationYrs = v; }
    public void   setTotalSemesters(int v)   { this.totalSemesters = v; }
    @Override public String toString()       { return courseCode + " - " + courseName; }
}

// ── Subject ───────────────────────────────────────────────────────────────────
class Subject {
    private int    subjectId;
    private String subjectName;
    private String subjectCode;
    private int    courseId;
    private String courseName;
    private int    semester;
    private int    credits;
    private boolean isElective;

    public Subject() {}
    public int    getSubjectId()   { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public String getSubjectCode() { return subjectCode; }
    public int    getCourseId()    { return courseId; }
    public String getCourseName()  { return courseName; }
    public int    getSemester()    { return semester; }
    public int    getCredits()     { return credits; }
    public boolean isElective()    { return isElective; }
    public void setSubjectId(int v)       { this.subjectId = v; }
    public void setSubjectName(String v)  { this.subjectName = v; }
    public void setSubjectCode(String v)  { this.subjectCode = v; }
    public void setCourseId(int v)        { this.courseId = v; }
    public void setCourseName(String v)   { this.courseName = v; }
    public void setSemester(int v)        { this.semester = v; }
    public void setCredits(int v)         { this.credits = v; }
    public void setElective(boolean v)    { this.isElective = v; }
    @Override public String toString()    { return subjectCode + " - " + subjectName; }
}

// ── Attendance ────────────────────────────────────────────────────────────────
class Attendance {
    private int       attId;
    private int       studentId;
    private String    studentName;
    private int       subjectId;
    private String    subjectName;
    private LocalDate attDate;
    private String    status;
    private int       markedBy;

    public Attendance() {}
    public int       getAttId()       { return attId; }
    public int       getStudentId()   { return studentId; }
    public String    getStudentName() { return studentName; }
    public int       getSubjectId()   { return subjectId; }
    public String    getSubjectName() { return subjectName; }
    public LocalDate getAttDate()     { return attDate; }
    public String    getStatus()      { return status; }
    public int       getMarkedBy()    { return markedBy; }
    public void setAttId(int v)          { this.attId = v; }
    public void setStudentId(int v)      { this.studentId = v; }
    public void setStudentName(String v) { this.studentName = v; }
    public void setSubjectId(int v)      { this.subjectId = v; }
    public void setSubjectName(String v) { this.subjectName = v; }
    public void setAttDate(LocalDate v)  { this.attDate = v; }
    public void setStatus(String v)      { this.status = v; }
    public void setMarkedBy(int v)       { this.markedBy = v; }
}

// ── FeePayment ────────────────────────────────────────────────────────────────
class FeePayment {
    private int        paymentId;
    private int        studentId;
    private String     studentName;
    private int        feeId;
    private String     feeType;
    private double     amountPaid;
    private double     fineAmount;
    private LocalDate  paymentDate;
    private LocalDate  dueDate;
    private String     paymentMode;
    private String     receiptNo;
    private String     remarks;

    public FeePayment() {}
    public int       getPaymentId()   { return paymentId; }
    public int       getStudentId()   { return studentId; }
    public String    getStudentName() { return studentName; }
    public int       getFeeId()       { return feeId; }
    public String    getFeeType()     { return feeType; }
    public double    getAmountPaid()  { return amountPaid; }
    public double    getFineAmount()  { return fineAmount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public LocalDate getDueDate()     { return dueDate; }
    public String    getPaymentMode() { return paymentMode; }
    public String    getReceiptNo()   { return receiptNo; }
    public String    getRemarks()     { return remarks; }
    public void setPaymentId(int v)      { this.paymentId = v; }
    public void setStudentId(int v)      { this.studentId = v; }
    public void setStudentName(String v) { this.studentName = v; }
    public void setFeeId(int v)          { this.feeId = v; }
    public void setFeeType(String v)     { this.feeType = v; }
    public void setAmountPaid(double v)  { this.amountPaid = v; }
    public void setFineAmount(double v)  { this.fineAmount = v; }
    public void setPaymentDate(LocalDate v){ this.paymentDate = v; }
    public void setDueDate(LocalDate v)  { this.dueDate = v; }
    public void setPaymentMode(String v) { this.paymentMode = v; }
    public void setReceiptNo(String v)   { this.receiptNo = v; }
    public void setRemarks(String v)     { this.remarks = v; }
}

// ── Result ────────────────────────────────────────────────────────────────────
class Result {
    private int    resultId;
    private int    studentId;
    private String studentName;
    private int    examId;
    private String examName;
    private String subjectName;
    private double marks;
    private int    maxMarks;
    private String grade;
    private boolean isPass;

    public Result() {}
    public int    getResultId()    { return resultId; }
    public int    getStudentId()   { return studentId; }
    public String getStudentName() { return studentName; }
    public int    getExamId()      { return examId; }
    public String getExamName()    { return examName; }
    public String getSubjectName() { return subjectName; }
    public double getMarks()       { return marks; }
    public int    getMaxMarks()    { return maxMarks; }
    public String getGrade()       { return grade; }
    public boolean isPass()        { return isPass; }
    public void setResultId(int v)      { this.resultId = v; }
    public void setStudentId(int v)     { this.studentId = v; }
    public void setStudentName(String v){ this.studentName = v; }
    public void setExamId(int v)        { this.examId = v; }
    public void setExamName(String v)   { this.examName = v; }
    public void setSubjectName(String v){ this.subjectName = v; }
    public void setMarks(double v)      { this.marks = v; }
    public void setMaxMarks(int v)      { this.maxMarks = v; }
    public void setGrade(String v)      { this.grade = v; }
    public void setPass(boolean v)      { this.isPass = v; }
}

// ── Notice ────────────────────────────────────────────────────────────────────
class Notice {
    private int           noticeId;
    private String        title;
    private String        content;
    private int           postedBy;
    private String        postedByName;
    private String        targetRole;
    private boolean       isImportant;
    private LocalDateTime postedAt;
    private LocalDate     expiresAt;

    public Notice() {}
    public int           getNoticeId()    { return noticeId; }
    public String        getTitle()       { return title; }
    public String        getContent()     { return content; }
    public int           getPostedBy()    { return postedBy; }
    public String        getPostedByName(){ return postedByName; }
    public String        getTargetRole()  { return targetRole; }
    public boolean       isImportant()    { return isImportant; }
    public LocalDateTime getPostedAt()    { return postedAt; }
    public LocalDate     getExpiresAt()   { return expiresAt; }
    public void setNoticeId(int v)         { this.noticeId = v; }
    public void setTitle(String v)         { this.title = v; }
    public void setContent(String v)       { this.content = v; }
    public void setPostedBy(int v)         { this.postedBy = v; }
    public void setPostedByName(String v)  { this.postedByName = v; }
    public void setTargetRole(String v)    { this.targetRole = v; }
    public void setImportant(boolean v)    { this.isImportant = v; }
    public void setPostedAt(LocalDateTime v){ this.postedAt = v; }
    public void setExpiresAt(LocalDate v)  { this.expiresAt = v; }
}

// ── Assignment ────────────────────────────────────────────────────────────────
class Assignment {
    private int       assignmentId;
    private String    title;
    private String    description;
    private int       subjectId;
    private String    subjectName;
    private int       teacherId;
    private String    teacherName;
    private LocalDate dueDate;
    private int       maxMarks;
    private LocalDateTime createdAt;

    public Assignment() {}
    public int       getAssignmentId() { return assignmentId; }
    public String    getTitle()        { return title; }
    public String    getDescription()  { return description; }
    public int       getSubjectId()    { return subjectId; }
    public String    getSubjectName()  { return subjectName; }
    public int       getTeacherId()    { return teacherId; }
    public String    getTeacherName()  { return teacherName; }
    public LocalDate getDueDate()      { return dueDate; }
    public int       getMaxMarks()     { return maxMarks; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setAssignmentId(int v)      { this.assignmentId = v; }
    public void setTitle(String v)          { this.title = v; }
    public void setDescription(String v)    { this.description = v; }
    public void setSubjectId(int v)         { this.subjectId = v; }
    public void setSubjectName(String v)    { this.subjectName = v; }
    public void setTeacherId(int v)         { this.teacherId = v; }
    public void setTeacherName(String v)    { this.teacherName = v; }
    public void setDueDate(LocalDate v)     { this.dueDate = v; }
    public void setMaxMarks(int v)          { this.maxMarks = v; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
}

// ── LibraryBook ───────────────────────────────────────────────────────────────
class LibraryBook {
    private int    bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int    pubYear;
    private int    totalCopies;
    private int    available;

    public LibraryBook() {}
    public int    getBookId()      { return bookId; }
    public String getTitle()       { return title; }
    public String getAuthor()      { return author; }
    public String getIsbn()        { return isbn; }
    public String getCategory()    { return category; }
    public String getPublisher()   { return publisher; }
    public int    getPubYear()     { return pubYear; }
    public int    getTotalCopies() { return totalCopies; }
    public int    getAvailable()   { return available; }
    public void setBookId(int v)       { this.bookId = v; }
    public void setTitle(String v)     { this.title = v; }
    public void setAuthor(String v)    { this.author = v; }
    public void setIsbn(String v)      { this.isbn = v; }
    public void setCategory(String v)  { this.category = v; }
    public void setPublisher(String v) { this.publisher = v; }
    public void setPubYear(int v)      { this.pubYear = v; }
    public void setTotalCopies(int v)  { this.totalCopies = v; }
    public void setAvailable(int v)    { this.available = v; }
    @Override public String toString() { return title + " by " + author; }
}
