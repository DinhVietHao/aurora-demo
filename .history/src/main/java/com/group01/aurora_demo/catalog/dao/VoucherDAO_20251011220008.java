package com.group01.aurora_demo.catalog.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.group01.aurora_demo.shop.model.Voucher;

public class VoucherDAO {
    public boolean insertVoucher(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO Vouchers (Code, DiscountType, Value, MaxAmount, MinOrderAmount, StartAt, EndAt, " +
                     "UsageLimit, PerUserLimit, Status, IsShopVoucher, ShopID, Description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            if (voucher.getPerUserLimit() != null) {
                stmt.setInt(9, voucher.getPerUserLimit());
            } else {
                stmt.setNull(9, java.sql.Types.INTEGER);
            }
            stmt.setString(10, voucher.getStatus());
            stmt.setBoolean(11, voucher.isShopVoucher());
            if (voucher.getShopId() != null) {
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
