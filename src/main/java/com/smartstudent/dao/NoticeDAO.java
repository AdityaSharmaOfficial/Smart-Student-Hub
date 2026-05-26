package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class NoticeDAO {

    public List<Map<String, Object>> getNoticesForRole(String role) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT n.notice_id, n.title, n.content, n.is_important, n.posted_at, " +
                     "n.expires_at, n.target_role, u.username AS posted_by " +
                     "FROM notices n JOIN users u ON n.posted_by=u.user_id " +
                     "WHERE (n.target_role='All' OR n.target_role=?) " +
                     "AND (n.expires_at IS NULL OR n.expires_at >= CURDATE()) " +
                     "ORDER BY n.is_important DESC, n.posted_at DESC";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("notice_id",  rs.getInt("notice_id"));
                    row.put("title",      rs.getString("title"));
                    row.put("content",    rs.getString("content"));
                    row.put("important",  rs.getBoolean("is_important"));
                    row.put("posted_at",  rs.getTimestamp("posted_at"));
                    row.put("expires_at", rs.getDate("expires_at"));
                    row.put("target",     rs.getString("target_role"));
                    row.put("posted_by",  rs.getString("posted_by"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public boolean addNotice(String title, String content, int postedBy,
                             String targetRole, boolean important, LocalDate expiresAt) throws SQLException {
        String sql = "INSERT INTO notices (title, content, posted_by, target_role, is_important, expires_at) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, postedBy);
            ps.setString(4, targetRole);
            ps.setBoolean(5, important);
            ps.setDate(6, expiresAt != null ? java.sql.Date.valueOf(expiresAt) : null);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteNotice(int noticeId) throws SQLException {
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(
                "DELETE FROM notices WHERE notice_id=?")) {
            ps.setInt(1, noticeId);
            return ps.executeUpdate() > 0;
        }
    }
}
