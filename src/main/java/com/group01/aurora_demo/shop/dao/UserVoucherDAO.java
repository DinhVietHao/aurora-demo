package com.group01.aurora_demo.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group01.aurora_demo.common.config.DataSourceProvider;

public class UserVoucherDAO {
    public boolean insertUserVoucher(Connection conn, Long userId, Long voucherId) {
        String sql = "INSERT INTO UserVouchers(UserID, VoucherID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, userId);
            ps.setLong(2, voucherId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public int getUserVoucherUsageCount(Long userId, Long voucherId) {
        String sql = "SELECT COUNT(*) FROM UserVouchers WHERE UserID = ? AND VoucherID = ? AND [Status] = 'USED'";
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, voucherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean restoreUserVoucher(Connection conn, long voucherId, long userId) {
        String selectSql = """
                    SELECT TOP 1 UserVoucherID
                    FROM UserVouchers
                    WHERE VoucherID = ? AND UserID = ? AND Status = 'USED'
                    ORDER BY UserVoucherID DESC
                """;

        String updateSql = "UPDATE UserVouchers SET Status = 'ACTIVE' WHERE UserVoucherID = ?";

        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
            selectPs.setLong(1, voucherId);
            selectPs.setLong(2, userId);

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    long userVoucherId = rs.getLong("UserVoucherID");

                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setLong(1, userVoucherId);
                        int affectedRows = updatePs.executeUpdate();
                        return affectedRows > 0;
                    }
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
