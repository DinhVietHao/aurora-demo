package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.Shop;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO {

    /**
     * Find all shops with optional filtering and pagination
     */
    public List<Shop> findAll(String keyword, String status, int page, int pageSize, int[] outTotalRows) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        String where = " WHERE 1=1" +
                (keyword != null && !keyword.isEmpty() ? " AND (s.Name LIKE ? OR s.InvoiceEmail LIKE ?)" : "") +
                (status != null && !status.isEmpty() ? " AND s.Status = ?" : "");

        String countSql = "SELECT COUNT(*) FROM Shops s" + where;
        String dataSql = "SELECT s.*, u.FullName AS OwnerName, u.Email AS OwnerEmail, " +
                "(SELECT COUNT(*) FROM Products WHERE ShopID = s.ShopID) AS ProductCount " +
                "FROM Shops s " +
                "LEFT JOIN Users u ON s.OwnerUserID = u.UserID " +
                where + " ORDER BY s.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            // Count total rows
            int total = 0;
            try (PreparedStatement ps = cn.prepareStatement(countSql)) {
                int i = 1;
                if (keyword != null && !keyword.isEmpty()) {
                    String pattern = "%" + keyword + "%";
                    ps.setString(i++, pattern);
                    ps.setString(i++, pattern);
                }
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }
            if (outTotalRows != null && outTotalRows.length > 0) outTotalRows[0] = total;

            // Fetch data
            List<Shop> list = new ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement(dataSql)) {
                int i = 1;
                if (keyword != null && !keyword.isEmpty()) {
                    String pattern = "%" + keyword + "%";
                    ps.setString(i++, pattern);
                    ps.setString(i++, pattern);
                }
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ps.setInt(i++, (page - 1) * pageSize);
                ps.setInt(i, pageSize);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(new Shop(rs));
            }
            return list;
        }
    }

    /**
     * Find a shop by ID with detailed information
     */
    public Shop findById(long id) throws SQLException {
        String sql = "SELECT s.*, u.FullName AS OwnerName, u.Email AS OwnerEmail, " +
                "(SELECT COUNT(*) FROM Products WHERE ShopID = s.ShopID) AS ProductCount " +
                "FROM Shops s " +
                "LEFT JOIN Users u ON s.OwnerUserID = u.UserID " +
                "WHERE s.ShopID = ?";
        
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Shop(rs);
            return null;
        }
    }

    /**
     * Get pickup address for a shop
     */
    public PickupAddress getPickupAddress(long shopId) throws SQLException {
        String sql = "SELECT a.* FROM Shops s " +
                "JOIN Addresses a ON s.PickupAddressID = a.AddressID " +
                "WHERE s.ShopID = ?";
        
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new PickupAddress(rs);
            return null;
        }
    }

    /**
     * Load all shop statuses from ShopStatus table
     */
    public List<String> loadStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT StatusCode FROM ShopStatus ORDER BY StatusCode")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) statuses.add(rs.getString(1));
        }
        return statuses;
    }

    /**
     * Update shop information
     */
    public int update(long id, String name, String description, String status, String invoiceEmail) throws SQLException {
        String sql = "UPDATE Shops SET Name=?, Description=?, Status=?, InvoiceEmail=? WHERE ShopID=?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, status);
            ps.setString(4, invoiceEmail);
            ps.setLong(5, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Update shop avatar
     */
    public int updateAvatar(long id, String avatarUrl) throws SQLException {
        String sql = "UPDATE Shops SET AvatarUrl=? WHERE ShopID=?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, avatarUrl);
            ps.setLong(2, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Inner class for pickup address
     */
    public static class PickupAddress {
        public long addressId;
        public String recipientName;
        public String phone;
        public String line;
        public String city;
        public String district;
        public String ward;
        public String postalCode;
        public Timestamp createdAt;

        public PickupAddress(ResultSet rs) throws SQLException {
            this.addressId = rs.getLong("AddressID");
            this.recipientName = rs.getString("RecipientName");
            this.phone = rs.getString("Phone");
            this.line = rs.getString("Line");
            this.city = rs.getString("City");
            this.district = rs.getString("District");
            this.ward = rs.getString("Ward");
            this.postalCode = rs.getString("PostalCode");
            this.createdAt = rs.getTimestamp("CreatedAt");
        }

        // Getters
        public long getAddressId() { return addressId; }
        public String getRecipientName() { return recipientName; }
        public String getPhone() { return phone; }
        public String getLine() { return line; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getWard() { return ward; }
        public String getPostalCode() { return postalCode; }
        public Timestamp getCreatedAt() { return createdAt; }
    }
}

