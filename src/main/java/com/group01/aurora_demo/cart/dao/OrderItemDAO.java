package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.OrderItem;

public class OrderItemDAO {
    public long createOrderItem(Connection conn, OrderItem orderItem) {
        String sql = """
                INSERT INTO OrderItems (OrderShopID, ProductID, FlashSaleItemID,
                                        Quantity, OriginalPrice, SalePrice, Subtotal, VATRate)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderItem.getOrderShopId());
            ps.setLong(2, orderItem.getProductId());

            if (orderItem.getFlashSaleItemId() != null) {
                ps.setLong(3, orderItem.getFlashSaleItemId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setInt(4, orderItem.getQuantity());
            ps.setDouble(5, orderItem.getOriginalPrice());
            ps.setDouble(6, orderItem.getSalePrice());
            ps.setDouble(7, orderItem.getSubtotal());
            ps.setDouble(8, orderItem.getVatRate());

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
