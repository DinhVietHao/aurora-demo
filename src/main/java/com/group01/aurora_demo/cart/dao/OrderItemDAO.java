package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.OrderItem;

public class OrderItemDAO {
    public boolean createOrderItem(Connection conn, OrderItem orderItem) {
        String sql = """
                INSERT INTO OrderItems (OrderShopID, ProductID, FlashSaleItemID,
                                        Quantity, UnitPrice, Subtotal, VATRate)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderItem.getOrderShopId());
            ps.setLong(2, orderItem.getProductId());

            if (orderItem.getFlashSaleItemId() != null) {
                ps.setLong(3, orderItem.getFlashSaleItemId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setInt(4, orderItem.getQuantity());
            ps.setDouble(5, orderItem.getUnitPrice());
            ps.setDouble(6, orderItem.getSubtotal());
            ps.setDouble(7, orderItem.getVatRate());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
