package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setAdmissionNo(rs.getString("admission_no"));
        s.setFullName(rs.getString("full_name"));
        Date dob = rs.getDate("dob");
        if (dob != null) s.setDob(dob.toLocalDate());
        s.setGender(rs.getString("gender"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        s.setCourseId(rs.getInt("course_id"));
        s.setCourseName(rs.getString("course_name"));
        s.setCurrentSemester(rs.getInt("current_semester"));
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        s.setGuardianRelation(rs.getString("guardian_relation"));
        s.setBloodGroup(rs.getString("blood_group"));
        s.setPhotoPath(rs.getString("photo_path"));
        Date ad = rs.getDate("admission_date");
        if (ad != null) s.setAdmissionDate(ad.toLocalDate());
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }

    private static final String SELECT_BASE =
        "SELECT s.*, c.course_name FROM students s " +
        "JOIN courses c ON s.course_id = c.course_id ";

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY s.student_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Student getById(int studentId) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.student_id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Student getByUserId(int userId) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.user_id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Student> search(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_BASE +
            "WHERE s.full_name LIKE ? OR s.admission_no LIKE ? OR s.email LIKE ? OR s.phone LIKE ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q); ps.setString(2, q);
            ps.setString(3, q); ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Student> getByCourse(int courseId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE s.course_id = ? AND s.is_active = TRUE ORDER BY s.full_name";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Student> getBySemester(int courseId, int semester) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE s.course_id = ? AND s.current_semester = ? AND s.is_active = TRUE";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId); ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(Student s) throws SQLException {
        String sql = "INSERT INTO students (user_id, admission_no, full_name, dob, gender, email, phone, address, " +
                     "course_id, current_semester, guardian_name, guardian_phone, guardian_relation, blood_group, " +
                     "photo_path, admission_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getAdmissionNo());
            ps.setString(3, s.getFullName());
            ps.setDate(4, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(5, s.getGender());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getAddress());
            ps.setInt(9, s.getCourseId());
            ps.setInt(10, s.getCurrentSemester());
            ps.setString(11, s.getGuardianName());
            ps.setString(12, s.getGuardianPhone());
            ps.setString(13, s.getGuardianRelation());
            ps.setString(14, s.getBloodGroup());
            ps.setString(15, s.getPhotoPath());
            ps.setDate(16, s.getAdmissionDate() != null ? Date.valueOf(s.getAdmissionDate()) : Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET full_name=?, dob=?, gender=?, email=?, phone=?, address=?, " +
                     "course_id=?, current_semester=?, guardian_name=?, guardian_phone=?, guardian_relation=?, " +
                     "blood_group=?, photo_path=? WHERE student_id=?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setDate(2, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(3, s.getGender());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getAddress());
            ps.setInt(7, s.getCourseId());
            ps.setInt(8, s.getCurrentSemester());
            ps.setString(9, s.getGuardianName());
            ps.setString(10, s.getGuardianPhone());
            ps.setString(11, s.getGuardianRelation());
            ps.setString(12, s.getBloodGroup());
            ps.setString(13, s.getPhotoPath());
            ps.setInt(14, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int studentId) throws SQLException {
        String sql = "UPDATE students SET is_active = FALSE WHERE student_id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTotalActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE is_active = TRUE";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String generateAdmissionNo() throws SQLException {
        int year = LocalDate.now().getYear();
        String sql = "SELECT COUNT(*) FROM students WHERE YEAR(admission_date) = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                return String.format("ADM%d%03d", year, count + 1);
            }
        }
    }
}
