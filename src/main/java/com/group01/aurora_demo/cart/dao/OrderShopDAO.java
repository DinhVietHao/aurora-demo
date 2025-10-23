package com.group01.aurora_demo.cart.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.cart.dao.dto.OrderDTO;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class OrderShopDAO {

    public List<OrderDTO> getOrderShopsByOrderId(long orderId) {
        List<OrderDTO> orderShops = new ArrayList<>();
        String sql = """
                    SELECT
                    o.OrderID,
                    o.VoucherDiscountID,
                    o.TotalShippingFee,
                    o.ShippingDiscount AS SystemShippingDiscount,
                    o.TotalAmount,

                    os.OrderShopID,
                    s.Name AS ShopName,
                    os.Discount AS ShopDiscount,
                    os.ShippingFee AS ShopShippingFee,
                    os.Status AS ShopStatus,
                    os.UpdateAt,

                    p.ProductID,
                    p.Title AS ProductName,
                    img.Url AS ImageUrl,
                    oi.Quantity,
                    oi.OriginalPrice,
                    oi.SalePrice,
                    oi.Subtotal
                FROM Orders o
                JOIN OrderShops os ON o.OrderID = os.OrderID
                JOIN Shops s ON os.ShopID = s.ShopID
                JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                JOIN Products p ON oi.ProductID = p.ProductID
                JOIN ProductImages img ON p.ProductID = img.ProductID
                WHERE o.OrderID = ?
                  AND img.IsPrimary = 1
                ORDER BY os.UpdateAt DESC
                    """;

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, orderId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDTO orderShop = new OrderDTO();

                orderShop.setOrderId(rs.getLong("OrderID"));
                orderShop.setSystemVoucherId(rs.getLong("VoucherDiscountID"));
                orderShop.setTotalShippingFee(rs.getDouble("TotalShippingFee"));
                orderShop.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                orderShop.setTotalAmount(rs.getDouble("TotalAmount"));

                // --- Shop ---
                orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                orderShop.setShopName(rs.getString("ShopName"));
                orderShop.setShopDiscount(rs.getDouble("ShopDiscount"));
                orderShop.setShopShippingFee(rs.getDouble("ShopShippingFee"));
                orderShop.setShopStatus(rs.getString("ShopStatus"));
                orderShop.setUpdateAt(rs.getDate("UpdateAt"));

                // --- Product ---
                orderShop.setProductId(rs.getLong("ProductID"));
                orderShop.setProductName(rs.getString("ProductName"));
                orderShop.setImageUrl(rs.getString("ImageUrl"));
                orderShop.setQuantity(rs.getInt("Quantity"));
                orderShop.setOriginalPrice(rs.getDouble("OriginalPrice"));
                orderShop.setSalePrice(rs.getDouble("SalePrice"));
                orderShop.setSubtotal(rs.getDouble("Subtotal"));
                boolean canReturn = "COMPLETED".equalsIgnoreCase(orderShop.getShopStatus())
                        && ChronoUnit.DAYS.between(
                                orderShop.getUpdateAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                LocalDate.now()) < 7;

                orderShop.setCanReturn(canReturn);

                orderShops.add(orderShop);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return orderShops;
    }

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

    public boolean updateOrderShopStatus(long orderShopId, String newStatus) {
        String sql = "UPDATE OrderShops SET Status = ?, UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE OrderShopId = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderShopId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderShopStatusByOrderId(long orderId, String newStatus) {
        String sql = "UPDATE OrderShops SET Status = ?, UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE OrderID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelOrderShop(Long orderShopId, String cancelReason) {
        String sql = "{CALL CancelOrderShop(?, ?)}";

        try (Connection conn = DataSourceProvider.get().getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setLong(1, orderShopId);
            cs.setString(2, cancelReason);
            cs.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnOrderShop(long orderShopId, String returnReason) {
        String sql = """
                    UPDATE OrderShops
                    SET Status = 'RETURNED_REQUESTED',
                        ReturnReason = ?,
                        UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                    WHERE OrderShopID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, returnReason);
            ps.setLong(2, orderShopId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int autoCompleteConfirmOrders() {
        String sql = """
                    UPDATE OrderShops
                    SET Status = 'COMPLETED',
                        UpdateAt = SYSUTCDATETIME()
                    WHERE Status = 'CONFIRM'
                      AND UpdateAt <= DATEADD(DAY, -7, SYSUTCDATETIME())
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int autoCancelPendingPaymentOrders() {
        String sql = "SELECT OrderShopID FROM OrderShops WHERE Status = 'PENDING_PAYMENT' AND DATEDIFF(HOUR, UpdateAt, GETDATE()) >= 1";
        int count = 0;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long orderShopId = rs.getLong("OrderShopID");
                if (cancelOrderShop(orderShopId, "Quá thời hạn thanh toán")) {
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

}
