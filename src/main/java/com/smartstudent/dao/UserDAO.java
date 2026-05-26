package com.smartstudent.dao;

import com.smartstudent.config.DatabaseConfig;
import com.smartstudent.model.User;
import com.smartstudent.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.role_id, r.role_name, u.is_active " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password_hash");
                    if (!PasswordUtil.verify(password, stored)) return null;
                    if (!rs.getBoolean("is_active")) return null;

                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(stored);
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setActive(true);
                    return user;
                }
            }
        }
        return null;
    }

    public int createUser(String username, String password, int roleId) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.sha256(password));
            ps.setInt(3, roleId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean changePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.sha256(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setActiveStatus(int userId, boolean active) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.role_id, r.role_name, u.is_active " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id ORDER BY u.user_id";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getInt("role_id"),
                    rs.getString("role_name"),
                    rs.getBoolean("is_active")
                );
                list.add(u);
            }
        }
        return list;
    }
}
