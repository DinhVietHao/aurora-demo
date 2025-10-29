package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.cart.dao.dto.OrderShopDTO;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.common.config.DataSourceProvider;

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

    public List<OrderItem> getItemsByOrderShopId(Long orderShopId) {
        String sql = "SELECT ProductID, Quantity FROM OrderItems WHERE OrderShopID = ?";
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderShopId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setProductId(rs.getLong("ProductID"));
                item.setQuantity(rs.getInt("Quantity"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<OrderShopDTO> getOrderItemsByOrderShopId(long orderShopId) {
        List<OrderShopDTO> orderItems = new ArrayList<>();
        String sql = """
                SELECT
                    os.OrderShopID,
                    s.Name AS ShopName,
                    os.Status AS ShopStatus,
                    os.UpdatedAt,
                    os.ShopDiscount AS ShopDiscount,
                    os.ShippingFee AS ShopShippingFee,
                    os.SystemDiscount,
                    os.SystemShippingDiscount,
                    os.FinalAmount AS ShopFinalAmount,
                    p.ProductID,
                    p.Title AS ProductName,
                    img.Url AS ImageUrl,
                    oi.Quantity,
                    oi.OriginalPrice,
                    oi.SalePrice,
                    oi.Subtotal
                FROM OrderShops os
                JOIN Shops s ON os.ShopID = s.ShopID
                JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                JOIN Products p ON oi.ProductID = p.ProductID
                JOIN ProductImages img ON p.ProductID = img.ProductID
                WHERE os.OrderShopID = ?
                  AND img.IsPrimary = 1
                ORDER BY os.UpdatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, orderShopId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShopDTO order = new OrderShopDTO();
                    order.setOrderShopId(rs.getLong("OrderShopID"));
                    order.setShopName(rs.getString("ShopName"));
                    order.setShopStatus(rs.getString("ShopStatus"));
                    order.setUpdatedAt(rs.getTimestamp("UpdatedAt"));

                    // --- Sản phẩm ---
                    order.setProductId(rs.getLong("ProductID"));
                    order.setProductName(rs.getString("ProductName"));
                    order.setImageUrl(rs.getString("ImageUrl"));
                    order.setQuantity(rs.getInt("Quantity"));
                    order.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    order.setSalePrice(rs.getDouble("SalePrice"));
                    order.setSubtotal(rs.getDouble("Subtotal"));

                    // --- Shop ---
                    order.setShopDiscount(rs.getDouble("ShopDiscount"));
                    order.setShopShippingFee(rs.getDouble("ShopShippingFee"));
                    order.setSystemDiscount(rs.getDouble("SystemDiscount"));
                    order.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                    order.setShopFinalAmount(rs.getDouble("ShopFinalAmount"));

                    orderItems.add(order);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}