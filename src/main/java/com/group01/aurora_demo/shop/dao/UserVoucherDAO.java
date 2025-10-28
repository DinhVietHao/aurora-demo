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
        String sql = """
                UPDATE UserVouchers
                SET Status = 'ACTIVE'
                WHERE UserVoucherID = (
                    SELECT TOP 1 UserVoucherID
                    FROM UserVouchers
                    WHERE VoucherID = ? AND UserID = ? AND Status = 'USED'
                    ORDER BY UserVoucherID DESC
                )
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, voucherId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
