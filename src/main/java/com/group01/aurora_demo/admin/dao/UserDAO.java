package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UserDAO for admin package - provides user management operations
 * This is separate from auth.dao.UserDAO to avoid conflicts
 */
public class UserDAO {

    /**
     * Get all users with pagination
     */
    public List<User> getAllUsers(int page, int pageSize) {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sql = """
            SELECT u.UserID, u.Email, u.FullName, u.Status, u.AuthProvider, u.AvatarUrl, u.CreatedAt,
                   STUFF((SELECT ', ' + r.RoleName
                          FROM UserRoles ur
                          JOIN Roles r ON ur.RoleCode = r.RoleCode
                          WHERE ur.UserID = u.UserID
                          FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS Roles
            FROM Users u
            ORDER BY u.CreatedAt DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Search users with filters
     */
    public List<User> searchUsers(String keyword, String status, String role, int page, int pageSize) {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
            SELECT u.UserID, u.Email, u.FullName, u.Status, u.AuthProvider, u.AvatarUrl, u.CreatedAt,
                   STUFF((SELECT ', ' + r.RoleName
                          FROM UserRoles ur
                          JOIN Roles r ON ur.RoleCode = r.RoleCode
                          WHERE ur.UserID = u.UserID
                          FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS Roles
            FROM Users u
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (u.FullName LIKE ? OR u.Email LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND u.Status = ?");
            params.add(status);
        }

        if (role != null && !role.isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM UserRoles ur JOIN Roles r ON ur.RoleCode = r.RoleCode WHERE ur.UserID = u.UserID AND r.RoleCode = ?)");
            params.add(role);
        }

        sql.append(" ORDER BY u.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Count total users
     */
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM Users";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Count search results
     */
    public int countSearchResults(String keyword, String status, String role) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users u WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (u.FullName LIKE ? OR u.Email LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND u.Status = ?");
            params.add(status);
        }
        
        if (role != null && !role.isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM UserRoles ur JOIN Roles r ON ur.RoleCode = r.RoleCode WHERE ur.UserID = u.UserID AND r.RoleCode = ?)");
            params.add(role);
        }
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting search results: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Find user by ID
     */
    public User findById(long userId) {
        String sql = """
            SELECT u.UserID, u.Email, u.FullName, u.Status, u.AuthProvider, u.AvatarUrl, u.CreatedAt,
                   STUFF((SELECT ', ' + r.RoleName
                          FROM UserRoles ur
                          JOIN Roles r ON ur.RoleCode = r.RoleCode
                          WHERE ur.UserID = u.UserID
                          FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS Roles
            FROM Users u
            WHERE u.UserID = ?
        """;

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, Status = ?, AvatarUrl = ? WHERE UserID = ?";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getStatus());
            ps.setString(4, user.getAvatarUrl());
            ps.setLong(5, user.getUserID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update user roles
     */
    public boolean updateUserRoles(long userId, String[] roleCodes) {
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete existing roles
                String deleteSql = "DELETE FROM UserRoles WHERE UserID = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }

                // Insert new roles
                String insertSql = "INSERT INTO UserRoles (UserID, RoleCode) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (String roleCode : roleCodes) {
                        ps.setLong(1, userId);
                        ps.setString(2, roleCode);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error updating user roles: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error in updateUserRoles: " + e.getMessage());
        }
        return false;
    }

    /**
     * Toggle user status (active/locked)
     */
    public boolean toggleUserStatus(long userId) {
        String sql = "UPDATE Users SET Status = CASE WHEN Status = 'active' THEN 'locked' ELSE 'active' END WHERE UserID = ?";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error toggling user status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Extract User object from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getLong("UserID"));
        user.setEmail(rs.getString("Email"));
        user.setFullName(rs.getString("FullName"));
        user.setStatus(rs.getString("Status"));
        user.setAuthProvider(rs.getString("AuthProvider"));
        user.setAvatarUrl(rs.getString("AvatarUrl"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        
        String rolesStr = rs.getString("Roles");
        if (rolesStr != null && !rolesStr.isEmpty()) {
            user.setRoles(Arrays.asList(rolesStr.split(", ")));
        } else {
            user.setRoles(new ArrayList<>());
        }
        
        return user;
    }
}

