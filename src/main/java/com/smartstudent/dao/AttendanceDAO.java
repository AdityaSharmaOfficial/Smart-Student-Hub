package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceDAO {

    public boolean markAttendance(int studentId, int subjectId, LocalDate date,
                                  String status, int markedBy) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, att_date, status, marked_by) " +
                     "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE status=VALUES(status)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setString(4, status);
            ps.setInt(5, markedBy);
            return ps.executeUpdate() > 0;
        }
    }

    /** Returns list of maps: student_id, full_name, status for a given subject+date */
    public List<Map<String, Object>> getAttendanceByDate(int subjectId, LocalDate date) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.full_name, s.admission_no, " +
                     "COALESCE(a.status,'Absent') AS status " +
                     "FROM students s " +
                     "LEFT JOIN attendance a ON s.student_id=a.student_id " +
                     "  AND a.subject_id=? AND a.att_date=? " +
                     "WHERE s.is_active=TRUE ORDER BY s.full_name";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("student_id",   rs.getInt("student_id"));
                    row.put("admission_no", rs.getString("admission_no"));
                    row.put("full_name",    rs.getString("full_name"));
                    row.put("status",       rs.getString("status"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    /** Returns attendance % for a student in a subject */
    public double getAttendancePercent(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance WHERE student_id=? AND subject_id=?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    if (total == 0) return 0;
                    return (rs.getDouble("present") / total) * 100.0;
                }
            }
        }
        return 0;
    }

    /** Monthly summary for a student: returns rows with subject, total, present, percent */
    public List<Map<String, Object>> getMonthlySummary(int studentId, int month, int year) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sub.subject_name, sub.subject_code, " +
                     "COUNT(a.att_id) AS total, " +
                     "SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) AS present " +
                     "FROM subjects sub " +
                     "JOIN enrollments e ON e.subject_id=sub.subject_id AND e.student_id=? " +
                     "LEFT JOIN attendance a ON a.subject_id=sub.subject_id AND a.student_id=? " +
                     "  AND MONTH(a.att_date)=? AND YEAR(a.att_date)=? " +
                     "GROUP BY sub.subject_id ORDER BY sub.subject_name";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, studentId);
            ps.setInt(3, month);     ps.setInt(4, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    int total   = rs.getInt("total");
                    int present = rs.getInt("present");
                    double pct  = total > 0 ? (present * 100.0 / total) : 0;
                    row.put("subject_code", rs.getString("subject_code"));
                    row.put("subject_name", rs.getString("subject_name"));
                    row.put("total",   total);
                    row.put("present", present);
                    row.put("percent", String.format("%.1f%%", pct));
                    row.put("warning", pct < 75);
                    list.add(row);
                }
            }
        }
        return list;
    }

    /** Students with attendance below threshold in a subject */
    public List<Map<String, Object>> getLowAttendanceStudents(int subjectId, double threshold) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.admission_no, s.full_name, " +
                     "COUNT(a.att_id) AS total, " +
                     "SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) AS present " +
                     "FROM students s " +
                     "JOIN attendance a ON a.student_id=s.student_id AND a.subject_id=? " +
                     "GROUP BY s.student_id " +
                     "HAVING (present/total*100) < ? ORDER BY (present/total)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setDouble(2, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    int total   = rs.getInt("total");
                    int present = rs.getInt("present");
                    row.put("student_id",   rs.getInt("student_id"));
                    row.put("admission_no", rs.getString("admission_no"));
                    row.put("full_name",    rs.getString("full_name"));
                    row.put("total",   total);
                    row.put("present", present);
                    row.put("percent", String.format("%.1f%%", total > 0 ? present*100.0/total : 0));
                    list.add(row);
                }
            }
        }
        return list;
    }
}
