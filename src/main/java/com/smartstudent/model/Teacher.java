package com.smartstudent.model;

import java.time.LocalDate;

public class Teacher {
    private int       teacherId;
    private int       userId;
    private String    employeeId;
    private String    fullName;
    private LocalDate dob;
    private String    gender;
    private String    email;
    private String    phone;
    private String    address;
    private int       deptId;
    private String    deptName;
    private String    qualification;
    private LocalDate joiningDate;
    private String    photoPath;
    private boolean   isActive;

    public Teacher() {}

    public int       getTeacherId()    { return teacherId; }
    public int       getUserId()       { return userId; }
    public String    getEmployeeId()   { return employeeId; }
    public String    getFullName()     { return fullName; }
    public LocalDate getDob()          { return dob; }
    public String    getGender()       { return gender; }
    public String    getEmail()        { return email; }
    public String    getPhone()        { return phone; }
    public String    getAddress()      { return address; }
    public int       getDeptId()       { return deptId; }
    public String    getDeptName()     { return deptName; }
    public String    getQualification(){ return qualification; }
    public LocalDate getJoiningDate()  { return joiningDate; }
    public String    getPhotoPath()    { return photoPath; }
    public boolean   isActive()        { return isActive; }

    public void setTeacherId(int v)         { this.teacherId = v; }
    public void setUserId(int v)            { this.userId = v; }
    public void setEmployeeId(String v)     { this.employeeId = v; }
    public void setFullName(String v)       { this.fullName = v; }
    public void setDob(LocalDate v)         { this.dob = v; }
    public void setGender(String v)         { this.gender = v; }
    public void setEmail(String v)          { this.email = v; }
    public void setPhone(String v)          { this.phone = v; }
    public void setAddress(String v)        { this.address = v; }
    public void setDeptId(int v)            { this.deptId = v; }
    public void setDeptName(String v)       { this.deptName = v; }
    public void setQualification(String v)  { this.qualification = v; }
    public void setJoiningDate(LocalDate v) { this.joiningDate = v; }
    public void setPhotoPath(String v)      { this.photoPath = v; }
    public void setActive(boolean v)        { this.isActive = v; }

    @Override
    public String toString() { return employeeId + " - " + fullName; }
}
