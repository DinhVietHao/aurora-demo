package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.FlashSale;
import com.group01.aurora_demo.admin.model.FlashSaleItemView;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlashSaleDAO {

    public List<FlashSale> findAll(String keyword, String status, int page, int pageSize, int[] outTotalRows) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        String where = " WHERE 1=1" +
                (keyword != null && !keyword.isEmpty() ? " AND Name LIKE ?" : "") +
                (status != null && !status.isEmpty() ? " AND Status = ?" : "");

        String countSql = "SELECT COUNT(*) FROM FlashSales" + where;
        String dataSql = "SELECT * FROM FlashSales" + where + " ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            int total = 0;
            try (PreparedStatement ps = cn.prepareStatement(countSql)) {
                int i = 1;
                if (keyword != null && !keyword.isEmpty()) ps.setString(i++, "%" + keyword + "%");
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }
            if (outTotalRows != null && outTotalRows.length > 0) outTotalRows[0] = total;

            List<FlashSale> list = new ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement(dataSql)) {
                int i = 1;
                if (keyword != null && !keyword.isEmpty()) ps.setString(i++, "%" + keyword + "%");
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ps.setInt(i++, (page - 1) * pageSize);
                ps.setInt(i, pageSize);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(new FlashSale(rs));
            }
            return list;
        }
    }

    public List<String> loadStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT StatusCode FROM FlashSaleStatus ORDER BY StatusCode")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) statuses.add(rs.getString(1));
        }
        return statuses;
    }

    public FlashSale findById(long id) throws SQLException {
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM FlashSales WHERE FlashSaleID=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new FlashSale(rs);
            return null;
        }
    }

    public List<FlashSaleItemView> findItems(long flashSaleId, String name, String publisher, String priceRange,
                                             int page, int pageSize, int[] outTotalRows) throws SQLException {
        return findItems(flashSaleId, name, publisher, priceRange, null, page, pageSize, outTotalRows);
    }

    public List<FlashSaleItemView> findItems(long flashSaleId, String name, String publisher, String priceRange, String sort,
                                             int page, int pageSize, int[] outTotalRows) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        String where = " WHERE fsi.FlashSaleID = ?" +
                (name != null && !name.isEmpty() ? " AND p.Title LIKE ?" : "") +
                (publisher != null && !publisher.isEmpty() ? " AND pub.Name LIKE ?" : "") +
                (priceRange != null && !priceRange.isEmpty() ? " AND fsi.FlashPrice BETWEEN ? AND ?" : "");

        String base = " FROM FlashSaleItems fsi JOIN Products p ON p.ProductID = fsi.ProductID " +
                " LEFT JOIN Publishers pub ON pub.PublisherID = p.PublisherID " + where;

        // Build ORDER BY clause
        String orderBy = " ORDER BY ";
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "price_asc":
                    orderBy += "fsi.FlashPrice ASC";
                    break;
                case "price_desc":
                    orderBy += "fsi.FlashPrice DESC";
                    break;
                case "name_asc":
                    orderBy += "p.Title ASC";
                    break;
                case "stock_desc":
                    orderBy += "fsi.FsStock DESC";
                    break;
                default:
                    orderBy += "p.ProductID DESC";
                    break;
            }
        } else {
            orderBy += "p.ProductID DESC";
        }

        String countSql = "SELECT COUNT(*)" + base;
        String dataSql = "SELECT fsi.FlashSaleItemID, p.ProductID, p.Title, pub.Name AS PublisherName, s.Name AS ShopName, fsi.FlashPrice, fsi.FsStock, p.OriginalPrice, p.SalePrice, fsi.ApprovalStatus "
                + base.replace("FROM FlashSaleItems fsi JOIN Products p ON p.ProductID = fsi.ProductID " +
                " LEFT JOIN Publishers pub ON pub.PublisherID = p.PublisherID ", 
                "FROM FlashSaleItems fsi JOIN Products p ON p.ProductID = fsi.ProductID " +
                " LEFT JOIN Publishers pub ON pub.PublisherID = p.PublisherID " +
                " LEFT JOIN Shops s ON s.ShopID = fsi.ShopID ") + orderBy + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            // count
            int total = 0; int i;
            try (PreparedStatement ps = cn.prepareStatement(countSql)) {
                i = 1; ps.setLong(i++, flashSaleId);
                if (name != null && !name.isEmpty()) ps.setString(i++, "%" + name + "%");
                if (publisher != null && !publisher.isEmpty()) ps.setString(i++, "%" + publisher + "%");
                if (priceRange != null && !priceRange.isEmpty()) {
                    String[] parts = priceRange.split("-");
                    ps.setBigDecimal(i++, new java.math.BigDecimal(parts[0]));
                    ps.setBigDecimal(i++, new java.math.BigDecimal(parts[1]));
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }
            if (outTotalRows != null && outTotalRows.length > 0) outTotalRows[0] = total;

            List<FlashSaleItemView> list = new ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement(dataSql)) {
                i = 1; ps.setLong(i++, flashSaleId);
                if (name != null && !name.isEmpty()) ps.setString(i++, "%" + name + "%");
                if (publisher != null && !publisher.isEmpty()) ps.setString(i++, "%" + publisher + "%");
                if (priceRange != null && !priceRange.isEmpty()) {
                    String[] parts = priceRange.split("-");
                    ps.setBigDecimal(i++, new java.math.BigDecimal(parts[0]));
                    ps.setBigDecimal(i++, new java.math.BigDecimal(parts[1]));
                }
                ps.setInt(i++, (page - 1) * pageSize);
                ps.setInt(i, pageSize);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(new FlashSaleItemView(rs));
            }
            return list;
        }
    }

    public long insert(String name, java.sql.Timestamp startAt, java.sql.Timestamp endAt, String status) throws SQLException {
        String sql = "INSERT INTO FlashSales (Name, StartAt, EndAt, Status, CreatedAt) VALUES (?,?,?,?, SYSUTCDATETIME()); SELECT SCOPE_IDENTITY();";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setTimestamp(2, startAt);
            ps.setTimestamp(3, endAt);
            ps.setString(4, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
            return 0L;
        }
    }

    public int update(long id, String name, java.sql.Timestamp startAt, java.sql.Timestamp endAt, String status) throws SQLException {
        String sql = "UPDATE FlashSales SET Name=?, StartAt=?, EndAt=?, Status=? WHERE FlashSaleID=?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setTimestamp(2, startAt);
            ps.setTimestamp(3, endAt);
            ps.setString(4, status);
            ps.setLong(5, id);
            return ps.executeUpdate();
        }
    }

    public int removeProductFromFlashSale(long flashSaleId, long productId) throws SQLException {
        String sql = "DELETE FROM FlashSaleItems WHERE FlashSaleID = ? AND ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, productId);
            return ps.executeUpdate();
        }
    }

    public boolean isProductInFlashSale(long flashSaleId, long productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM FlashSaleItems WHERE FlashSaleID = ? AND ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public List<FlashSaleItemView> findApprovalItems(String status, int page, int pageSize, int[] outTotalRows) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        String where = " WHERE 1=1" +
                (status != null && !status.isEmpty() ? " AND fsi.ApprovalStatus = ?" : "");

        // Use a subquery with ROW_NUMBER to get only one entry per product (the most recent one)
        String base = " FROM (SELECT ROW_NUMBER() OVER (PARTITION BY fsi.ProductID ORDER BY fsi.CreatedAt DESC) as RowNum, " +
                "fsi.FlashSaleItemID, fsi.ProductID, fsi.ShopID, fsi.FlashPrice, fsi.FsStock, fsi.ApprovalStatus, fsi.CreatedAt, " +
                "fsi.FlashSaleID FROM FlashSaleItems fsi) as fsi " +
                "JOIN Products p ON p.ProductID = fsi.ProductID " +
                "LEFT JOIN Publishers pub ON pub.PublisherID = p.PublisherID " +
                "LEFT JOIN Shops s ON s.ShopID = fsi.ShopID " +
                "LEFT JOIN FlashSales fs ON fs.FlashSaleID = fsi.FlashSaleID " + 
                where + " AND fsi.RowNum = 1";  // Only select the most recent entry for each product

        String countSql = "SELECT COUNT(*)" + base;
        String dataSql = "SELECT fsi.FlashSaleItemID, p.ProductID, p.Title, pub.Name AS PublisherName, s.Name AS ShopName, " +
                "fsi.FlashPrice, fsi.FsStock, p.OriginalPrice, p.SalePrice, fsi.ApprovalStatus, fs.Name AS FlashSaleName " +
                base + " ORDER BY fsi.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            // count
            int total = 0; int i;
            try (PreparedStatement ps = cn.prepareStatement(countSql)) {
                i = 1;
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }
            if (outTotalRows != null && outTotalRows.length > 0) outTotalRows[0] = total;

            List<FlashSaleItemView> list = new ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement(dataSql)) {
                i = 1;
                if (status != null && !status.isEmpty()) ps.setString(i++, status);
                ps.setInt(i++, (page - 1) * pageSize);
                ps.setInt(i, pageSize);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(new FlashSaleItemView(rs));
            }
            return list;
        }
    }

    public List<String> loadApprovalStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT StatusCode FROM FlashSaleItemApprovalStatus ORDER BY StatusCode")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) statuses.add(rs.getString(1));
        }
        return statuses;
    }

    public int updateApprovalStatus(long flashSaleItemId, String approvalStatus) throws SQLException {
        // When approving, first check if any other items exist for the same product
        if ("APPROVED".equals(approvalStatus)) {
            // Get the product ID for this flash sale item
            long productId = getProductIdForFlashSaleItem(flashSaleItemId);
            if (productId > 0) {
                // Mark any other items for this product as REJECTED to prevent duplicates
                String rejectOthersSql = "UPDATE FlashSaleItems SET ApprovalStatus = 'REJECTED' WHERE ProductID = ? AND FlashSaleItemID != ?";
                try (Connection cn = DataSourceProvider.get().getConnection();
                     PreparedStatement ps = cn.prepareStatement(rejectOthersSql)) {
                    ps.setLong(1, productId);
                    ps.setLong(2, flashSaleItemId);
                    ps.executeUpdate();
                }
            }
        }
        
        // Now update the requested item
        String sql = "UPDATE FlashSaleItems SET ApprovalStatus = ? WHERE FlashSaleItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, approvalStatus);
            ps.setLong(2, flashSaleItemId);
            return ps.executeUpdate();
        }
    }
    
    /**
     * Get the product ID for a given flash sale item
     */
    private long getProductIdForFlashSaleItem(long flashSaleItemId) throws SQLException {
        String sql = "SELECT ProductID FROM FlashSaleItems WHERE FlashSaleItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }
    
    /**
     * Check if a product associated with this flash sale item is already approved in any flash sale
     */
    public boolean checkIfProductAlreadyApproved(long flashSaleItemId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM FlashSaleItems fsi1
            JOIN FlashSaleItems fsi2 ON fsi1.ProductID = fsi2.ProductID
            WHERE fsi1.FlashSaleItemID = ?
            AND fsi2.FlashSaleItemID != ?
            AND fsi2.ApprovalStatus = 'APPROVED'
        """;
        
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleItemId);
            ps.setLong(2, flashSaleItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

public int upsertFlashSaleItem(long flashSaleId, long productId, long shopId,
                               double flashPrice, int fsStock, int perUserLimit) throws SQLException {
    // First check if this product is already in any flash sale with PENDING or APPROVED status
    String checkSql = """
        SELECT COUNT(*) FROM FlashSaleItems 
        WHERE ProductID = ? AND ApprovalStatus IN ('PENDING', 'APPROVED')
    """;
    
    try (Connection cn = DataSourceProvider.get().getConnection()) {
        // Check if product already exists in a flash sale
        try (PreparedStatement checkPs = cn.prepareStatement(checkSql)) {
            checkPs.setLong(1, productId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Product already exists in a flash sale
                // Delete existing entries for this product (to avoid duplicates)
                String deleteSql = "DELETE FROM FlashSaleItems WHERE ProductID = ?";
                try (PreparedStatement deletePs = cn.prepareStatement(deleteSql)) {
                    deletePs.setLong(1, productId);
                    deletePs.executeUpdate();
                }
            }
        }
        
        // Now insert the new item
        String sql = """
            INSERT INTO FlashSaleItems 
            (FlashSaleID, ProductID, ShopID, FlashPrice, FsStock, PerUserLimit, ApprovalStatus, CreatedAt)
            VALUES (?, ?, ?, ?, ?, ?, 'PENDING', SYSDATETIME());
        """;
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleId);
            ps.setLong(2, productId);
            ps.setLong(3, shopId);
            ps.setDouble(4, flashPrice);
            ps.setInt(5, fsStock);
            ps.setInt(6, perUserLimit);
            return ps.executeUpdate();
        }
    }
}

    /**
     * Kiểm tra xem sản phẩm có thể bị xoá khỏi Flash Sale hay không
     * - Chỉ cho phép xoá khi đợt Flash Sale chưa bắt đầu (status != 'ACTIVE')
     * - Và sản phẩm chưa có đơn hàng nào
     */
    public boolean canDelete(long flashSaleItemId) throws SQLException {
        String sql = """
            SELECT fs.Status, COUNT(oi.OrderItemID) AS UsedCount
            FROM FlashSaleItems fsi
            JOIN FlashSales fs ON fs.FlashSaleID = fsi.FlashSaleID
            LEFT JOIN OrderItems oi ON oi.FlashSaleItemID = fsi.FlashSaleItemID
            WHERE fsi.FlashSaleItemID = ?
            GROUP BY fs.Status
        """;
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("Status");
                int usedCount = rs.getInt("UsedCount");
                return !status.equalsIgnoreCase("ACTIVE") && usedCount == 0;
            }
        }
        return false;
    }
    
    /**
     * Xoá sản phẩm khỏi Flash Sale (xoá vật lý)
     */
    public int deleteFlashSaleItem(long flashSaleItemId) throws SQLException {
        String sql = "DELETE FROM FlashSaleItems WHERE FlashSaleItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleItemId);
            return ps.executeUpdate();
        }
    }
    
    /**
     * Xoá mềm sản phẩm khỏi Flash Sale (sử dụng IsDeleted)
     * Lưu ý: Cần đảm bảo bảng FlashSaleItems đã có cột IsDeleted
     * ALTER TABLE FlashSaleItems ADD IsDeleted BIT NOT NULL DEFAULT(0)
     */
    public int softDeleteFlashSaleItem(long flashSaleItemId) throws SQLException {
        String sql = "UPDATE FlashSaleItems SET IsDeleted = 1 WHERE FlashSaleItemID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, flashSaleItemId);
            return ps.executeUpdate();
        }
    }
}
