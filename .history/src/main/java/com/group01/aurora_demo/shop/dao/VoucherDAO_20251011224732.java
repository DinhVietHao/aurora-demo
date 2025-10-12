package com.group01.aurora_demo.shop.dao;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
                        SUM(CASE
                                WHEN GETDATE() < StartAt THEN 0
                                WHEN GETDATE() > EndAt OR (ISNULL(UsageLimit, 0) <= 0) THEN 0
                                ELSE 1
                            END) AS activeCount,
                        SUM(CASE WHEN GETDATE() < StartAt THEN 1 ELSE 0 END) AS upcomingCount,
                        SUM(CASE WHEN GETDATE() > EndAt OR (ISNULL(UsageLimit, 0) <= 0) THEN 1 ELSE 0 END) AS expiredCount,
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
            System.out.println(e.getMessage());
        }
        return stats;
    }

    public Voucher getVoucherByVoucherCode(String voucherCode) {
        String sql = "SELECT * FROM Vouchers WHERE Code = ?";
        Voucher v = null;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, voucherCode);
            ResultSet rs = ps.executeQuery();

            Timestamp now = new Timestamp(System.currentTimeMillis());

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

                Timestamp start = v.getStartAt();
                Timestamp end = v.getEndAt();
                Integer usageLimit = v.getUsageLimit();
                Integer usageCount = v.getUsageCount();

                String status;
                if (now.before(start)) {
                    status = "UPCOMING";
                } else if (now.after(end) || (usageLimit != null && usageCount != null && usageCount >= usageLimit)) {
                    status = "EXPIRED";
                } else {
                    status = "ACTIVE";
                }
                v.setStatus(status);
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
            if (voucher.getShopI() != null) {
                stmt.setLong(12, voucher.getShopId());
            } else {
                stmt.setNull(12, java.sql.Types.BIGINT);
            }
            stmt.setString(13, voucher.getDescription());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Optional: Check if code exists
    public boolean isCodeExists(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vouchers WHERE Code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Optional: Get vouchers by shop ID
    public List<Voucher> getVouchersByShopId(Long shopId) throws SQLException {
        // Implement if needed
        return null;
    }

}
