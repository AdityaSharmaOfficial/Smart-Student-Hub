package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class FeeDAO {

    public List<Map<String, Object>> getFeeStructure(int courseId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT fee_id, semester, fee_type, amount, academic_year " +
                     "FROM fee_structure WHERE course_id=? ORDER BY semester, fee_type";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fee_id",       rs.getInt("fee_id"));
                    row.put("semester",     rs.getInt("semester"));
                    row.put("fee_type",     rs.getString("fee_type"));
                    row.put("amount",       rs.getDouble("amount"));
                    row.put("academic_year",rs.getString("academic_year"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getPaymentHistory(int studentId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT fp.payment_id, fp.receipt_no, fp.amount_paid, fp.fine_amount, " +
                     "fp.payment_date, fp.payment_mode, fs.fee_type, fs.semester " +
                     "FROM fee_payments fp " +
                     "JOIN fee_structure fs ON fp.fee_id=fs.fee_id " +
                     "WHERE fp.student_id=? ORDER BY fp.payment_date DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("receipt_no",   rs.getString("receipt_no"));
                    row.put("fee_type",     rs.getString("fee_type"));
                    row.put("semester",     rs.getInt("semester"));
                    row.put("amount_paid",  rs.getDouble("amount_paid"));
                    row.put("fine_amount",  rs.getDouble("fine_amount"));
                    row.put("total",        rs.getDouble("amount_paid") + rs.getDouble("fine_amount"));
                    row.put("payment_date", rs.getDate("payment_date"));
                    row.put("payment_mode", rs.getString("payment_mode"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public double getPendingDues(int studentId, int courseId) throws SQLException {
        String totalDue = "SELECT COALESCE(SUM(fs.amount),0) FROM fee_structure fs " +
                          "WHERE fs.course_id=?";
        String totalPaid = "SELECT COALESCE(SUM(fp.amount_paid),0) FROM fee_payments fp " +
                           "WHERE fp.student_id=?";
        double due = 0, paid = 0;
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(totalDue)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) due = rs.getDouble(1); }
        }
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(totalPaid)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) paid = rs.getDouble(1); }
        }
        return Math.max(0, due - paid);
    }

    public String generateReceiptNo() throws SQLException {
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM fee_payments")) {
            int n = rs.next() ? rs.getInt(1) : 0;
            return String.format("RCP%d%04d", LocalDate.now().getYear(), n + 1);
        }
    }

    public int recordPayment(int studentId, int feeId, double amount, double fine,
                             LocalDate paymentDate, LocalDate dueDate,
                             String mode, String receiptNo, String remarks) throws SQLException {
        String sql = "INSERT INTO fee_payments (student_id, fee_id, amount_paid, fine_amount, " +
                     "payment_date, due_date, payment_mode, receipt_no, remarks) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, feeId);
            ps.setDouble(3, amount);
            ps.setDouble(4, fine);
            ps.setDate(5, java.sql.Date.valueOf(paymentDate));
            ps.setDate(6, dueDate != null ? java.sql.Date.valueOf(dueDate) : null);
            ps.setString(7, mode);
            ps.setString(8, receiptNo);
            ps.setString(9, remarks);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public double getTotalPendingAllStudents() throws SQLException {
        String sql = "SELECT " +
                     "(SELECT COALESCE(SUM(fs.amount),0) FROM fee_structure fs " +
                     " JOIN students s ON s.course_id=fs.course_id WHERE s.is_active=TRUE) - " +
                     "(SELECT COALESCE(SUM(fp.amount_paid),0) FROM fee_payments fp " +
                     " JOIN students s ON s.student_id=fp.student_id WHERE s.is_active=TRUE) AS pending";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? Math.max(0, rs.getDouble("pending")) : 0;
        }
    }

    public List<Map<String, Object>> getStudentsWithPendingFees() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.admission_no, s.full_name, c.course_name, " +
                     "(SELECT COALESCE(SUM(fs.amount),0) FROM fee_structure fs WHERE fs.course_id=s.course_id) - " +
                     "(SELECT COALESCE(SUM(fp.amount_paid),0) FROM fee_payments fp WHERE fp.student_id=s.student_id) AS pending " +
                     "FROM students s JOIN courses c ON s.course_id=c.course_id " +
                     "WHERE s.is_active=TRUE HAVING pending > 0 ORDER BY pending DESC";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("student_id",  rs.getInt("student_id"));
                row.put("admission_no",rs.getString("admission_no"));
                row.put("full_name",   rs.getString("full_name"));
                row.put("course_name", rs.getString("course_name"));
                row.put("pending",     rs.getDouble("pending"));
                list.add(row);
            }
        }
        return list;
    }
}
