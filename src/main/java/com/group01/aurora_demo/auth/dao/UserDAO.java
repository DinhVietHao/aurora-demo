package com.group01.aurora_demo.auth.dao;

import java.sql.*;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class UserDAO {

    public User findById(long userId) {
        String sql = "SELECT UserID, Email, FullName, PasswordHash, AuthProvider, CreatedAt "
                + "FROM dbo.Users WHERE UserID = ?";
        try (Connection con = DataSourceProvider.get().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getLong("UserID"));
                    u.setEmail(rs.getString("Email"));
                    u.setFullName(rs.getString("FullName"));
                    u.setPasswordHash(rs.getString("PasswordHash"));
                    u.setAuthProvider(rs.getString("AuthProvider"));
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    u.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User findByEmailAndProvider(String email, String provider) {
        String sql = "SELECT UserID, Email, FullName, Password, CreatedAt, AuthProvider FROM dbo.Users WHERE Email = ? AND AuthProvider = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, provider);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User u = new User();
                u.setId(rs.getLong("UserID"));
                u.setEmail(rs.getString("Email"));
                u.setFullName(rs.getString("FullName"));
                u.setPasswordHash(rs.getString("Password"));
                Timestamp ts = rs.getTimestamp("CreatedAt");
                u.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                u.setAuthProvider(rs.getString("AuthProvider"));
                return u;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean createAccount(User user) {
        String sql = "INSERT INTO dbo.Users (Email, FullName, Password, AuthProvider) VALUES (?, ?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getAuthProvider());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("createAccount failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateLocalPasswordByUserId(long userId, String newHash) {
        final String sql = "UPDATE dbo.Users SET Password=? WHERE UserID=? AND AuthProvider='LOCAL'";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}