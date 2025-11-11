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
                    os.OrderID,
                    os.OrderShopID,
                    s.Name AS ShopName,
                    os.Status AS ShopStatus,
                    os.UpdatedAt,
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
                WHERE os.OrderID = ?
                  AND img.IsPrimary = 1
                ORDER BY os.UpdatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDTO orderShop = new OrderDTO();

                    orderShop.setOrderId(rs.getLong("OrderID"));
                    orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                    orderShop.setShopName(rs.getString("ShopName"));
                    orderShop.setShopStatus(rs.getString("ShopStatus"));
                    orderShop.setUpdateAt(rs.getTimestamp("UpdateAt"));
                    orderShop.setShopFinalAmount(rs.getDouble("ShopFinalAmount"));

                    // --- Product ---
                    orderShop.setProductId(rs.getLong("ProductID"));
                    orderShop.setProductName(rs.getString("ProductName"));
                    orderShop.setImageUrl(rs.getString("ImageUrl"));
                    orderShop.setQuantity(rs.getInt("Quantity"));
                    orderShop.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    orderShop.setSalePrice(rs.getDouble("SalePrice"));
                    orderShop.setSubtotal(rs.getDouble("Subtotal"));

                    boolean canReturn = false;
                    if (orderShop.getUpdateAt() != null && "COMPLETED".equalsIgnoreCase(orderShop.getShopStatus())) {
                        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
                        LocalDate updateDate = orderShop.getUpdateAt().toInstant().atZone(zone).toLocalDate();
                        long days = ChronoUnit.DAYS.between(updateDate, LocalDate.now(zone));
                        canReturn = days >= 0 && days < 7;
                    }
                    orderShop.setCanReturn(canReturn);

                    orderShops.add(orderShop);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getOrderShopsByOrderId: " + e.getMessage());
        }

        return orderShops;
    }

    public long createOrderShop(Connection conn, OrderShop orderShop) {
        // NOTE: VoucherID field is deprecated. OrderShops table has VoucherShopID, VoucherDiscountID, VoucherShipID instead.
        // This method needs refactoring to use the correct voucher columns.
        String sql = """
                    INSERT INTO OrderShops(OrderID, ShopID, Subtotal,
                                           Discount, SystemVoucherDiscount, SystemShippingDiscount, ShippingFee, FinalAmount, [Status], CreatedAt)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, DATEADD(HOUR, 7, SYSUTCDATETIME()))
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderShop.getOrderId());
            ps.setLong(2, orderShop.getShopId());

            ps.setDouble(3, orderShop.getSubtotal());
            ps.setDouble(4, orderShop.getDiscount());
            ps.setDouble(5, orderShop.getSystemVoucherDiscount());
            ps.setDouble(6, orderShop.getSystemShippingDiscount());
            ps.setDouble(7, orderShop.getShippingFee());
            ps.setDouble(8, orderShop.getFinalAmount());
            ps.setString(9, orderShop.getStatus());
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
        String sql = "UPDATE OrderShops SET Status = ?, UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE OrderShopId = ?";
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
        String sql = "UPDATE OrderShops SET Status = ?, UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE OrderID = ?";
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
                        UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
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
                        UpdatedAt = SYSUTCDATETIME()
                    WHERE Status = 'CONFIRM'
                      AND UpdatedAt <= DATEADD(DAY, -7, SYSUTCDATETIME())
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
        String sql = "SELECT OrderShopID FROM OrderShops WHERE Status = 'PENDING_PAYMENT' AND DATEDIFF(HOUR, UpdatedAt, GETDATE()) >= 1";
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

    public OrderShop findById(Connection conn, long orderShopId) throws SQLException {
        String sql = """
                SELECT
                    OrderShopID,
                    OrderID,
                    ShopID,
                    Status,
                    Subtotal,
                    ShippingFee,
                    Discount,
                    FinalAmount,
                    VoucherID,
                    CancelReason,
                    UpdateAt
                FROM OrderShops
                WHERE OrderShopID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderShopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderShop shop = new OrderShop();
                    shop.setOrderShopId(rs.getLong("OrderShopID"));
                    shop.setOrderId(rs.getLong("OrderID"));
                    shop.setShopId(rs.getLong("ShopID"));
                    shop.setStatus(rs.getString("Status"));
                    shop.setSubtotal(rs.getDouble("Subtotal"));
                    shop.setShippingFee(rs.getDouble("ShippingFee"));
                    shop.setDiscount(rs.getDouble("Discount"));
                    shop.setFinalAmount(rs.getDouble("FinalAmount"));
                    shop.setVoucherId(rs.getObject("VoucherID") != null ? rs.getLong("VoucherID") : null);
                    shop.setCancelReason(rs.getString("CancelReason"));
                    shop.setUpdateAt(rs.getTimestamp("UpdateAt"));
                    return shop;
                }
            }
        }
        return null;
    }

    public List<OrderShop> getActiveShopsByOrderId(Connection conn, long orderId) throws SQLException {
        // NOTE: VoucherID field is deprecated. OrderShops table has VoucherShopID, VoucherDiscountID, VoucherShipID instead.
        String sql = """
                SELECT
                    OrderShopID, OrderID, ShopID, Subtotal, ShippingFee,
                    Discount, FinalAmount, Status, VoucherShopID
                FROM OrderShops
                WHERE OrderID = ? AND Status NOT IN ('CANCELLED', 'RETURNED', 'RETURNED_REJECTED')
                """;

        List<OrderShop> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShop s = new OrderShop();
                    s.setOrderShopId(rs.getLong("OrderShopID"));
                    s.setOrderId(rs.getLong("OrderID"));
                    s.setShopId(rs.getLong("ShopID"));
                    s.setSubtotal(rs.getDouble("Subtotal"));
                    s.setShippingFee(rs.getDouble("ShippingFee"));
                    s.setDiscount(rs.getDouble("Discount"));
                    s.setFinalAmount(rs.getDouble("FinalAmount"));
                    s.setStatus(rs.getString("Status"));
                    s.setVoucherId(rs.getLong("VoucherShopID")); // Using VoucherShopID as primary voucher
                    list.add(s);
                }
            }
        }
        return list;
    }

    public boolean update(Connection conn, OrderShop shop) throws SQLException {
        // NOTE: VoucherID field is deprecated. OrderShops table has VoucherShopID, VoucherDiscountID, VoucherShipID instead.
        // This method needs refactoring to use the correct voucher columns.
        String sql = """
                UPDATE OrderShops
                SET
                    Status = ?,
                    CancelReason = ?,
                    FinalAmount = ?,
                    UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()),
                    Discount = ?,
                    ShippingFee = ?
                WHERE OrderShopID = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, shop.getStatus());
            ps.setString(2, shop.getCancelReason());
            ps.setDouble(3, shop.getFinalAmount());
            ps.setDouble(4, shop.getDiscount());
            ps.setDouble(5, shop.getShippingFee());
            ps.setLong(6, shop.getOrderShopId());

            return ps.executeUpdate() > 0;
        }
    }

    public List<OrderShop> getAllShopsByOrderId(Connection conn, long orderId) throws SQLException {
        String sql = """
                SELECT
                    OrderShopID, OrderID, ShopID, Subtotal, ShippingFee,
                    Discount, FinalAmount, Status, VoucherID
                FROM OrderShops
                WHERE OrderID = ?
                """;

        List<OrderShop> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShop s = new OrderShop();
                    s.setOrderShopId(rs.getLong("OrderShopID"));
                    s.setOrderId(rs.getLong("OrderID"));
                    s.setShopId(rs.getLong("ShopID"));
                    s.setSubtotal(rs.getDouble("Subtotal"));
                    s.setShippingFee(rs.getDouble("ShippingFee"));
                    s.setDiscount(rs.getDouble("Discount"));
                    s.setFinalAmount(rs.getDouble("FinalAmount"));
                    s.setStatus(rs.getString("Status"));
                    s.setVoucherId(rs.getObject("VoucherID") != null ? rs.getLong("VoucherID") : null);
                    list.add(s);
                }
            }
        }
        return list;
    }
}
