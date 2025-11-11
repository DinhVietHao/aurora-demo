package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.PlatformRevenueStats;
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
        String sql = "SELECT COUNT(*) FROM Products WHERE Quantity <= ?";
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

        // Recent orders (using OrderShops instead of Orders)
        String orderSql = "SELECT TOP(?) 'order' AS Type, os.OrderShopID AS ID, " +
                         "u.FullName AS Name, os.FinalAmount AS Value, " +
                         "os.Status AS Status, os.CreatedAt " +
                         "FROM OrderShops os " +
                         "JOIN Users u ON os.UserID = u.UserID " +
                         "ORDER BY os.CreatedAt DESC";

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
                           "r.Rating AS Value, r.CreatedAt " +
                           "FROM Reviews r " +
                           "JOIN Users u ON r.UserID = u.UserID " +
                           "LEFT JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID " +
                           "LEFT JOIN Products p ON oi.ProductID = p.ProductID " +
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

    /**
     * Get platform revenue statistics including monthly shop fees and tax revenue
     *
     * @return PlatformRevenueStats object containing revenue breakdown
     */
    public PlatformRevenueStats getPlatformRevenueStats() {
        PlatformRevenueStats stats = new PlatformRevenueStats();

        try (Connection conn = DataSourceProvider.get().getConnection()) {
            // Step 1: Get active shop count
            String shopCountSql = "SELECT COUNT(*) FROM Shops WHERE Status = 'ACTIVE'";
            try (PreparedStatement ps = conn.prepareStatement(shopCountSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setActiveShopCount(rs.getInt(1));
                }
            }

            // Step 2: Get platform fee setting
            String feeSql = "SELECT setting_value FROM setting WHERE setting_key = 'platform_fee_monthly'";
            try (PreparedStatement ps = conn.prepareStatement(feeSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String feeValue = rs.getString("setting_value");
                    if (feeValue != null && !feeValue.trim().isEmpty()) {
                        try {
                            BigDecimal monthlyFee = new BigDecimal(feeValue);
                            stats.setMonthlyFeePerShop(monthlyFee);

                            // Calculate total monthly fees
                            BigDecimal totalMonthlyFees = monthlyFee.multiply(
                                new BigDecimal(stats.getActiveShopCount())
                            );
                            stats.setTotalMonthlyFees(totalMonthlyFees);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid platform_fee_monthly value: " + feeValue);
                            stats.setMonthlyFeePerShop(BigDecimal.ZERO);
                            stats.setTotalMonthlyFees(BigDecimal.ZERO);
                        }
                    }
                }
            }

            // Step 3: Calculate tax revenue from completed orders
            // VATRate is already stored in OrderItems table at purchase time
            String taxSql = "SELECT ISNULL(SUM(oi.SalePrice * oi.Quantity * oi.VATRate / 100), 0) AS TotalTaxRevenue " +
                           "FROM OrderItems oi " +
                           "INNER JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID " +
                           "WHERE os.Status = 'COMPLETED'";
            try (PreparedStatement ps = conn.prepareStatement(taxSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal taxRevenue = rs.getBigDecimal("TotalTaxRevenue");
                    stats.setTotalTaxRevenue(taxRevenue != null ? taxRevenue : BigDecimal.ZERO);
                }
            }

            // Step 4: Calculate grand total
            BigDecimal grandTotal = stats.getTotalMonthlyFees().add(stats.getTotalTaxRevenue());
            stats.setGrandTotalRevenue(grandTotal);

        } catch (SQLException e) {
            System.err.println("Error calculating platform revenue statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Get tax details by shop for admin dashboard
     * Returns list of shops with their tax information
     */
    public List<Map<String, Object>> getShopTaxDetails() {
        List<Map<String, Object>> shopTaxList = new ArrayList<>();

        String sql = """
                SELECT
                    s.ShopID,
                    s.Name AS ShopName,
                    COUNT(DISTINCT os.OrderShopID) AS TotalOrders,
                    SUM(os.Subtotal) AS TotalSubtotal,
                    SUM(os.FinalAmount) AS TotalRevenue,
                    SUM(os.FinalAmount * 0.05) AS TotalTax,
                    SUM(os.FinalAmount * 0.95) AS ShopEarnings
                FROM Shops s
                LEFT JOIN OrderShops os ON s.ShopID = os.ShopID
                    AND os.Status IN ('DELIVERED', 'COMPLETED')
                    AND os.CreatedAt >= DATEADD(MONTH, -1, GETDATE())
                WHERE s.Status = 'ACTIVE'
                GROUP BY s.ShopID, s.Name
                HAVING COUNT(DISTINCT os.OrderShopID) > 0
                ORDER BY TotalRevenue DESC
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> shopTax = new LinkedHashMap<>();
                shopTax.put("shopId", rs.getLong("ShopID"));
                shopTax.put("shopName", rs.getString("ShopName"));
                shopTax.put("totalOrders", rs.getInt("TotalOrders"));
                shopTax.put("totalSubtotal", rs.getBigDecimal("TotalSubtotal"));
                shopTax.put("totalRevenue", rs.getBigDecimal("TotalRevenue"));
                shopTax.put("totalTax", rs.getBigDecimal("TotalTax"));
                shopTax.put("shopEarnings", rs.getBigDecimal("ShopEarnings"));
                shopTaxList.add(shopTax);
            }

        } catch (SQLException e) {
            System.err.println("Error getting shop tax details: " + e.getMessage());
            e.printStackTrace();
        }

        return shopTaxList;
    }
}
