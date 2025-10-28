package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;

public class OrderDAO {
    public long createOrder(Connection conn, Order order) {
        String sql = """
                    INSERT INTO Orders(UserID, Address, VoucherDiscountID, VoucherShipID,
                                       TotalAmount, DiscountAmount, TotalShippingFee, ShippingDiscount,
                                       FinalAmount, OrderStatus)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getUserId());
            ps.setString(2, order.getAddress());

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

    public List<Order> getOrdersByUserId(long userId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String sql = """
                    SELECT
                        o.OrderID,
                        o.Address,
                        u.FullName,
                        o.TotalAmount,
                        o.DiscountAmount,
                        o.TotalShippingFee,
                        o.ShippingDiscount,
                        o.FinalAmount,
                        o.CreatedAt
                    FROM Orders o
                    JOIN Users u ON o.UserID = u.UserID
                    WHERE o.UserID = ?
                    ORDER BY o.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getLong("OrderID"));
                order.setAddress(rs.getString("Address"));
                order.setCustomerName(rs.getString("FullName"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setDiscountAmount(rs.getDouble("DiscountAmount"));
                order.setTotalShippingFee(rs.getDouble("TotalShippingFee"));
                order.setShippingDiscount(rs.getDouble("ShippingDiscount"));
                order.setFinalAmount(rs.getDouble("FinalAmount"));
                order.setCreatedAt(rs.getDate("CreatedAt"));
                orders.add(order);
            }
        }

        return orders;
    }

    public List<Order> getOrdersByShopAndStatus(Long shopId, String status) throws SQLException {
        String sql = """
                    SELECT
                        o.OrderID,
                        o.OrderStatus,
                        o.TotalAmount,
                        o.CreatedAt,
                        u.FullName AS CustomerName
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN Users u ON o.UserID = u.UserID
                    WHERE os.ShopID = ? AND o.OrderStatus = ?
                    ORDER BY o.CreatedAt DESC
                """;

        List<Order> orders = new ArrayList<>();

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getLong("OrderID"));
                    order.setOrderStatus(rs.getString("OrderStatus"));
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    order.setCustomerName(rs.getString("CustomerName"));
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    public boolean updateOrderStatus(long orderId, String newStatus) {
        String sql = "UPDATE Orders SET OrderStatus = ? WHERE OrderID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Order getOrderById(long orderId) {
        String sql = """
                    SELECT
                        OrderID,
                        UserID,
                        VoucherDiscountID,
                        VoucherShipID,
                        TotalAmount,
                        DiscountAmount,
                        TotalShippingFee,
                        ShippingDiscount,
                        FinalAmount,
                        OrderStatus,
                        CreatedAt
                    FROM Orders WHERE OrderID = ?
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getLong("OrderID"));
                    order.setUserId(rs.getLong("UserID"));
                    order.setVoucherDiscountId(
                            rs.getObject("VoucherDiscountID") != null ? rs.getLong("VoucherDiscountID") : null);
                    order.setVoucherShipId(rs.getObject("VoucherShipID") != null ? rs.getLong("VoucherShipID") : null);
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    order.setTotalShippingFee(rs.getDouble("TotalShippingFee"));
                    order.setShippingDiscount(rs.getDouble("ShippingDiscount"));
                    order.setFinalAmount(rs.getDouble("FinalAmount"));
                    order.setOrderStatus(rs.getString("OrderStatus"));
                    order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return order;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Order finById(Connection conn, long orderId) {
        String sql = """
                    SELECT
                        OrderID,
                        UserID,
                        VoucherDiscountID,
                        VoucherShipID,
                        TotalAmount,
                        DiscountAmount,
                        TotalShippingFee,
                        ShippingDiscount,
                        FinalAmount,
                        OrderStatus,
                        CreatedAt
                    FROM Orders WHERE OrderID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getLong("OrderID"));
                    order.setUserId(rs.getLong("UserID"));
                    order.setVoucherDiscountId(
                            rs.getObject("VoucherDiscountID") != null ? rs.getLong("VoucherDiscountID") : null);
                    order.setVoucherShipId(rs.getObject("VoucherShipID") != null ? rs.getLong("VoucherShipID") : null);
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    order.setTotalShippingFee(rs.getDouble("TotalShippingFee"));
                    order.setShippingDiscount(rs.getDouble("ShippingDiscount"));
                    order.setFinalAmount(rs.getDouble("FinalAmount"));
                    order.setOrderStatus(rs.getString("OrderStatus"));
                    order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return order;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Connection conn, Order order) throws SQLException {
        String sql = """
                UPDATE Orders
                SET
                    OrderStatus = ?,
                    TotalAmount = ?,
                    DiscountAmount = ?,
                    TotalShippingFee = ?,
                    FinalAmount = ?,
                    VoucherDiscountID = ?,
                    VoucherShipID = ?,
                    CancelledAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                WHERE OrderID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getOrderStatus());
            ps.setDouble(2, order.getTotalAmount());
            ps.setDouble(3, order.getDiscountAmount());
            ps.setDouble(4, order.getTotalShippingFee());
            ps.setDouble(5, order.getFinalAmount());

            if (order.getVoucherDiscountId() != null)
                ps.setLong(6, order.getVoucherDiscountId());
            else
                ps.setNull(6, Types.BIGINT);

            if (order.getVoucherShipId() != null)
                ps.setLong(7, order.getVoucherShipId());
            else
                ps.setNull(7, Types.BIGINT);
            ps.setLong(8, order.getOrderId());
            return ps.executeUpdate() > 0;
        }
    }
}
