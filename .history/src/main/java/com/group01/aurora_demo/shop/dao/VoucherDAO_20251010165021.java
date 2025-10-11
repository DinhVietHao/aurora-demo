package com.group01.aurora_demo.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.shop.model.Voucher;

public class VoucherDAO {
    public List<Voucher> getVouchersByShopId(long shopId) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return v;
    }

    public boolean checkVoucherCode(String code, Long shopId) {
        String sql = "SELECT COUNT(*) FROM Vouchers WHERE Code = ? AND ShopID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, code);

            ps.setLong(3, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // true nếu bị trùng
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
