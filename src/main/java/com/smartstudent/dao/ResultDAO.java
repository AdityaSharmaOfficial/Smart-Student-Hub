package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

// Public wrapper so other packages can import these DAOs

public class ResultDAO {

    public List<Map<String, Object>> getResultsByStudent(int studentId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT r.result_id, e.exam_name, e.exam_type, e.max_marks, e.pass_marks, " +
                     "sub.subject_name, sub.subject_code, r.marks, r.grade, r.is_pass, e.exam_date " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id=e.exam_id " +
                     "JOIN subjects sub ON e.subject_id=sub.subject_id " +
                     "WHERE r.student_id=? ORDER BY e.exam_date DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("exam_name",    rs.getString("exam_name"));
                    row.put("exam_type",    rs.getString("exam_type"));
                    row.put("subject_name", rs.getString("subject_name"));
                    row.put("subject_code", rs.getString("subject_code"));
                    row.put("marks",        rs.getDouble("marks"));
                    row.put("max_marks",    rs.getInt("max_marks"));
                    row.put("grade",        rs.getString("grade"));
                    row.put("is_pass",      rs.getBoolean("is_pass"));
                    row.put("exam_date",    rs.getDate("exam_date"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getResultsByExam(int examId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.admission_no, s.full_name, r.marks, r.grade, r.is_pass " +
                     "FROM results r JOIN students s ON r.student_id=s.student_id " +
                     "WHERE r.exam_id=? ORDER BY r.marks DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("admission_no", rs.getString("admission_no"));
                    row.put("full_name",    rs.getString("full_name"));
                    row.put("marks",        rs.getDouble("marks"));
                    row.put("grade",        rs.getString("grade"));
                    row.put("is_pass",      rs.getBoolean("is_pass"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public boolean saveResult(int studentId, int examId, double marks, String grade, boolean isPass) throws SQLException {
        String sql = "INSERT INTO results (student_id, exam_id, marks, grade, is_pass) VALUES (?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE marks=VALUES(marks), grade=VALUES(grade), is_pass=VALUES(is_pass)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, examId);
            ps.setDouble(3, marks);  ps.setString(4, grade); ps.setBoolean(5, isPass);
            return ps.executeUpdate() > 0;
        }
    }

    public static String calculateGrade(double marks, int maxMarks) {
        double pct = (marks / maxMarks) * 100;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }

    public List<Map<String, Object>> getToppers(int examId, int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.admission_no, s.full_name, r.marks, r.grade " +
                     "FROM results r JOIN students s ON r.student_id=s.student_id " +
                     "WHERE r.exam_id=? ORDER BY r.marks DESC LIMIT ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, examId); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("rank", rank++);
                    row.put("admission_no", rs.getString("admission_no"));
                    row.put("full_name",    rs.getString("full_name"));
                    row.put("marks",        rs.getDouble("marks"));
                    row.put("grade",        rs.getString("grade"));
                    list.add(row);
                }
            }
        }
        return list;
    }
}
