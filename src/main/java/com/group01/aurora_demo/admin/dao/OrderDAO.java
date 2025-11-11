package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    /**
     * Get the total count of orders (OrderShops)
     */
    public int getOrderCount() {
        String sql = "SELECT COUNT(*) FROM OrderShops";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the count of orders with a specific status
     */
    public int getOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM OrderShops WHERE Status = ?";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get the total revenue from OrderShops
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(FinalAmount) FROM OrderShops WHERE Status IN ('DELIVERED', 'COMPLETED')";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get the total revenue for the last n days from OrderShops
     */
    public BigDecimal getRevenueLastDays(int days) {
        String sql = "SELECT SUM(FinalAmount) FROM OrderShops WHERE Status IN ('DELIVERED', 'COMPLETED') AND CreatedAt >= DATEADD(DAY, -?, GETDATE())";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(1);
                    return result != null ? result : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get daily revenue for last 7 days from OrderShops
     */
    public Map<String, BigDecimal> getDailyRevenueLast7Days() {
        Map<String, BigDecimal> dailyRevenue = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Create SQL query for the last 7 days
        String sql = "SELECT CAST(CreatedAt AS DATE) AS OrderDate, SUM(FinalAmount) AS DailyRevenue " +
                     "FROM OrderShops " +
                     "WHERE Status IN ('DELIVERED', 'COMPLETED') AND CreatedAt >= DATEADD(DAY, -7, GETDATE()) " +
                     "GROUP BY CAST(CreatedAt AS DATE) " +
                     "ORDER BY OrderDate DESC";

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date date = rs.getDate("OrderDate");
                BigDecimal revenue = rs.getBigDecimal("DailyRevenue");
                String dateStr = date.toLocalDate().format(formatter);
                dailyRevenue.put(dateStr, revenue);
            }

            // Fill in missing dates with zero
            LocalDateTime today = LocalDateTime.now();
            for (int i = 0; i < 7; i++) {
                LocalDateTime day = today.minusDays(i);
                String dateStr = day.format(formatter);
                dailyRevenue.putIfAbsent(dateStr, BigDecimal.ZERO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dailyRevenue;
    }
    
    /**
     * Get most recent orders from OrderShops
     */
    public List<Map<String, Object>> getRecentOrders(int limit) {
        List<Map<String, Object>> orders = new ArrayList<>();

        String sql = "SELECT TOP(?) os.OrderShopID, os.CreatedAt, os.FinalAmount, os.Status, u.FullName AS CustomerName " +
                     "FROM OrderShops os " +
                     "JOIN Users u ON os.UserID = u.UserID " +
                     "ORDER BY os.CreatedAt DESC";

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> order = new LinkedHashMap<>();
                    order.put("orderId", rs.getLong("OrderShopID"));
                    order.put("createdAt", rs.getTimestamp("CreatedAt").toLocalDateTime());
                    order.put("amount", rs.getBigDecimal("FinalAmount"));
                    order.put("status", rs.getString("Status"));
                    order.put("customerName", rs.getString("CustomerName"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Get the count of orders for the last n days from OrderShops
     */
    public int getOrderCountLastDays(int days) {
        String sql = "SELECT COUNT(*) FROM OrderShops WHERE CreatedAt >= DATEADD(DAY, -?, GETDATE())";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
