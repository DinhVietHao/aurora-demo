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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.dto.OrderShopDTO;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.shop.dao.UserVoucherDAO;
import com.group01.aurora_demo.shop.dao.VoucherDAO;

public class OrderShopDAO {
    private VoucherDAO voucherDAO;
    private OrderItemDAO orderItemDAO;
    private ProductDAO productDAO;
    private UserVoucherDAO userVoucherDAO;
    private PaymentDAO paymentDAO;

    public OrderShopDAO() {
        this.voucherDAO = new VoucherDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.productDAO = new ProductDAO();
        this.userVoucherDAO = new UserVoucherDAO();
        this.paymentDAO = new PaymentDAO();

    }

    public List<OrderShopDTO> getOrderShopsByPaymentId(long paymentId) {
        List<OrderShopDTO> orderShops = new ArrayList<>();
        String sql = """
                    SELECT
                        os.OrderShopID,
                        os.Subtotal,
                        os.ShippingFee,
                        os.ShopDiscount,
                        os.SystemShippingDiscount,
                        os.SystemDiscount,
                        p.ProductID,
                        p.Title AS ProductName,
                        img.Url AS ImageUrl,
                        oi.Quantity,
                        oi.OriginalPrice,
                        oi.SalePrice,
                        os.CreatedAt
                    FROM OrderShops os
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    JOIN Products p ON oi.ProductID = p.ProductID
                    JOIN ProductImages img ON p.ProductID = img.ProductID
                    WHERE os.PaymentID = ?
                      AND img.IsPrimary = 1
                    ORDER BY os.OrderShopID;
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, paymentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShopDTO orderShop = new OrderShopDTO();
                    orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                    orderShop.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    orderShop.setSubtotal(rs.getDouble("Subtotal"));
                    // --- Shop info ---
                    orderShop.setShopShippingFee(rs.getDouble("ShippingFee"));
                    orderShop.setShopDiscount(rs.getDouble("ShopDiscount"));
                    orderShop.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                    orderShop.setSystemDiscount(rs.getDouble("SystemDiscount"));

                    // --- Product info ---
                    orderShop.setProductId(rs.getLong("ProductID"));
                    orderShop.setProductName(rs.getString("ProductName"));
                    orderShop.setImageUrl(rs.getString("ImageUrl"));
                    orderShop.setQuantity(rs.getInt("Quantity"));
                    orderShop.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    orderShop.setSalePrice(rs.getDouble("SalePrice"));

                    orderShops.add(orderShop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderShops;
    }

    public List<OrderShopDTO> getOrderShopsByStatus(long userId, String status) {
        List<OrderShopDTO> orderShops = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT
                    os.OrderShopID,
                    os.PaymentID,
                    s.Name AS ShopName,
                    s.ShopId,
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
                WHERE os.UserID = ?
                  AND img.IsPrimary = 1
                """);
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            if (status.equalsIgnoreCase("waiting_ship")) {
                sql.append(" AND (os.Status = 'WAITING_SHIP' OR os.Status = 'CONFIRM') ");
            } else if (status.equalsIgnoreCase("returned")) {
                sql.append(
                        " AND (os.Status = 'RETURNED_REQUESTED' OR os.Status = 'RETURNED' OR os.Status = 'RETURNED_REJECTED') ");
            } else {
                sql.append(" AND os.Status = ? ");
            }
        }
        sql.append(" ORDER BY os.UpdatedAt DESC");
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            ps.setLong(1, userId);
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
                if (!status.equalsIgnoreCase("waiting_ship") && !status.equalsIgnoreCase("returned")) {
                    ps.setString(2, status.toUpperCase());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderShopDTO orderShop = new OrderShopDTO();
                    orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                    orderShop.setPaymentId(rs.getLong("PaymentID"));
                    orderShop.setShopName(rs.getString("ShopName"));
                    orderShop.setShopId(rs.getLong("ShopId"));
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
                        UserID, ShopID, PaymentID, Address,
                        VoucherShopID, VoucherDiscountID, VoucherShipID,
                        Subtotal, ShopDiscount, SystemDiscount,
                        ShippingFee, SystemShippingDiscount, FinalAmount,
                        Status, CreatedAt, UpdatedAt
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DATEADD(HOUR, 7, SYSUTCDATETIME()), DATEADD(HOUR, 7, SYSUTCDATETIME()))
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orderShop.getUserId());
            ps.setLong(2, orderShop.getShopId());
            ps.setLong(3, orderShop.getPaymentId());
            ps.setString(4, orderShop.getAddress());
            if (orderShop.getVoucherShopId() != null)
                ps.setLong(5, orderShop.getVoucherShopId());
            else
                ps.setNull(5, Types.BIGINT);
            if (orderShop.getVoucherDiscountId() != null)
                ps.setLong(6, orderShop.getVoucherDiscountId());
            else
                ps.setNull(6, Types.BIGINT);
            if (orderShop.getVoucherShipId() != null)
                ps.setLong(7, orderShop.getVoucherShipId());
            else
                ps.setNull(7, Types.BIGINT);
            ps.setDouble(8, orderShop.getSubtotal());
            ps.setDouble(9, orderShop.getShopDiscount());
            ps.setDouble(10, orderShop.getSystemDiscount());
            ps.setDouble(11, orderShop.getShippingFee());
            ps.setDouble(12, orderShop.getSystemShippingDiscount());
            ps.setDouble(13, orderShop.getFinalAmount());
            ps.setString(14, orderShop.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
            }

        }
        return -1;
    }

    public boolean updateOrderShopStatusByPaymentId(long paymentId, String newStatus) {
        String sql = "UPDATE OrderShops SET Status = ?, UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE PaymentID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, paymentId);
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

    public boolean cancelOrderShop(Connection conn, long orderShopId, String reason) {
        String sql = """
                    UPDATE OrderShops
                    SET Status = 'CANCELLED',
                        CancelReason = ?,
                        UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                    WHERE OrderShopID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public int autoCancelPendingPayments() {
        String sql = """
                    SELECT PaymentID, TransactionRef
                    FROM Payments
                    WHERE Status = 'PENDING_PAYMENT' AND DATEDIFF(HOUR, UpdatedAt, GETDATE()) >= 1;
                """;
        int cancelledCount = 0;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long paymentId = rs.getLong("PaymentID");
                String transactionRef = rs.getString("TransactionRef");

                boolean rolledBack = rollbackFailedPaymentByPaymentId(paymentId);

                if (rolledBack) {
                    this.paymentDAO.updatePaymentStatusById(paymentId, "FAILED", transactionRef);
                    cancelledCount++;
                    System.out.println("Tự động hủy Payment #" + paymentId + " do quá hạn thanh toán.");
                } else {
                    System.err.println("Không thể rollback Payment #" + paymentId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cancelledCount;
    }

    public boolean rollbackFailedPaymentByPaymentId(long paymentId) {
        String sql = "SELECT OrderShopID FROM OrderShops WHERE PaymentID = ? AND Status = 'PENDING_PAYMENT'";
        boolean success = false;

        try (Connection conn = DataSourceProvider.get().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, paymentId);
                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        Long orderShopId = rs.getLong("OrderShopID");

                        OrderShop orderShop = findByOrderShopId(conn, orderShopId);
                        if (orderShop == null)
                            continue;

                        boolean cancelled = cancelOrderShop(conn, orderShopId, "Thanh toán thất bại");
                        if (!cancelled)
                            continue;

                        if (orderShop.getVoucherShopId() != null) {
                            userVoucherDAO.restoreUserVoucher(conn, orderShop.getVoucherShopId(),
                                    orderShop.getUserId());
                            voucherDAO.decreaseUsageCount(conn, orderShop.getVoucherShopId());
                        }

                        List<OrderItem> items = orderItemDAO.getItemsByOrderShopId(conn, orderShopId);
                        for (OrderItem item : items) {
                            productDAO.restoreStock(conn, item.getProductId(), item.getQuantity());
                        }

                        List<OrderShop> activeShop = getActiveShopsByPaymentId(conn,
                                orderShop.getPaymentId());
                        if (activeShop.isEmpty()) {

                            if (orderShop.getVoucherDiscountId() != null) {
                                userVoucherDAO.restoreUserVoucher(conn, orderShop.getVoucherDiscountId(),
                                        orderShop.getUserId());
                                voucherDAO.decreaseUsageCount(conn, orderShop.getVoucherDiscountId());
                            }
                            if (orderShop.getVoucherShipId() != null) {
                                userVoucherDAO.restoreUserVoucher(conn, orderShop.getVoucherShipId(),
                                        orderShop.getUserId());
                                voucherDAO.decreaseUsageCount(conn, orderShop.getVoucherShipId());
                            }
                        }
                    }
                    conn.commit();
                    success = true;

                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public OrderShop findByOrderShopId(Connection conn, long orderShopId) throws SQLException {
        String sql = """
                SELECT
                    OrderShopID,
                    ShopID,
                    PaymentID,
                    Status,
                    Subtotal,
                    ShippingFee,
                    ShopDiscount,
                    FinalAmount,
                    VoucherShopID,
                    VoucherDiscountID,
                    VoucherShipID,
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
                    shop.setPaymentId(rs.getLong("PaymentID"));
                    shop.setStatus(rs.getString("Status"));
                    shop.setSubtotal(rs.getDouble("Subtotal"));
                    shop.setShippingFee(rs.getDouble("ShippingFee"));
                    shop.setShopDiscount(rs.getDouble("ShopDiscount"));
                    shop.setFinalAmount(rs.getDouble("FinalAmount"));
                    shop.setVoucherShopId(rs.getObject("VoucherShopID") != null ? rs.getLong("VoucherShopID") : null);
                    shop.setVoucherDiscountId(
                            rs.getObject("VoucherDiscountID") != null ? rs.getLong("VoucherDiscountID") : null);
                    shop.setVoucherShipId(rs.getObject("VoucherShipID") != null ? rs.getLong("VoucherShipID") : null);
                    shop.setCancelReason(rs.getString("CancelReason"));
                    shop.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    return shop;
                }
            }
        }
        return null;
    }

    public List<OrderShop> getActiveShopsByPaymentId(Connection conn, long paymentId) throws SQLException {
        String sql = """
                 SELECT
                    OrderShopID, ShopID, Subtotal, ShippingFee,
                    ShopDiscount, FinalAmount, Status, VoucherShopID
                FROM OrderShops
                WHERE PaymentID = ? AND Status NOT IN ('CANCELLED', 'RETURNED', 'RETURNED_REJECTED')
                """;

        List<OrderShop> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
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

    public List<OrderShop> getOrderShopByShopId(long shopId) throws SQLException {
        String sql = """
                SELECT
                    os.OrderShopID,
                    os.UserID,
                    os.ShopID,
                    os.Status AS ShopStatus,
                    os.FinalAmount,
                    os.Subtotal,
                    os.ShopDiscount,
                    os.SystemDiscount,
                    os.ShippingFee,
                    os.SystemShippingDiscount,
                    os.CreatedAt,
                    os.UpdatedAt,
                    u.FullName AS CustomerName,
                    oi.OrderItemID,
                    oi.ProductID,
                    oi.Quantity,
                    oi.OriginalPrice AS ItemOriginalPrice,
                    oi.SalePrice AS ItemSalePrice,
                    oi.Subtotal AS ItemSubtotal,
                    oi.VATRate,
                    p.Title,
                    p.OriginalPrice AS ProductOriginalPrice,
                    p.SalePrice AS ProductSalePrice,
                    pi.Url AS PrimaryImageUrl
                FROM OrderShops os
                JOIN Users u ON os.UserID = u.UserID
                LEFT JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                LEFT JOIN Products p ON oi.ProductID = p.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                WHERE os.ShopID = ? AND os.Status <> 'PENDING_PAYMENT'
                ORDER BY os.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                Map<Long, OrderShop> orderShopMap = new LinkedHashMap<>();

                while (rs.next()) {
                    long orderShopId = rs.getLong("OrderShopID");
                    OrderShop os = orderShopMap.get(orderShopId);
                    if (os == null) {
                        os = new OrderShop();
                        os.setOrderShopId(orderShopId);
                        os.setUserId(rs.getLong("UserID"));
                        os.setShopId(rs.getLong("ShopID"));
                        os.setStatus(rs.getString("ShopStatus"));
                        os.setFinalAmount(rs.getDouble("FinalAmount"));
                        os.setSubtotal(rs.getDouble("Subtotal"));
                        os.setShopDiscount(rs.getDouble("ShopDiscount"));
                        os.setSystemDiscount(rs.getDouble("SystemDiscount"));
                        os.setShippingFee(rs.getDouble("ShippingFee"));
                        os.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                        os.setCreatedAt(rs.getTimestamp("CreatedAt"));
                        os.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                        os.setItems(new ArrayList<>());

                        User user = new User();
                        user.setFullName(rs.getString("CustomerName"));
                        os.setUser(user);

                        orderShopMap.put(orderShopId, os);
                    }

                    long orderItemId = rs.getLong("OrderItemID");
                    if (!rs.wasNull()) {
                        OrderItem item = new OrderItem();
                        item.setOrderItemId(orderItemId);
                        item.setOrderShopId(orderShopId);
                        item.setProductId(rs.getLong("ProductID"));
                        item.setQuantity(rs.getInt("Quantity"));
                        item.setOriginalPrice(rs.getDouble("ItemOriginalPrice"));
                        item.setSalePrice(rs.getDouble("ItemSalePrice"));
                        item.setSubtotal(rs.getDouble("ItemSubtotal"));
                        item.setVatRate(rs.getDouble("VATRate"));

                        // Gán thông tin sản phẩm
                        Product product = new Product();
                        product.setProductId(rs.getLong("ProductID"));
                        product.setTitle(rs.getString("Title"));
                        product.setOriginalPrice(rs.getDouble("ProductOriginalPrice"));
                        product.setSalePrice(rs.getDouble("ProductSalePrice"));
                        product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                        item.setProduct(product);

                        os.getItems().add(item);
                    }
                }

                return new ArrayList<>(orderShopMap.values());
            }
        }
    }

    public OrderShop getOrderShopDetail(Long orderShopId) throws Exception {
        String sql = """
                SELECT
                    os.OrderShopID,
                    os.UserID,
                    os.ShopID,
                    os.Address,
                    os.VoucherShopID,
                    os.VoucherDiscountID,
                    os.VoucherShipID,
                    os.Subtotal,
                    os.ShopDiscount,
                    os.SystemDiscount,
                    os.ShippingFee,
                    os.SystemShippingDiscount,
                    os.FinalAmount,
                    os.Status,
                    os.CreatedAt,
                    os.UpdatedAt,
                    os.CancelReason,
                    os.ReturnReason,
                    u.FullName AS CustomerName,
                    u.Email AS CustomerEmail,
                    oi.OrderItemID,
                    oi.ProductID,
                    oi.Quantity,
                    oi.OriginalPrice,
                    oi.SalePrice,
                    oi.Subtotal AS ItemSubtotal,
                    oi.VatRate,
                    p.Title AS ProductTitle,
                    p.SalePrice AS ProductSalePrice,
                    pi.Url AS ProductImage
                FROM OrderShops os
                JOIN Users u ON os.UserID = u.UserID
                LEFT JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                LEFT JOIN Products p ON oi.ProductID = p.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                WHERE os.OrderShopID = ?
                ORDER BY oi.OrderItemID
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, orderShopId);
            try (ResultSet rs = ps.executeQuery()) {

                OrderShop orderShop = null;
                List<OrderItem> items = new ArrayList<>();

                while (rs.next()) {
                    if (orderShop == null) {
                        orderShop = new OrderShop();
                        orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                        orderShop.setUserId(rs.getLong("UserID"));
                        orderShop.setShopId(rs.getLong("ShopID"));
                        orderShop.setAddress(rs.getString("Address"));
                        orderShop.setVoucherShopId(rs.getObject("VoucherShopID", Long.class));
                        orderShop.setVoucherDiscountId(rs.getObject("VoucherDiscountID", Long.class));
                        orderShop.setVoucherShipId(rs.getObject("VoucherShipID", Long.class));
                        orderShop.setSubtotal(rs.getDouble("Subtotal"));
                        orderShop.setShopDiscount(rs.getDouble("ShopDiscount"));
                        orderShop.setSystemDiscount(rs.getDouble("SystemDiscount"));
                        orderShop.setShippingFee(rs.getDouble("ShippingFee"));
                        orderShop.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                        orderShop.setFinalAmount(rs.getDouble("FinalAmount"));
                        orderShop.setStatus(rs.getString("Status"));
                        orderShop.setCreatedAt(rs.getTimestamp("CreatedAt"));
                        orderShop.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                        orderShop.setCancelReason(rs.getString("CancelReason"));
                        orderShop.setReturnReason(rs.getString("ReturnReason"));

                        // User info
                        User user = new User();
                        user.setFullName(rs.getString("CustomerName"));
                        user.setEmail(rs.getString("CustomerEmail"));
                        orderShop.setUser(user);
                    }

                    long orderItemId = rs.getLong("OrderItemID");
                    if (!rs.wasNull()) {
                        OrderItem item = new OrderItem();
                        item.setOrderItemId(orderItemId);
                        item.setOrderShopId(orderShopId);
                        item.setProductId(rs.getLong("ProductID"));
                        item.setQuantity(rs.getInt("Quantity"));
                        item.setOriginalPrice(rs.getDouble("OriginalPrice"));
                        item.setSalePrice(rs.getDouble("SalePrice"));
                        item.setSubtotal(rs.getDouble("ItemSubtotal"));
                        item.setVatRate(rs.getDouble("VatRate"));

                        Product product = new Product();
                        product.setTitle(rs.getString("ProductTitle"));
                        product.setSalePrice(rs.getDouble("ProductSalePrice"));
                        product.setPrimaryImageUrl(rs.getString("ProductImage"));
                        item.setProduct(product);

                        items.add(item);
                    }
                }

                if (orderShop != null) {
                    orderShop.setItems(items);
                } else {
                    System.out.println("Không tìm thấy dữ liệu cho OrderShopID = " + orderShopId);
                }

                return orderShop;
            }
        }
    }

    public Map<String, Integer> getOrderShopCountsByShopId(Long shopId) throws SQLException {
        String sql = """
                    SELECT
                        CASE
                            WHEN Status LIKE 'RETURNED%' THEN 'RETURNED_GROUP'
                            ELSE Status
                        END AS StatusGroup,
                        COUNT(*) AS Count
                    FROM OrderShops
                    WHERE ShopID = ?
                    GROUP BY
                        CASE
                            WHEN Status LIKE 'RETURNED%' THEN 'RETURNED_GROUP'
                            ELSE Status
                        END
                """;

        Map<String, Integer> counts = new HashMap<>();
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("StatusGroup"), rs.getInt("Count"));
            }
        }
        return counts;
    }

    public int countOrderShopByShop(Long shopId) {
        String sql = "SELECT COUNT(*) FROM OrderShops WHERE ShopID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<OrderShop> getOrderShopByShopIdAndStatus(long shopId, String status) throws SQLException {
        String sql = """
                SELECT
                    os.OrderShopID,
                    os.ShopID,
                    os.UserID,
                    os.Status AS ShopStatus,
                    os.Subtotal,
                    os.ShopDiscount,
                    os.SystemDiscount,
                    os.ShippingFee,
                    os.SystemShippingDiscount,
                    os.FinalAmount,
                    os.CreatedAt,
                    os.UpdatedAt,
                    os.CancelReason,
                    os.ReturnReason,

                    u.FullName AS CustomerName,
                    u.Email AS CustomerEmail,

                    oi.OrderItemID,
                    oi.ProductID,
                    oi.Quantity,
                    oi.OriginalPrice,
                    oi.SalePrice,
                    oi.Subtotal AS ItemSubtotal,
                    oi.VatRate,

                    p.Title AS ProductTitle,
                    p.OriginalPrice AS ProductOriginalPrice,
                    p.SalePrice AS ProductSalePrice,
                    pi.Url AS PrimaryImageUrl

                FROM OrderShops os
                JOIN Users u ON os.UserID = u.UserID
                LEFT JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                LEFT JOIN Products p ON oi.ProductID = p.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                WHERE os.ShopID = ? AND os.Status LIKE ? AND os.Status <> 'PENDING_PAYMENT'
                ORDER BY os.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);
            ps.setString(2, status + "%");

            try (ResultSet rs = ps.executeQuery()) {
                Map<Long, OrderShop> orderShopMap = new LinkedHashMap<>();

                while (rs.next()) {
                    long orderShopId = rs.getLong("OrderShopID");

                    OrderShop os = orderShopMap.get(orderShopId);
                    if (os == null) {
                        os = new OrderShop();
                        os.setOrderShopId(orderShopId);
                        os.setShopId(rs.getLong("ShopID"));
                        os.setUserId(rs.getLong("UserID"));
                        os.setStatus(rs.getString("ShopStatus"));
                        os.setSubtotal(rs.getDouble("Subtotal"));
                        os.setShopDiscount(rs.getDouble("ShopDiscount"));
                        os.setSystemDiscount(rs.getDouble("SystemDiscount"));
                        os.setShippingFee(rs.getDouble("ShippingFee"));
                        os.setSystemShippingDiscount(rs.getDouble("SystemShippingDiscount"));
                        os.setFinalAmount(rs.getDouble("FinalAmount"));
                        os.setCreatedAt(rs.getTimestamp("CreatedAt"));
                        os.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                        os.setCancelReason(rs.getString("CancelReason"));
                        os.setReturnReason(rs.getString("ReturnReason"));

                        User user = new User();
                        user.setFullName(rs.getString("CustomerName"));
                        user.setEmail(rs.getString("CustomerEmail"));
                        os.setUser(user);

                        os.setItems(new ArrayList<>());
                        orderShopMap.put(orderShopId, os);
                    }

                    long orderItemId = rs.getLong("OrderItemID");
                    if (!rs.wasNull()) {
                        OrderItem item = new OrderItem();
                        item.setOrderItemId(orderItemId);
                        item.setOrderShopId(orderShopId);
                        item.setProductId(rs.getLong("ProductID"));
                        item.setQuantity(rs.getInt("Quantity"));
                        item.setOriginalPrice(rs.getDouble("OriginalPrice"));
                        item.setSalePrice(rs.getDouble("SalePrice"));
                        item.setSubtotal(rs.getDouble("ItemSubtotal"));
                        item.setVatRate(rs.getDouble("VatRate"));

                        Product product = new Product();
                        product.setProductId(rs.getLong("ProductID"));
                        product.setTitle(rs.getString("ProductTitle"));
                        product.setOriginalPrice(rs.getDouble("ProductOriginalPrice"));
                        product.setSalePrice(rs.getDouble("ProductSalePrice"));
                        product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));
                        item.setProduct(product);

                        os.getItems().add(item);
                    }
                }
                return new ArrayList<>(orderShopMap.values());
            }
        }
    }

    public boolean updateOrderShopStatusByBR(long orderShopId, String newStatus) {
        String selectSql = """
                SELECT
                    os.VoucherShopId,
                    os.VoucherDiscountId,
                    os.VoucherShipId,
                    os.FinalAmount
                FROM OrderShops os
                WHERE os.OrderShopId = ?
                """;

        String updateOrderSql = """
                UPDATE OrderShops
                SET Status = ?,
                    UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                WHERE OrderShopId = ?
                """;

        String restoreStockSql = """
                UPDATE p
                SET p.Quantity = p.Quantity + oi.Quantity,
                    p.Status = CASE
                                   WHEN (p.Quantity = 0 OR p.Status = 'OUT_OF_STOCK')
                                        AND (p.Quantity + oi.Quantity) > 0 THEN 'ACTIVE'
                                   ELSE p.Status
                               END
                FROM Products p
                JOIN OrderItems oi ON p.ProductID = oi.ProductID
                WHERE oi.OrderShopId = ?
                """;

        String restoreVoucherSql = """
                UPDATE Vouchers
                SET UsageCount = CASE WHEN UsageCount > 0 THEN UsageCount - 1 ELSE 0 END
                WHERE VoucherID = ?
                """;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement psSelect = conn.prepareStatement(selectSql)) {

            conn.setAutoCommit(false);

            // 🔹 Lấy thông tin voucher (nếu có)
            psSelect.setLong(1, orderShopId);
            ResultSet rs = psSelect.executeQuery();

            Long voucherShopId = null;
            Long voucherDiscountId = null;
            Long voucherShipId = null;
            double shopFinalAmount = 0;

            if (rs.next()) {
                voucherShopId = rs.getLong("VoucherShopId");
                voucherDiscountId = rs.getLong("VoucherDiscountId");
                voucherShipId = rs.getLong("VoucherShipId");
                shopFinalAmount = rs.getDouble("FinalAmount");
            }

            // 🔹 Cập nhật trạng thái đơn hàng
            try (PreparedStatement psUpdateOrder = conn.prepareStatement(updateOrderSql)) {
                psUpdateOrder.setString(1, newStatus);
                psUpdateOrder.setLong(2, orderShopId);
                psUpdateOrder.executeUpdate();
            }

            // 🔹 Hoàn lại hàng tồn kho
            try (PreparedStatement psRestoreStock = conn.prepareStatement(restoreStockSql)) {
                psRestoreStock.setLong(1, orderShopId);
                psRestoreStock.executeUpdate();
            }

            // 🔹 Hoàn lại voucher (nếu có)
            try (PreparedStatement psRestoreVoucher = conn.prepareStatement(restoreVoucherSql)) {
                if (voucherShopId != null && voucherShopId > 0) {
                    psRestoreVoucher.setLong(1, voucherShopId);
                    psRestoreVoucher.executeUpdate();
                }
            }

            PaymentDAO paymentDAO = new PaymentDAO();
            boolean refunded = paymentDAO.partialRefund(conn, orderShopId,
                    shopFinalAmount);
            if (!refunded) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int cancelExpiredOrders() {
        String selectSql = """
                SELECT os.OrderShopId, os.PaymentID, os.VoucherShopID, os.FinalAmount
                FROM OrderShops os
                WHERE os.Status = 'PENDING'
                AND DATEDIFF(DAY, os.CreatedAt, DATEADD(HOUR, 7, SYSUTCDATETIME())) >= 3
                        """;

        String cancelOrderSql = """
                UPDATE OrderShops
                SET Status = 'CANCELLED',
                    UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME()),
                    CancelReason = N'Hủy do quá hạn xác nhận đơn.'
                WHERE OrderShopId = ?
                """;

        String restoreStockSql = """
                UPDATE p
                SET p.Quantity = p.Quantity + oi.Quantity,
                    p.Status = CASE
                                   WHEN (p.Quantity = 0 OR p.Status = 'OUT_OF_STOCK')
                                        AND (p.Quantity + oi.Quantity) > 0 THEN 'ACTIVE'
                                   ELSE p.Status
                               END
                FROM Products p
                JOIN OrderItems oi ON p.ProductID = oi.ProductID
                WHERE oi.OrderShopId = ?
                """;

        String restoreVoucherSql = """
                UPDATE Vouchers
                SET UsageCount = CASE WHEN UsageCount > 0 THEN UsageCount - 1 ELSE 0 END
                WHERE VoucherID = ?
                """;

        int cancelledCount = 0;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql);
                ResultSet rs = ps.executeQuery()) {

            conn.setAutoCommit(false);

            while (rs.next()) {
                long orderShopId = rs.getLong("OrderShopId");
                long paymentId = rs.getLong("PaymentID");
                double shopFinalAmount = rs.getDouble("FinalAmount");
                Long voucherId = rs.getLong("VoucherID");
                if (rs.wasNull())
                    voucherId = null;

                // 🔹 Cập nhật trạng thái đơn
                try (PreparedStatement psCancel = conn.prepareStatement(cancelOrderSql)) {
                    psCancel.setLong(1, orderShopId);
                    psCancel.executeUpdate();
                }

                // 🔹 Khôi phục tồn kho
                try (PreparedStatement psRestoreStock = conn.prepareStatement(restoreStockSql)) {
                    psRestoreStock.setLong(1, orderShopId);
                    psRestoreStock.executeUpdate();
                }

                // 🔹 Hoàn lại voucher (nếu có)
                if (voucherId != null && voucherId > 0) {
                    try (PreparedStatement psRestoreVoucher = conn.prepareStatement(restoreVoucherSql)) {
                        psRestoreVoucher.setLong(1, voucherId);
                        psRestoreVoucher.executeUpdate();
                    }
                }

                // 🔹 Gọi hoàn tiền (nếu PaymentDAO hỗ trợ theo OrderShopId)
                PaymentDAO paymentDAO = new PaymentDAO();
                boolean refunded = paymentDAO.partialRefund(conn, paymentId, shopFinalAmount);

                if (!refunded) {
                    System.err.println("Partial refund failed for OrderShopID=" + orderShopId);
                }

                cancelledCount++;
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cancelledCount;
    }

    public int autoApproveReturnRequests() {
        String selectSql = """
                SELECT OrderShopID, VoucherShopID, FinalAmount
                FROM OrderShops
                WHERE Status = 'RETURNED_REQUESTED'
                  AND DATEDIFF(DAY, CreatedAt, DATEADD(HOUR, 7, SYSUTCDATETIME())) >= 3
                """;

        String updateStatusSql = """
                UPDATE OrderShops
                SET Status = 'RETURNED',
                    UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
                WHERE OrderShopID = ?
                """;

        String restoreStockSql = """
                UPDATE p
                SET p.Quantity = p.Quantity + oi.Quantity,
                    p.Status = CASE
                                   WHEN (p.Quantity = 0 OR p.Status = 'OUT_OF_STOCK')
                                        AND (p.Quantity + oi.Quantity) > 0 THEN 'ACTIVE'
                                   ELSE p.Status
                               END
                FROM Products p
                JOIN OrderItems oi ON p.ProductID = oi.ProductID
                WHERE oi.OrderShopId = ?
                """;

        String restoreVoucherSql = """
                UPDATE Vouchers
                SET UsageCount = CASE WHEN UsageCount > 0 THEN UsageCount - 1 ELSE 0 END
                WHERE VoucherID = ?
                """;

        int autoApprovedCount = 0;

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement psSelect = conn.prepareStatement(selectSql);
                ResultSet rs = psSelect.executeQuery()) {

            conn.setAutoCommit(false);

            while (rs.next()) {
                long orderShopId = rs.getLong("OrderShopID");
                long paymentId = rs.getLong("PaymentID");
                double shopFinalAmount = rs.getDouble("FinalAmount");
                Long voucherId = rs.getLong("VoucherID");
                if (rs.wasNull())
                    voucherId = null;

                try {
                    // ✅ 1. Cập nhật trạng thái đơn
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateStatusSql)) {
                        psUpdate.setLong(1, orderShopId);
                        psUpdate.executeUpdate();
                    }

                    // ✅ 2. Hoàn lại tồn kho
                    try (PreparedStatement psRestoreStock = conn.prepareStatement(restoreStockSql)) {
                        psRestoreStock.setLong(1, orderShopId);
                        psRestoreStock.executeUpdate();
                    }

                    // ✅ 3. Khôi phục voucher (nếu có)
                    if (voucherId != null && voucherId > 0) {
                        try (PreparedStatement psRestoreVoucher = conn.prepareStatement(restoreVoucherSql)) {
                            psRestoreVoucher.setLong(1, voucherId);
                            psRestoreVoucher.executeUpdate();
                        }
                    }

                    // ✅ 4. Hoàn tiền lại cho khách (nếu có PaymentDAO)
                    PaymentDAO paymentDAO = new PaymentDAO();
                    boolean refunded = paymentDAO.partialRefund(conn, paymentId, shopFinalAmount);
                    if (!refunded) {
                        System.err.println("Refund failed for OrderShopID=" + orderShopId);
                        conn.rollback();
                        continue;
                    }

                    autoApprovedCount++;

                } catch (SQLException e) {
                    e.printStackTrace();
                    conn.rollback();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autoApprovedCount;
    }

}
