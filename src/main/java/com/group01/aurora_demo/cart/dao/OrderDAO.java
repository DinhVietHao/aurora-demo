package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.cart.dao.dto.OrderDTO;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class OrderDAO {
    public long createOrder(Connection conn, Order order) {
        String sql = """
                    INSERT INTO Orders(UserID, AddressID, VoucherDiscountID, VoucherShipID,
                                       TotalAmount, DiscountAmount, TotalShippingFee, ShippingDiscount,
                                       FinalAmount, OrderStatus)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    public List<OrderDTO> getOrdersByStatus(long userId, String status) {
        List<OrderDTO> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        s.ShopID,
                        s.Name AS ShopName,
                        os.OrderShopID,
                        os.FinalAmount AS ShopTotal,
                        os.Status AS ShopStatus,
                        o.OrderID,
                        o.OrderStatus,
                        o.CreatedAt AS OrderDate,
                        p.ProductID,
                        p.Title AS ProductName,
                        img.Url AS ImageUrl,
                        oi.Quantity,
                        oi.OriginalPrice,
                        oi.SalePrice,
                        os.Subtotal
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN Shops s ON os.ShopID = s.ShopID
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    JOIN Products p ON oi.ProductID = p.ProductID
                    JOIN ProductImages img ON p.ProductID = img.ProductID
                    WHERE o.UserID = ?
                      AND img.IsPrimary = 1
                """);
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND o.OrderStatus = ? ");
        }
        sql.append(" ORDER BY o.CreatedAt DESC");
        try (Connection cn = DataSourceProvider.get().getConnection();) {
            PreparedStatement ps = cn.prepareStatement(sql.toString());
            ps.setLong(1, userId);
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
                ps.setString(2, status.toUpperCase());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getLong("OrderID"));
                order.setShopId(rs.getLong("ShopID"));
                order.setShopName(rs.getString("ShopName"));
                order.setProductId(rs.getLong("ProductID"));
                order.setProductName(rs.getString("ProductName"));
                order.setImageUrl(rs.getString("ImageUrl"));
                order.setQuantity(rs.getInt("Quantity"));
                order.setOriginalPrice(rs.getDouble("OriginalPrice"));
                order.setSalePrice(rs.getDouble("SalePrice"));
                order.setSubtotal(rs.getDouble("Subtotal"));
                order.setFinalAmount(rs.getDouble("ShopTotal"));
                order.setShopStatus(rs.getString("ShopStatus"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setOrderDate(rs.getDate("OrderDate"));
                orders.add(order);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return orders;
    }
}
