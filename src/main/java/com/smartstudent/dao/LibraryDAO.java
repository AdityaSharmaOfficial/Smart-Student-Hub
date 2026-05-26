package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LibraryDAO {

    public List<Map<String, Object>> searchBooks(String keyword) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT book_id, title, author, isbn, category, publisher, pub_year, total_copies, available " +
                     "FROM library_books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ? ORDER BY title";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q); ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("book_id",   rs.getInt("book_id"));
                    row.put("title",     rs.getString("title"));
                    row.put("author",    rs.getString("author"));
                    row.put("isbn",      rs.getString("isbn"));
                    row.put("category",  rs.getString("category"));
                    row.put("publisher", rs.getString("publisher"));
                    row.put("pub_year",  rs.getInt("pub_year"));
                    row.put("total",     rs.getInt("total_copies"));
                    row.put("available", rs.getInt("available"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getAllBooks() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT book_id, title, author, isbn, category, publisher, pub_year, total_copies, available " +
                     "FROM library_books ORDER BY title";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("book_id",   rs.getInt("book_id"));
                row.put("title",     rs.getString("title"));
                row.put("author",    rs.getString("author"));
                row.put("isbn",      rs.getString("isbn"));
                row.put("category",  rs.getString("category"));
                row.put("publisher", rs.getString("publisher"));
                row.put("pub_year",  rs.getInt("pub_year"));
                row.put("total",     rs.getInt("total_copies"));
                row.put("available", rs.getInt("available"));
                list.add(row);
            }
        }
        return list;
    }

    public boolean issueBook(int bookId, Integer studentId, Integer teacherId, LocalDate dueDate) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT available FROM library_books WHERE book_id=? FOR UPDATE")) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("available") <= 0) {
                        conn.rollback(); return false;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO issued_books (book_id,student_id,teacher_id,issue_date,due_date,status) VALUES(?,?,?,CURDATE(),?,'Issued')")) {
                ps.setInt(1, bookId);
                if (studentId != null) ps.setInt(2, studentId); else ps.setNull(2, Types.INTEGER);
                if (teacherId != null) ps.setInt(3, teacherId); else ps.setNull(3, Types.INTEGER);
                ps.setDate(4, java.sql.Date.valueOf(dueDate));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE library_books SET available=available-1 WHERE book_id=?")) {
                ps.setInt(1, bookId); ps.executeUpdate();
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            conn.rollback(); throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean returnBook(int issueId) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        conn.setAutoCommit(false);
        try {
            int bookId = -1; double fine = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT book_id, due_date FROM issued_books WHERE issue_id=?")) {
                ps.setInt(1, issueId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                        java.sql.Date due = rs.getDate("due_date");
                        if (due != null && LocalDate.now().isAfter(due.toLocalDate()))
                            fine = java.time.temporal.ChronoUnit.DAYS.between(due.toLocalDate(), LocalDate.now()) * 2.0;
                    }
                }
            }
            if (bookId == -1) { conn.rollback(); return false; }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE issued_books SET return_date=CURDATE(), fine_amount=?, status='Returned' WHERE issue_id=?")) {
                ps.setDouble(1, fine); ps.setInt(2, issueId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE library_books SET available=available+1 WHERE book_id=?")) {
                ps.setInt(1, bookId); ps.executeUpdate();
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            conn.rollback(); throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Map<String, Object>> getIssuedBooks() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT ib.issue_id, lb.title, lb.author, " +
                     "COALESCE(s.full_name, t.full_name) AS borrower, " +
                     "CASE WHEN s.student_id IS NOT NULL THEN 'Student' ELSE 'Teacher' END AS type, " +
                     "ib.issue_date, ib.due_date, ib.return_date, ib.fine_amount, ib.status " +
                     "FROM issued_books ib JOIN library_books lb ON ib.book_id=lb.book_id " +
                     "LEFT JOIN students s ON ib.student_id=s.student_id " +
                     "LEFT JOIN teachers t ON ib.teacher_id=t.teacher_id " +
                     "ORDER BY ib.issue_date DESC";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("issue_id",    rs.getInt("issue_id"));
                row.put("title",       rs.getString("title"));
                row.put("author",      rs.getString("author"));
                row.put("borrower",    rs.getString("borrower"));
                row.put("type",        rs.getString("type"));
                row.put("issue_date",  rs.getDate("issue_date"));
                row.put("due_date",    rs.getDate("due_date"));
                row.put("return_date", rs.getDate("return_date"));
                row.put("fine",        rs.getDouble("fine_amount"));
                row.put("status",      rs.getString("status"));
                list.add(row);
            }
        }
        return list;
    }
}
