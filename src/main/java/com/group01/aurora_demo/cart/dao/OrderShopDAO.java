package com.group01.aurora_demo.cart.dao;

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

import com.group01.aurora_demo.cart.dao.dto.OrderShopDTO;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class OrderShopDAO {
    public List<OrderShop> getOrderShopsByUserId(long userId) {
        List<OrderShop> orderShops = new ArrayList<>();

        String sql = """
                    SELECT
                        os.OrderShopID,
                        os.ShopID,
                        os.Status,
                        os.UpdatedAt,
                        os.FinalAmount,
                        os.ShippingFee,
                        os.Subtotal,
                        os.Address,
                        os.CreatedAt
                    FROM OrderShops os
                    JOIN Shops s ON os.ShopID = s.ShopID
                    WHERE os.UserID = ?
                    ORDER BY os.UpdatedAt DESC
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShop orderShop = new OrderShop();
                    orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                    orderShop.setShopId(rs.getLong("ShopID"));
                    orderShop.setStatus(rs.getString("Status"));
                    orderShop.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    orderShop.setFinalAmount(rs.getDouble("FinalAmount"));
                    orderShop.setShippingFee(rs.getDouble("ShippingFee"));
                    orderShop.setSubtotal(rs.getDouble("Subtotal"));
                    orderShop.setAddress(rs.getString("Address"));
                    orderShop.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    orderShops.add(orderShop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderShops;
    }

    public List<OrderShopDTO> getOrderShopsByOrderShopId(String groupOrderCode) {
        List<OrderShopDTO> orderShops = new ArrayList<>();
        String sql = """
                SELECT
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
                WHERE os.GroupOrderCode = ?
                  AND img.IsPrimary = 1
                ORDER BY os.UpdatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, groupOrderCode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShopDTO orderShop = new OrderShopDTO();
                    orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                    orderShop.setShopName(rs.getString("ShopName"));
                    orderShop.setShopStatus(rs.getString("ShopStatus"));
                    orderShop.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
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
                    if (orderShop.getUpdatedAt() != null && "COMPLETED".equalsIgnoreCase(orderShop.getShopStatus())) {
                        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
                        LocalDate updateDate = orderShop.getUpdatedAt().toInstant().atZone(zone).toLocalDate();
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

    public long createOrderShop(Connection conn, OrderShop orderShop) throws SQLException {
        String sql = """
                    INSERT INTO OrderShops (
                        UserID, ShopID, Address,
                        VoucherShopID, VoucherDiscountID, VoucherShipID,
                        Subtotal, ShopDiscount, SystemDiscount,
                        ShippingFee, SystemShippingDiscount, FinalAmount,
                        Status, CreatedAt
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DATEADD(HOUR, 7, SYSUTCDATETIME()))
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderShop.getUserId());
            ps.setLong(2, orderShop.getShopId());
            ps.setString(3, orderShop.getAddress());
            if (orderShop.getVoucherShopId() != null)
                ps.setLong(4, orderShop.getVoucherShopId());
            else
                ps.setNull(4, Types.BIGINT);
            if (orderShop.getVoucherDiscountId() != null)
                ps.setLong(5, orderShop.getVoucherDiscountId());
            else
                ps.setNull(5, Types.BIGINT);
            if (orderShop.getVoucherShipId() != null)
                ps.setLong(6, orderShop.getVoucherShipId());
            else
                ps.setNull(6, Types.BIGINT);
            ps.setDouble(7, orderShop.getSubtotal());
            ps.setDouble(8, orderShop.getShopDiscount());
            ps.setDouble(9, orderShop.getSystemDiscount());
            ps.setDouble(10, orderShop.getShippingFee());
            ps.setDouble(11, orderShop.getSystemShippingDiscount());
            ps.setDouble(12, orderShop.getFinalAmount());
            ps.setString(13, orderShop.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
            }

        }
        return -1;
    }

    public boolean updateOrderShopStatusByGroupOrderCode(String groupOrderCode, String newStatus) {
        String sql = "UPDATE OrderShops SET Status = ?, UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE GroupOrderCode = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, groupOrderCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public boolean cancelOrderShop(long orderShopId, String reason) {
        String sql = """
                    UPDATE OrderShops
                    SET Status = 'CANCELLED',
                        CancelReason = ?,
                        UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                    WHERE OrderShopID = ?
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setLong(2, orderShopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
                    ShopID,
                    Status,
                    Subtotal,
                    ShippingFee,
                    ShopDiscount,
                    FinalAmount,
                    VoucherShopID,
                    CancelReason,
                    UpdatedAt
                FROM OrderShops
                WHERE OrderShopID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderShopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderShop shop = new OrderShop();
                    shop.setOrderShopId(rs.getLong("OrderShopID"));
                    shop.setShopId(rs.getLong("ShopID"));
                    shop.setStatus(rs.getString("Status"));
                    shop.setSubtotal(rs.getDouble("Subtotal"));
                    shop.setShippingFee(rs.getDouble("ShippingFee"));
                    shop.setShopDiscount(rs.getDouble("ShopDiscount"));
                    shop.setFinalAmount(rs.getDouble("FinalAmount"));
                    shop.setVoucherShopId(rs.getObject("VoucherShopID") != null ? rs.getLong("VoucherShopID") : null);
                    shop.setCancelReason(rs.getString("CancelReason"));
                    shop.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    return shop;
                }
            }
        }
        return null;
    }

    public List<OrderShop> getActiveShopsByOrderId(Connection conn, long OrderShopId) throws SQLException {
        String sql = """
                 SELECT
                    OrderShopID, ShopID, Subtotal, ShippingFee,
                    ShopDiscount, FinalAmount, Status, VoucherShopID
                FROM OrderShops
                WHERE OrderShopID = ? AND Status NOT IN ('CANCELLED', 'RETURNED', 'RETURNED_REJECTED')
                """;

        List<OrderShop> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, OrderShopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShop s = new OrderShop();
                    s.setOrderShopId(rs.getLong("OrderShopID"));
                    s.setShopId(rs.getLong("ShopID"));
                    s.setSubtotal(rs.getDouble("Subtotal"));
                    s.setShippingFee(rs.getDouble("ShippingFee"));
                    s.setShopDiscount(rs.getDouble("ShopDiscount"));
                    s.setFinalAmount(rs.getDouble("FinalAmount"));
                    s.setStatus(rs.getString("Status"));
                    s.setVoucherShopId(rs.getLong("VoucherShopID"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public boolean update(Connection conn, OrderShop shop) throws SQLException {
        String sql = """
                UPDATE OrderShops
                SET
                    Status = ?,
                    CancelReason = ?,
                    FinalAmount = ?,
                    UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()),
                    ShopDiscount = ?,
                    ShippingFee = ?,
                    VoucherShopID = ?
                WHERE OrderShopID = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, shop.getStatus());
            ps.setString(2, shop.getCancelReason());
            ps.setDouble(3, shop.getFinalAmount());
            ps.setDouble(4, shop.getShopDiscount());
            ps.setDouble(5, shop.getShippingFee());
            if (shop.getVoucherShopId() != null) {
                ps.setLong(6, shop.getVoucherShopId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.setLong(7, shop.getOrderShopId());

            return ps.executeUpdate() > 0;
        }
    }

    public List<OrderShop> getAllShopsByOrderShopId(Connection conn, long orderShopId) throws SQLException {
        String sql = """
                SELECT
                    OrderShopID, ShopID, Subtotal, ShippingFee,
                    ShopDiscount, FinalAmount, Status, VoucherShipID
                FROM OrderShops
                WHERE OrderShopID = ?
                """;

        List<OrderShop> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderShopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShop s = new OrderShop();
                    s.setOrderShopId(rs.getLong("OrderShopID"));
                    s.setShopId(rs.getLong("ShopID"));
                    s.setSubtotal(rs.getDouble("Subtotal"));
                    s.setShippingFee(rs.getDouble("ShippingFee"));
                    s.setShopDiscount(rs.getDouble("ShopDiscount"));
                    s.setFinalAmount(rs.getDouble("FinalAmount"));
                    s.setStatus(rs.getString("Status"));
                    s.setVoucherShipId(rs.getObject("VoucherShipID") != null ? rs.getLong("VoucherShipID") : null);
                    list.add(s);
                }
            }
        }
        return list;
    }
}
