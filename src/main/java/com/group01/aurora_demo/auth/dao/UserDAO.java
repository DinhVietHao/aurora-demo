package com.group01.aurora_demo.auth.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class UserDAO {

    public User findById(long userId) {
        String sql = "SELECT UserID, Email, FullName, PasswordHash, AuthProvider, CreatedAt, Status "
                + "FROM dbo.Users WHERE UserID = ?";
        try (Connection con = DataSourceProvider.get().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getLong("UserID"));
                    u.setEmail(rs.getString("Email"));
                    u.setFullName(rs.getString("FullName"));
                    u.setPassword(rs.getString("Password"));
                    u.setAuthProvider(rs.getString("AuthProvider"));
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    u.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    u.setStatus(rs.getString("Status"));
                    
                    try {
                        u.setPhone(rs.getString("Phone"));
                    } catch (SQLException e) {
                        // Phone might be null
                    }
                    
                    try {
                        u.setPoints(rs.getInt("Points"));
                    } catch (SQLException e) {
                        // Points might be null
                    }
                    
                    try {
                        u.setNationalID(rs.getString("NationalID"));
                    } catch (SQLException e) {
                        // NationalID might be null
                    }
                    
                    try {
                        u.setAvatarUrl(rs.getString("AvatarUrl"));
                    } catch (SQLException e) {
                        // AvatarUrl might be null
                    }
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
                u.setUserID(rs.getLong("UserID"));
                u.setEmail(rs.getString("Email"));
                u.setFullName(rs.getString("FullName"));
                u.setPassword(rs.getString("Password"));
                Timestamp ts = rs.getTimestamp("CreatedAt");
                u.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                u.setAuthProvider(rs.getString("AuthProvider"));
                
                try {
                    u.setStatus(rs.getString("Status"));
                } catch (SQLException e) {
                    // Status might be null
                    u.setStatus("active");
                }
                
                try {
                    u.setPhone(rs.getString("Phone"));
                } catch (SQLException e) {
                    // Phone might be null
                }
                
                try {
                    u.setPoints(rs.getInt("Points"));
                } catch (SQLException e) {
                    // Points might be null
                }
                
                try {
                    u.setNationalID(rs.getString("NationalID"));
                } catch (SQLException e) {
                    // NationalID might be null
                }
                
                try {
                    u.setAvatarUrl(rs.getString("AvatarUrl"));
                } catch (SQLException e) {
                    // AvatarUrl might be null
                }
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
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getAuthProvider());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("createAccount failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all users with pagination
     */
    public List<User> getAllUsers(int page, int pageSize) {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT u.UserID, u.Email, u.FullName, u.Phone, u.Points, u.NationalID, "
                + "u.AuthProvider, u.CreatedAt, ISNULL(u.Status, 'active') as Status, u.AvatarUrl, "
                + "STUFF((SELECT ', ' + r.RoleName "
                + "FROM dbo.UserRoles ur "
                + "JOIN dbo.Roles r ON ur.RoleCode = r.RoleCode "
                + "WHERE ur.UserID = u.UserID "
                + "FOR XML PATH('')), 1, 2, '') as Roles "
                + "FROM dbo.Users u "
                + "ORDER BY u.UserID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
                
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getLong("UserID"));
                    user.setEmail(rs.getString("Email"));
                    user.setFullName(rs.getString("FullName"));
                    user.setPhone(rs.getString("Phone"));
                    user.setPoints(rs.getInt("Points"));
                    user.setNationalID(rs.getString("NationalID"));
                    user.setAuthProvider(rs.getString("AuthProvider"));
                    user.setStatus(rs.getString("Status"));
                    user.setRoles(rs.getString("Roles"));
                    
                    try {
                        user.setAvatarUrl(rs.getString("AvatarUrl"));
                    } catch (SQLException e) {
                        // AvatarUrl might be null
                    }
                    
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    user.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Count total number of users
     */
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM dbo.Users";
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
     * Search users with filters and pagination
     */
    public List<User> searchUsers(String keyword, String status, String role, int page, int pageSize) {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT u.UserID, u.Email, u.FullName, u.Phone, u.Points, u.NationalID, ")
                .append("u.AuthProvider, u.CreatedAt, ISNULL(u.Status, 'active') as Status, u.AvatarUrl, ")
                .append("STUFF((SELECT ', ' + r.RoleName ")
                .append("FROM dbo.UserRoles ur ")
                .append("JOIN dbo.Roles r ON ur.RoleCode = r.RoleCode ")
                .append("WHERE ur.UserID = u.UserID ")
                .append("FOR XML PATH('')), 1, 2, '') as Roles ")
                .append("FROM dbo.Users u ");
                
        // Join with roles table if role filter is specified
        if (!role.isEmpty()) {
            sqlBuilder.append("JOIN dbo.UserRoles ur ON u.UserID = ur.UserID ")
                     .append("JOIN dbo.Roles r ON ur.RoleCode = r.RoleCode ");
        }
        
        // Add WHERE clause for filtering
        boolean hasWhere = false;
        
        // Keyword search (name or email)
        if (!keyword.isEmpty()) {
            sqlBuilder.append("WHERE (u.FullName LIKE ? OR u.Email LIKE ?) ");
            hasWhere = true;
        }
        
        // Status filter
        if (!status.isEmpty()) {
            sqlBuilder.append(hasWhere ? "AND " : "WHERE ");
            sqlBuilder.append("ISNULL(u.Status, 'active') = ? ");
            hasWhere = true;
        }
        
        // Role filter
        if (!role.isEmpty()) {
            sqlBuilder.append(hasWhere ? "AND " : "WHERE ");
            sqlBuilder.append("r.RoleCode = ? ");
        }
        
        // Add order by and pagination
        sqlBuilder.append("ORDER BY u.UserID ")
                 .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
                
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set keyword parameters
            if (!keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            
            // Set status parameter
            if (!status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            
            // Set role parameter
            if (!role.isEmpty()) {
                ps.setString(paramIndex++, role);
            }
            
            // Set pagination parameters
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getLong("UserID"));
                    user.setEmail(rs.getString("Email"));
                    user.setFullName(rs.getString("FullName"));
                    user.setPhone(rs.getString("Phone"));
                    user.setPoints(rs.getInt("Points"));
                    user.setNationalID(rs.getString("NationalID"));
                    user.setAuthProvider(rs.getString("AuthProvider"));
                    user.setStatus(rs.getString("Status"));
                    user.setRoles(rs.getString("Roles"));
                    
                    try {
                        user.setAvatarUrl(rs.getString("AvatarUrl"));
                    } catch (SQLException e) {
                        // AvatarUrl might be null
                    }
                    
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    user.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Count users that match search criteria
     */
    public int countSearchResults(String keyword, String status, String role) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) FROM dbo.Users u ");
        
        // Join with roles table if role filter is specified
        if (!role.isEmpty()) {
            sqlBuilder.append("JOIN dbo.UserRoles ur ON u.UserID = ur.UserID ")
                     .append("JOIN dbo.Roles r ON ur.RoleCode = r.RoleCode ");
        }
        
        // Add WHERE clause for filtering
        boolean hasWhere = false;
        
        // Keyword search (name or email)
        if (!keyword.isEmpty()) {
            sqlBuilder.append("WHERE (u.FullName LIKE ? OR u.Email LIKE ?) ");
            hasWhere = true;
        }
        
        // Status filter
        if (!status.isEmpty()) {
            sqlBuilder.append(hasWhere ? "AND " : "WHERE ");
            sqlBuilder.append("ISNULL(u.Status, 'active') = ? ");
            hasWhere = true;
        }
        
        // Role filter
        if (!role.isEmpty()) {
            sqlBuilder.append(hasWhere ? "AND " : "WHERE ");
            sqlBuilder.append("r.RoleCode = ? ");
        }
                
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set keyword parameters
            if (!keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            
            // Set status parameter
            if (!status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            
            // Set role parameter
            if (!role.isEmpty()) {
                ps.setString(paramIndex++, role);
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
     * Toggle user account status between active and locked
     */
    public boolean toggleUserStatus(long userId) {
        String checkSql = "SELECT ISNULL(Status, 'active') AS Status FROM dbo.Users WHERE UserID = ?";
        String updateSql = "UPDATE dbo.Users SET Status = ? WHERE UserID = ?";
        
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            // First check current status
            String currentStatus;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false; // User not found
                    }
                    currentStatus = rs.getString("Status").trim();
                    System.out.println("Current status: '" + currentStatus + "'");
                }
            }
            
            // Toggle status
            String newStatus = "active".equals(currentStatus.trim()) ? "locked" : "active";
            
            // Update status
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newStatus);
                ps.setLong(2, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error toggling user status: " + e.getMessage());
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