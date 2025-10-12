package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.Order;

public class OrderDAO {
    public boolean createOrder(Connection conn, Order order) throws SQLException {
        String sql = """
                    INSERT INTO Orders(UserID, AddressID, VoucherDiscountID, VoucherShipID,
                                       TotalAmount, DiscountAmount, ShippingFee, ShippingDiscount,
                                       FinalAmount, OrderStatus)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, order.getUserId());
            ps.setLong(2, order.getAddressId());

            if (order.getVoucherDiscountId() != null)
                ps.setLong(3, order.getVoucherDiscountId());
            else
                ps.setNull(3, Types.BIGINT);

            if (order.getVoucherShipId() != null)
                ps.setLong(4, order.getVoucherShipId());
            else
                ps.setNull(4, Types.BIGINT);

            ps.setDouble(5, order.getTotalAmount());
            ps.setDouble(6, order.getDiscountAmount());
            ps.setDouble(7, order.getTotalShippingFee());
            ps.setDouble(8, order.getShippingDiscount());
            ps.setDouble(9, order.getFinalAmount());
            ps.setString(10, order.getOrderStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
