package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    private Teacher map(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getInt("teacher_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setEmployeeId(rs.getString("employee_id"));
        t.setFullName(rs.getString("full_name"));
        Date dob = rs.getDate("dob");
        if (dob != null) t.setDob(dob.toLocalDate());
        t.setGender(rs.getString("gender"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setAddress(rs.getString("address"));
        t.setDeptId(rs.getInt("dept_id"));
        t.setDeptName(rs.getString("dept_name"));
        t.setQualification(rs.getString("qualification"));
        Date jd = rs.getDate("joining_date");
        if (jd != null) t.setJoiningDate(jd.toLocalDate());
        t.setPhotoPath(rs.getString("photo_path"));
        t.setActive(rs.getBoolean("is_active"));
        return t;
    }

    private static final String BASE =
        "SELECT t.*, d.dept_name FROM teachers t " +
        "LEFT JOIN departments d ON t.dept_id = d.dept_id ";

    public List<Teacher> getAll() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(BASE + "ORDER BY t.full_name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Teacher getById(int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(BASE + "WHERE t.teacher_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Teacher getByUserId(int userId) throws SQLException {
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(BASE + "WHERE t.user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Teacher> search(String kw) throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = BASE + "WHERE t.full_name LIKE ? OR t.employee_id LIKE ? OR t.email LIKE ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            String q = "%" + kw + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(Teacher t) throws SQLException {
        String sql = "INSERT INTO teachers (user_id, employee_id, full_name, dob, gender, email, phone, address, " +
                     "dept_id, qualification, joining_date, photo_path) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getEmployeeId());
            ps.setString(3, t.getFullName());
            ps.setDate(4, t.getDob() != null ? Date.valueOf(t.getDob()) : null);
            ps.setString(5, t.getGender());
            ps.setString(6, t.getEmail());
            ps.setString(7, t.getPhone());
            ps.setString(8, t.getAddress());
            ps.setInt(9, t.getDeptId());
            ps.setString(10, t.getQualification());
            ps.setDate(11, t.getJoiningDate() != null ? Date.valueOf(t.getJoiningDate()) : null);
            ps.setString(12, t.getPhotoPath());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Teacher t) throws SQLException {
        String sql = "UPDATE teachers SET full_name=?, dob=?, gender=?, email=?, phone=?, address=?, " +
                     "dept_id=?, qualification=?, joining_date=?, photo_path=? WHERE teacher_id=?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getFullName());
            ps.setDate(2, t.getDob() != null ? Date.valueOf(t.getDob()) : null);
            ps.setString(3, t.getGender());
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getPhone());
            ps.setString(6, t.getAddress());
            ps.setInt(7, t.getDeptId());
            ps.setString(8, t.getQualification());
            ps.setDate(9, t.getJoiningDate() != null ? Date.valueOf(t.getJoiningDate()) : null);
            ps.setString(10, t.getPhotoPath());
            ps.setInt(11, t.getTeacherId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE teachers SET is_active=FALSE WHERE teacher_id=?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTotalActive() throws SQLException {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM teachers WHERE is_active=TRUE")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String generateEmployeeId() throws SQLException {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM teachers")) {
            int n = rs.next() ? rs.getInt(1) : 0;
            return String.format("EMP%03d", n + 1);
        }
    }
}
