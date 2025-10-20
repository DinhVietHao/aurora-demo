package com.group01.aurora_demo.shop.dao;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import com.group01.aurora_demo.shop.model.Voucher;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class VoucherDAO {
    public List<Voucher> getActiveVouchersByShopId(long shopId) {
        List<Voucher> listVouchersShop = new ArrayList<>();
        String sql = """
                      SELECT
                          VoucherID,
                          Code,
                          DiscountType,
                          Value,
                          MaxAmount,
                          MinOrderAmount,
                          StartAt,
                          EndAt,
                          UsageLimit,
                          PerUserLimit,
                          Status,
                          UsageCount,
                          IsShopVoucher,
                          ShopID
                          FROM Vouchers
                          WHERE IsShopVoucher = 1
                            AND ShopID = ?
                            AND StartAt <= SYSUTCDATETIME()
                            AND EndAt   >= SYSUTCDATETIME()
                            AND  (UsageLimit IS NULL OR UsageCount < UsageLimit)
                            AND Status = 'ACTIVE'
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setVoucherID(rs.getLong("VoucherID"));
                voucher.setCode(rs.getString("Code"));
                voucher.setDiscountType(rs.getString("DiscountType"));
                voucher.setValue(rs.getDouble("Value"));
                voucher.setMaxAmount(rs.getDouble("MaxAmount"));
                voucher.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
                voucher.setStartAt(rs.getTimestamp("StartAt"));
                voucher.setEndAt(rs.getTimestamp("EndAt"));
                voucher.setUsageLimit(rs.getInt("UsageLimit"));
                voucher.setPerUserLimit(rs.getInt("PerUserLimit"));
                voucher.setStatus(rs.getString("Status"));
                voucher.setUsageCount(rs.getInt("UsageCount"));
                voucher.setShopVoucher(rs.getBoolean("IsShopVoucher"));
                voucher.setShopID(rs.getObject("ShopID", Long.class));
                listVouchersShop.add(voucher);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listVouchersShop;
    }

    public List<Voucher> getActiveSystemVouchers() {
        List<Voucher> listVouchersSystem = new ArrayList<>();
        String sql = """
                    SELECT
                        VoucherID,
                        Code,
                        DiscountType,
                        Value,
                        MaxAmount,
                        MinOrderAmount,
                        StartAt,
                        EndAt,
                        UsageLimit,
                        PerUserLimit,
                        Status,
                        UsageCount
                    FROM Vouchers WHERE IsShopVoucher = 0
                    AND StartAt <= SYSUTCDATETIME() AND EndAt >= SYSUTCDATETIME()
                    AND  (UsageLimit IS NULL OR UsageCount < UsageLimit)
                    AND [Status] = 'ACTIVE'
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setVoucherID(rs.getLong("VoucherID"));
                voucher.setCode(rs.getString("Code"));
                voucher.setDiscountType(rs.getString("DiscountType"));
                voucher.setValue(rs.getDouble("Value"));
                voucher.setMaxAmount(rs.getDouble("MaxAmount"));
                voucher.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
                voucher.setStartAt(rs.getTimestamp("StartAt"));
                voucher.setEndAt(rs.getTimestamp("EndAt"));
                voucher.setUsageLimit(rs.getInt("UsageLimit"));
                voucher.setPerUserLimit(rs.getInt("PerUserLimit"));
                voucher.setStatus(rs.getString("Status"));
                voucher.setUsageCount(rs.getInt("UsageCount"));
                listVouchersSystem.add(voucher);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listVouchersSystem;
    }

    public Voucher getVoucherByCode(String code, boolean isShopVoucher) {
        String sql = """
                    SELECT
                        VoucherID, Code, DiscountType, Value, MaxAmount,
                        MinOrderAmount, StartAt, EndAt, UsageLimit,
                        PerUserLimit, Status, UsageCount, IsShopVoucher, ShopID
                    FROM Vouchers
                    WHERE Code = ?
                    AND IsShopVoucher = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, code);
            ps.setBoolean(2, isShopVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setVoucherID(rs.getLong("VoucherID"));
                voucher.setCode(rs.getString("Code"));
                voucher.setDiscountType(rs.getString("DiscountType"));
                voucher.setValue(rs.getDouble("Value"));
                voucher.setMaxAmount(rs.getDouble("MaxAmount"));
                voucher.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
                voucher.setStartAt(rs.getTimestamp("StartAt"));
                voucher.setEndAt(rs.getTimestamp("EndAt"));
                voucher.setUsageLimit(rs.getInt("UsageLimit"));
                voucher.setPerUserLimit(rs.getInt("PerUserLimit"));
                voucher.setStatus(rs.getString("Status"));
                voucher.setUsageCount(rs.getInt("UsageCount"));
                voucher.setShopVoucher(rs.getBoolean("IsShopVoucher"));
                voucher.setShopID(rs.getLong("ShopID"));
                return voucher;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Voucher> getAllVouchersByShopId(long shopId) {
        List<Voucher> list = new ArrayList<>();
        String sql = """
                    SELECT * FROM Vouchers
                    WHERE IsShopVoucher = 1 AND ShopID = ?
                    ORDER BY CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();

            Timestamp now = new Timestamp(System.currentTimeMillis());

            while (rs.next()) {
                Voucher v = new Voucher();
                v.setVoucherID(rs.getLong("VoucherID"));
                v.setCode(rs.getString("Code"));
                v.setDiscountType(rs.getString("DiscountType"));
                v.setValue(rs.getDouble("Value"));
                v.setMaxAmount(rs.getObject("MaxAmount") != null ? rs.getDouble("MaxAmount") : null);
                v.setMinOrderAmount(rs.getObject("MinOrderAmount") != null ? rs.getDouble("MinOrderAmount") : null);
                v.setStartAt(rs.getTimestamp("StartAt"));
                v.setEndAt(rs.getTimestamp("EndAt"));
                v.setShopVoucher(rs.getBoolean("IsShopVoucher"));
                v.setShopID(rs.getLong("ShopID"));
                v.setUsageLimit(rs.getObject("UsageLimit") != null ? rs.getInt("UsageLimit") : null);
                v.setCreatedAt(rs.getTimestamp("CreatedAt"));
                v.setUsageCount(rs.getInt("UsageCount"));
                v.setDescription(rs.getString("Description"));
                String status;
                Timestamp start = v.getStartAt();
                Timestamp end = v.getEndAt();
                Integer usageLimit = v.getUsageLimit();
                Integer usageCount = v.getUsageCount();

                if (now.before(start)) {
                    status = "UPCOMING";
                } else if (now.after(end) || (usageLimit != null && usageCount != null && usageCount >= usageLimit)) {
                    status = "EXPIRED";
                } else {
                    status = "ACTIVE";
                }

                v.setStatus(status);
                list.add(v);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Map<String, Integer> getVoucherStatsByShop(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
                    SELECT
                        COUNT(*) AS totalVouchers,
                        SUM(CASE WHEN Status = 'ACTIVE' THEN 1 ELSE 0 END) AS activeCount,
                        SUM(CASE WHEN Status = 'UPCOMING' THEN 1 ELSE 0 END) AS upcomingCount,
                        SUM(CASE WHEN Status = 'EXPIRED' THEN 1 ELSE 0 END) AS expiredCount,
                        SUM(ISNULL(UsageCount, 0)) AS totalUsage
                    FROM Vouchers
                    WHERE ShopID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalVouchers", rs.getInt("totalVouchers"));
                    stats.put("activeCount", rs.getInt("activeCount"));
                    stats.put("upcomingCount", rs.getInt("upcomingCount"));
                    stats.put("expiredCount", rs.getInt("expiredCount"));
                    stats.put("totalUsage", rs.getInt("totalUsage"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // tốt hơn System.out.println
        }

        return stats;
    }

    public Voucher getVoucherByVoucherID(long voucherID) {
        String sql = "SELECT * FROM Vouchers WHERE VoucherID = ?";
        Voucher v = null;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, voucherID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                v = new Voucher();
                v.setVoucherID(rs.getLong("VoucherID"));
                v.setCode(rs.getString("Code"));
                v.setDiscountType(rs.getString("DiscountType"));
                v.setValue(rs.getDouble("Value"));
                v.setMaxAmount(rs.getObject("MaxAmount") != null ? rs.getDouble("MaxAmount") : null);
                v.setMinOrderAmount(rs.getObject("MinOrderAmount") != null ? rs.getDouble("MinOrderAmount") : null);
                v.setStartAt(rs.getTimestamp("StartAt"));
                v.setEndAt(rs.getTimestamp("EndAt"));
                v.setShopVoucher(rs.getBoolean("IsShopVoucher"));
                v.setShopID(rs.getLong("ShopID"));
                v.setUsageLimit(rs.getObject("UsageLimit") != null ? rs.getInt("UsageLimit") : null);
                v.setUsageCount(rs.getInt("UsageCount"));
                v.setCreatedAt(rs.getTimestamp("CreatedAt"));
                v.setDescription(rs.getString("Description"));

                v.setStatus(rs.getString("Status"));

                System.out.println(v.getCode());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return v;
    }

    public boolean checkVoucherCode(String code, Long shopId) {
        String sql = "SELECT COUNT(*) FROM Vouchers WHERE Code = ? AND ShopID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setLong(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean insertVoucher(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO Vouchers (Code, DiscountType, Value, MaxAmount, MinOrderAmount, StartAt, EndAt, " +
                "UsageLimit, UsageCount, Status, IsShopVoucher, ShopID, Description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, voucher.getCode());
            stmt.setString(2, voucher.getDiscountType());
            stmt.setDouble(3, voucher.getValue());
            if (voucher.getMaxAmount() != null) {
                stmt.setDouble(4, voucher.getMaxAmount());
            } else {
                stmt.setNull(4, java.sql.Types.DECIMAL);
            }
            stmt.setDouble(5, voucher.getMinOrderAmount());
            stmt.setTimestamp(6, voucher.getStartAt());
            stmt.setTimestamp(7, voucher.getEndAt());
            stmt.setInt(8, voucher.getUsageLimit());
            stmt.setInt(9, 0);
            stmt.setString(10, voucher.getStatus());
            stmt.setBoolean(11, voucher.isShopVoucher());
            stmt.setLong(12, voucher.getShopID());
            stmt.setString(13, voucher.getDescription());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteVoucherByBusinessRule(long voucherID) throws SQLException {
        String sql = """
                    SELECT v.VoucherID, v.Status, v.UsageCount,
                           CASE
                               WHEN EXISTS (SELECT 1 FROM Orders o WHERE o.VoucherDiscountID = v.VoucherID) THEN 1
                               WHEN EXISTS (SELECT 1 FROM OrderShops os WHERE os.VoucherID = v.VoucherID) THEN 1
                               ELSE 0
                           END AS UsedInOrders
                    FROM Vouchers v
                    WHERE v.VoucherID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, voucherID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return false;
            }

            String status = rs.getString("Status");
            int usageCount = rs.getInt("UsageCount");
            boolean usedInOrders = rs.getInt("UsedInOrders") > 0;

            if (usedInOrders) {
                return false;
            }

            boolean canDelete = "UPCOMING".equalsIgnoreCase(status) ||
                    ("ACTIVE".equalsIgnoreCase(status) && usageCount == 0);

            if (!canDelete) {
                return false;
            }

            String deleteSql = "DELETE FROM Vouchers WHERE VoucherID = ?";
            try (PreparedStatement deletePs = cn.prepareStatement(deleteSql)) {
                deletePs.setLong(1, voucherID);
                int rows = deletePs.executeUpdate();
                return rows > 0;
            }
        }
    }

    public Long getVoucherIDByVoucherCodeAndShopID(String voucherCode, Long shopID) {
        String sql = "SELECT VoucherID FROM Vouchers WHERE Code = ? AND ShopID = ?";
        Long voucherID = null;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, voucherCode);
            ps.setLong(2, shopID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    voucherID = rs.getLong("VoucherID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voucherID;
    }

    public boolean updateVoucher(Voucher voucher) {
        String sql = """
                    UPDATE Vouchers
                    SET
                        Code = ?,
                        Description = ?,
                        DiscountType = ?,
                        Value = ?,
                        MaxAmount = ?,
                        MinOrderAmount = ?,
                        UsageLimit = ?,
                        StartAt = ?,
                        EndAt = ?,
                        Status = ?
                    WHERE VoucherID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getDescription());
            ps.setString(3, voucher.getDiscountType());
            ps.setDouble(4, voucher.getValue());
            if (voucher.getMaxAmount() != null) {
                ps.setDouble(5, voucher.getMaxAmount());
            } else {
                ps.setNull(5, Types.DOUBLE);
            }
            ps.setDouble(6, voucher.getMinOrderAmount());
            ps.setInt(7, voucher.getUsageLimit());
            ps.setTimestamp(8, voucher.getStartAt());
            ps.setTimestamp(9, voucher.getEndAt());
            ps.setString(10, voucher.getStatus());

            ps.setLong(11, voucher.getVoucherID());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUsageCount(Long voucherId) {
        String sql = "SELECT SUM(UsageCount) AS totalUsage FROM Vouchers WHERE VoucherID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("totalUsage");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateVoucherExpired(Voucher voucher) {
        String sql = """
                    UPDATE Vouchers
                    SET
                        Description = ?,
                        StartAt = ?, 
                        EndAt = ?,
                        Status = ?
                    WHERE VoucherID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, voucher.getDescription());
            ps.setTimestamp(2, voucher.getStartAt());
            ps.setTimestamp(3, voucher.getEndAt());
            ps.setString(4, voucher.getStatus());
            ps.setLong(5, voucher.getVoucherID());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
