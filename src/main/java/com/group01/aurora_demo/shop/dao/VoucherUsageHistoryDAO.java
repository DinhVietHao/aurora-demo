package com.group01.aurora_demo.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.shop.model.VoucherUsageHistory;

public class VoucherUsageHistoryDAO {

    public List<VoucherUsageHistory> getVoucherUsageHistory(long voucherID) throws SQLException {
        List<VoucherUsageHistory> list = new ArrayList<>();

        String sql = """
                SELECT
                    os.OrderShopID AS OrderID,
                    u.FullName AS CustomerName,
                    os.FinalAmount AS OrderValue,
                    os.ShopDiscount AS DiscountValue,
                    os.CreatedAt AS UsedAt,
                    os.Status AS OrderStatus
                FROM OrderShops os
                JOIN Users u ON os.UserID = u.UserID
                WHERE os.VoucherShopID = ?
                ORDER BY os.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, voucherID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VoucherUsageHistory h = new VoucherUsageHistory();
                    h.setOrderId(rs.getLong("OrderID"));
                    h.setCustomerName(rs.getString("CustomerName"));
                    h.setOrderValue(rs.getDouble("OrderValue"));
                    h.setDiscountValue(rs.getDouble("DiscountValue"));
                    h.setUsedAt(rs.getTimestamp("UsedAt"));
                    h.setOrderStatus(rs.getString("OrderStatus"));
                    list.add(h);
                }
            }
        }

        return list;
    }

}
