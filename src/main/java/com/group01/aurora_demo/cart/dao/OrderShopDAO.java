package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.OrderShop;

public class OrderShopDAO {
    public boolean createOrderShop(Connection conn, OrderShop orderShop) throws SQLException {
        String sql = """
                    INSERT INTO OrderShops(OrderID, ShopID, VoucherID, Subtotal,
                                           Discount, ShippingFee, FinalAmount, [Status])
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
