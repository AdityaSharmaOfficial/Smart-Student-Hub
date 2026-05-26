package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AssignmentDAO {

    public List<Map<String, Object>> getBySubject(int subjectId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.assignment_id, a.title, a.description, a.due_date, a.max_marks, " +
                     "t.full_name AS teacher_name " +
                     "FROM assignments a JOIN teachers t ON a.teacher_id=t.teacher_id " +
                     "WHERE a.subject_id=? ORDER BY a.due_date DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("assignment_id", rs.getInt("assignment_id"));
                    row.put("title",         rs.getString("title"));
                    row.put("description",   rs.getString("description"));
                    row.put("due_date",      rs.getDate("due_date"));
                    row.put("max_marks",     rs.getInt("max_marks"));
                    row.put("teacher_name",  rs.getString("teacher_name"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getByTeacher(int teacherId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.assignment_id, a.title, a.due_date, a.max_marks, sub.subject_name, " +
                     "(SELECT COUNT(*) FROM submissions s WHERE s.assignment_id=a.assignment_id) AS submissions " +
                     "FROM assignments a JOIN subjects sub ON a.subject_id=sub.subject_id " +
                     "WHERE a.teacher_id=? ORDER BY a.created_at DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("assignment_id", rs.getInt("assignment_id"));
                    row.put("title",         rs.getString("title"));
                    row.put("subject_name",  rs.getString("subject_name"));
                    row.put("due_date",      rs.getDate("due_date"));
                    row.put("max_marks",     rs.getInt("max_marks"));
                    row.put("submissions",   rs.getInt("submissions"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public boolean addAssignment(String title, String desc, int subjectId, int teacherId,
                                 LocalDate dueDate, int maxMarks) throws SQLException {
        String sql = "INSERT INTO assignments (title, description, subject_id, teacher_id, due_date, max_marks) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, title); ps.setString(2, desc);
            ps.setInt(3, subjectId); ps.setInt(4, teacherId);
            ps.setDate(5, java.sql.Date.valueOf(dueDate)); ps.setInt(6, maxMarks);
            return ps.executeUpdate() > 0;
        }
    }
}
