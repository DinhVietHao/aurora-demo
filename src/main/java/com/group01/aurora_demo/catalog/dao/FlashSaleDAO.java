package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.catalog.model.FlashSale;
import com.group01.aurora_demo.catalog.model.FlashSaleItem;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class FlashSaleDAO {
    public boolean isProductInCurrentFlashSale(long productId, LocalDateTime now) throws SQLException {
        String sql = """
                    SELECT COUNT(*)
                    FROM FlashSaleItems fsi
                    JOIN FlashSales fs ON fsi.FlashSaleID = fs.FlashSaleID
                    WHERE fsi.ProductID = ?
                      AND fs.Status = 'ACTIVE'
                      AND fs.StartAt <= ? AND fs.EndAt >= ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setTimestamp(3, Timestamp.valueOf(now));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<FlashSale> getAllFlashSales() {
        List<FlashSale> list = new ArrayList<>();
        String sql = "SELECT FlashSaleID, Name, StartAt, EndAt, [Status] FROM FlashSales ORDER BY StartAt DESC";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FlashSale f = new FlashSale();
                f.setFlashSaleID(rs.getLong("FlashSaleID"));
                f.setName(rs.getString("Name"));
                f.setStartAt(rs.getTimestamp("StartAt"));
                f.setEndAt(rs.getTimestamp("EndAt"));
                f.setStatus(rs.getString("Status"));
                list.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertFlashSaleItem(long flashSaleId, long shopId, long productId,
            double flashPrice, int fsStock,
            Integer perUserLimit, String approvalStatus) {
        String sql = "INSERT INTO FlashSaleItems (FlashSaleID, ProductID, ShopID, FlashPrice, FsStock, PerUserLimit, ApprovalStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, productId);
            ps.setLong(3, shopId);
            ps.setDouble(4, flashPrice);
            ps.setInt(5, fsStock);
            if (perUserLimit != null)
                ps.setInt(6, perUserLimit);
            else
                ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, approvalStatus);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isProductInThisFlashSale(long flashSaleId, long productId) {
        String sql = "SELECT COUNT(*) FROM FlashSaleItems WHERE FlashSaleID = ? AND ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<FlashSaleItem> getFlashSaleItemsByFlashSaleIdAndShopId(long flashSaleId, long shopId)
            throws SQLException {
        List<FlashSaleItem> list = new ArrayList<>();

        String sql = """
                    SELECT fsi.FlashSaleItemID, fsi.ProductID, p.Title, p.OriginalPrice,
                           fsi.FlashPrice, fsi.FsStock, fsi.PerUserLimit, fsi.ApprovalStatus,
                           img.Url AS ImageUrl
                    FROM FlashSaleItems fsi
                    JOIN Products p ON fsi.ProductID = p.ProductID
                    OUTER APPLY (
                        SELECT TOP 1 Url FROM ProductImages
                        WHERE ProductID = p.ProductID AND IsPrimary = 1
                    ) img
                    WHERE fsi.FlashSaleID = ? AND fsi.ShopID = ?
                    ORDER BY fsi.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FlashSaleItem item = new FlashSaleItem();
                    item.setFlashSaleItemID(rs.getLong("FlashSaleItemID"));
                    item.setProductID(rs.getLong("ProductID"));
                    item.setTitle(rs.getString("Title"));
                    item.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    item.setFlashPrice(rs.getDouble("FlashPrice"));
                    item.setFsStock(rs.getInt("FsStock"));
                    item.setPerUserLimit(rs.getInt("PerUserLimit"));
                    item.setApprovalStatus(rs.getString("ApprovalStatus"));
                    item.setImageUrl(rs.getString("ImageUrl"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public FlashSaleItem getFlashSaleItemDetail(long itemId) throws SQLException {
        String sql = """
                    SELECT
                    fsi.FlashSaleItemID,
                    fsi.FlashSaleID,
                    fs.StartAt,
                    fs.EndAt,
                    p.ProductID,
                    p.Title,
                    p.OriginalPrice,
                    fsi.FlashPrice,
                    fsi.FsStock,
                    fsi.SoldCount,
                    fsi.PerUserLimit,
                    fsi.ApprovalStatus,
                    pi.Url AS ImageUrl
                FROM FlashSaleItems fsi
                JOIN FlashSales fs ON fsi.FlashSaleID = fs.FlashSaleID
                JOIN Products p ON fsi.ProductID = p.ProductID
                LEFT JOIN ProductImages pi
                       ON p.ProductID = pi.ProductID
                      AND pi.IsPrimary = 1
                WHERE fsi.FlashSaleItemID = ?

                                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FlashSaleItem item = new FlashSaleItem();
                    item.setFlashSaleItemID(rs.getLong("FlashSaleItemID"));
                    item.setFlashSaleId(rs.getLong("FlashSaleID"));
                    item.setStartAt(rs.getTimestamp("StartAt"));
                    item.setEndAt(rs.getTimestamp("EndAt"));
                    item.setProductID(rs.getLong("ProductID"));
                    item.setTitle(rs.getString("Title"));
                    item.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    item.setFlashPrice(rs.getDouble("FlashPrice"));
                    item.setFsStock(rs.getInt("FsStock"));
                    item.setSoldCount(rs.getInt("SoldCount"));
                    item.setPerUserLimit(rs.getInt("PerUserLimit"));
                    item.setApprovalStatus(rs.getString("ApprovalStatus"));
                    item.setImageUrl(rs.getString("ImageUrl"));
                    return item;
                }
            }
        }
        return null;
    }

    public Map<LocalDate, Double> getRevenueByFlashSaleItem(long flashSaleItemId, Timestamp start,
            Timestamp end) throws SQLException {
        Map<LocalDate, Double> revenueMap = new LinkedHashMap<>();

        String sql = """
                    SELECT
                        CAST(os.CreatedAt AS DATE) AS OrderDate,
                        SUM(oi.Quantity * oi.SalePrice) AS TotalRevenue
                    FROM OrderItems oi
                    JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID
                    WHERE oi.FlashSaleItemID = ?
                      AND os.CreatedAt BETWEEN ? AND ?
                      AND os.Status IN ('COMPLETED')
                    GROUP BY CAST(os.CreatedAt AS DATE)
                    ORDER BY OrderDate
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, flashSaleItemId);
            ps.setTimestamp(2, start);
            ps.setTimestamp(3, end);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("OrderDate").toLocalDate();
                    double revenue = rs.getDouble("TotalRevenue");
                    revenueMap.put(date, revenue);
                }
            }
        }

        return revenueMap;
    }

}
