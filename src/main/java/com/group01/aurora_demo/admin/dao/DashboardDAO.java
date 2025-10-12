package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for dashboard statistics and analytics
 */
public class DashboardDAO {

    /**
     * Get the count of low stock products (less than or equal to the threshold)
     */
    public int getLowStockProductCount(int threshold) {
        String sql = "SELECT COUNT(*) FROM Products WHERE Stock <= ?";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, threshold);
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
     * Get the count of recent user registrations within the specified number of days
     */
    public int getRecentUserRegistrations(int days) {
        String sql = "SELECT COUNT(*) FROM Users WHERE CreatedAt >= DATEADD(DAY, -?, GETDATE())";
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
    
    /**
     * Get the average rating across all products
     */
    public double getAverageProductRating() {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) AS AvgRating FROM Reviews";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("AvgRating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Get the count of pending actions (e.g., orders to process, reviews to moderate)
     */
    public Map<String, Integer> getPendingActionsCount() {
        Map<String, Integer> pendingActions = new LinkedHashMap<>();
        
        // Pending orders count
        String orderSql = "SELECT COUNT(*) FROM Orders WHERE OrderStatus = 'pending'";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(orderSql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                pendingActions.put("orders", rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pendingActions.put("orders", 0);
        }
        
        // Pending reviews count
        String reviewSql = "SELECT COUNT(*) FROM Reviews WHERE Status = 'pending'";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(reviewSql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                pendingActions.put("reviews", rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pendingActions.put("reviews", 0);
        }
        
        return pendingActions;
    }
    
    /**
     * Get sales data by category
     */
    public List<Map<String, Object>> getSalesByCategory() {
        List<Map<String, Object>> categoryData = new ArrayList<>();
        
        String sql = "SELECT c.Name AS CategoryName, COUNT(oi.OrderItemID) AS SalesCount, " +
                     "SUM(oi.Subtotal) AS Revenue " +
                     "FROM OrderItems oi " +
                     "JOIN Products p ON oi.ProductID = p.ProductID " +
                     "JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     "JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID " +
                     "JOIN Orders o ON os.OrderID = o.OrderID " +
                     "WHERE o.PaymentStatus = 'paid' " +
                     "GROUP BY c.Name " +
                     "ORDER BY Revenue DESC";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> category = new LinkedHashMap<>();
                category.put("name", rs.getString("CategoryName"));
                category.put("count", rs.getInt("SalesCount"));
                category.put("revenue", rs.getBigDecimal("Revenue"));
                categoryData.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categoryData;
    }
    
    /**
     * Get recent activities for the dashboard feed
     */
    public List<Map<String, Object>> getRecentActivities(int limit) {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Recent orders
        String orderSql = "SELECT TOP(?) 'order' AS Type, o.OrderID AS ID, " +
                         "u.FullName AS Name, o.FinalAmount AS Value, " +
                         "o.OrderStatus AS Status, o.CreatedAt " +
                         "FROM Orders o " +
                         "JOIN Users u ON o.UserID = u.UserID " +
                         "ORDER BY o.CreatedAt DESC";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(orderSql)) {
            
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("type", "order");
                    activity.put("id", rs.getLong("ID"));
                    activity.put("name", rs.getString("Name"));
                    activity.put("value", rs.getBigDecimal("Value"));
                    activity.put("status", rs.getString("Status"));
                    activity.put("createdAt", rs.getTimestamp("CreatedAt"));
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Recent reviews
        String reviewSql = "SELECT TOP(?) 'review' AS Type, r.ReviewID AS ID, " +
                           "u.FullName AS Name, p.Title AS ProductName, " +
                           "r.Rating AS Value, r.Status, r.CreatedAt " +
                           "FROM Reviews r " +
                           "JOIN Users u ON r.UserID = u.UserID " +
                           "JOIN Products p ON r.ProductID = p.ProductID " +
                           "ORDER BY r.CreatedAt DESC";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(reviewSql)) {
            
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> activity = new LinkedHashMap<>();
                    activity.put("type", "review");
                    activity.put("id", rs.getLong("ID"));
                    activity.put("name", rs.getString("Name"));
                    activity.put("productName", rs.getString("ProductName"));
                    activity.put("value", rs.getInt("Value"));
                    activity.put("status", rs.getString("Status"));
                    activity.put("createdAt", rs.getTimestamp("CreatedAt"));
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Sort all activities by creation date
        activities.sort((a, b) -> {
            Timestamp aTime = (Timestamp) a.get("createdAt");
            Timestamp bTime = (Timestamp) b.get("createdAt");
            return bTime.compareTo(aTime); // Descending order
        });
        
        // Return only the requested number of activities
        return activities.size() > limit ? activities.subList(0, limit) : activities;
    }
}
