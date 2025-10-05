package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.cart.model.Voucher;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class VoucherDAO {
    public List<Voucher> getShopVouchers(long shopId) {
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
                voucher.setVoucherId(rs.getLong("VoucherID"));
                voucher.setCode(rs.getString("Code"));
                voucher.setDiscountType(rs.getString("DiscountType"));
                voucher.setValue(rs.getDouble("Value"));
                voucher.setMaxAmount(rs.getDouble("MaxAmount"));
                voucher.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
                voucher.setStartAt(rs.getDate("StartAt"));
                voucher.setEndAt(rs.getDate("EndAt"));
                voucher.setUsageLimit(rs.getInt("UsageLimit"));
                voucher.setPerUserLimit(rs.getInt("PerUserLimit"));
                voucher.setStatus(rs.getString("Status"));
                voucher.setUsageCount(rs.getInt("UsageCount"));
                voucher.setShopVoucher(rs.getBoolean("IsShopVoucher"));
                voucher.setShopId(rs.getObject("ShopID", Long.class));
                listVouchersShop.add(voucher);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listVouchersShop;
    }

    public List<Voucher> getSystemVouchers() {
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
                voucher.setVoucherId(rs.getLong("VoucherID"));
                voucher.setCode(rs.getString("Code"));
                voucher.setDiscountType(rs.getString("DiscountType"));
                voucher.setValue(rs.getDouble("Value"));
                voucher.setMaxAmount(rs.getDouble("MaxAmount"));
                voucher.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
                voucher.setStartAt(rs.getDate("StartAt"));
                voucher.setEndAt(rs.getDate("EndAt"));
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

    public static void main(String[] args) {
        // Test thử chức năng tạo giỏ hàng
        VoucherDAO voucherDAO = new VoucherDAO();
        System.out.println(voucherDAO.getShopVouchers(1));
    }

}
