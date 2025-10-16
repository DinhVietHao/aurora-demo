package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.OrderShop;

public class OrderShopDAO {
    public long createOrderShop(Connection conn, OrderShop orderShop) {
        String sql = """
                    INSERT INTO OrderShops(OrderID, ShopID, VoucherID, Subtotal,
                                           Discount, ShippingFee, FinalAmount, [Status], CreatedAt)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATEADD(HOUR, 7, SYSUTCDATETIME()))
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderShop.getOrderId());
            ps.setLong(2, orderShop.getShopId());
            if (orderShop.getVoucherId() != null)
                ps.setLong(3, orderShop.getVoucherId());
            else
                ps.setNull(3, Types.BIGINT);

            ps.setDouble(4, orderShop.getSubtotal());
            ps.setDouble(5, orderShop.getDiscount());
            ps.setDouble(6, orderShop.getShippingFee());
            ps.setDouble(7, orderShop.getFinalAmount());
            ps.setString(8, orderShop.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
}
