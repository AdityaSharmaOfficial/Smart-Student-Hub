package com.smartstudent.model;

import java.time.LocalDate;

public class Student {
    private int       studentId;
    private int       userId;
    private String    admissionNo;
    private String    fullName;
    private LocalDate dob;
    private String    gender;
    private String    email;
    private String    phone;
    private String    address;
    private int       courseId;
    private String    courseName;
    private int       currentSemester;
    private String    guardianName;
    private String    guardianPhone;
    private String    guardianRelation;
    private String    bloodGroup;
    private String    photoPath;
    private LocalDate admissionDate;
    private boolean   isActive;

    public Student() {}

    // --- Getters ---
    public int       getStudentId()       { return studentId; }
    public int       getUserId()          { return userId; }
    public String    getAdmissionNo()     { return admissionNo; }
    public String    getFullName()        { return fullName; }
    public LocalDate getDob()             { return dob; }
    public String    getGender()          { return gender; }
    public String    getEmail()           { return email; }
    public String    getPhone()           { return phone; }
    public String    getAddress()         { return address; }
    public int       getCourseId()        { return courseId; }
    public String    getCourseName()      { return courseName; }
    public int       getCurrentSemester() { return currentSemester; }
    public String    getGuardianName()    { return guardianName; }
    public String    getGuardianPhone()   { return guardianPhone; }
    public String    getGuardianRelation(){ return guardianRelation; }
    public String    getBloodGroup()      { return bloodGroup; }
    public String    getPhotoPath()       { return photoPath; }
    public LocalDate getAdmissionDate()   { return admissionDate; }
    public boolean   isActive()           { return isActive; }

    // --- Setters ---
    public void setStudentId(int v)          { this.studentId = v; }
    public void setUserId(int v)             { this.userId = v; }
    public void setAdmissionNo(String v)     { this.admissionNo = v; }
    public void setFullName(String v)        { this.fullName = v; }
    public void setDob(LocalDate v)          { this.dob = v; }
    public void setGender(String v)          { this.gender = v; }
    public void setEmail(String v)           { this.email = v; }
    public void setPhone(String v)           { this.phone = v; }
    public void setAddress(String v)         { this.address = v; }
    public void setCourseId(int v)           { this.courseId = v; }
    public void setCourseName(String v)      { this.courseName = v; }
    public void setCurrentSemester(int v)    { this.currentSemester = v; }
    public void setGuardianName(String v)    { this.guardianName = v; }
    public void setGuardianPhone(String v)   { this.guardianPhone = v; }
    public void setGuardianRelation(String v){ this.guardianRelation = v; }
    public void setBloodGroup(String v)      { this.bloodGroup = v; }
    public void setPhotoPath(String v)       { this.photoPath = v; }
    public void setAdmissionDate(LocalDate v){ this.admissionDate = v; }
    public void setActive(boolean v)         { this.isActive = v; }

    @Override
    public String toString() { return admissionNo + " - " + fullName; }
}
