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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.catalog.model.Product;
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

    public List<Order> getOrdersByUserId(long userId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String sql = """
                    SELECT
                        o.OrderID,
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

    public List<OrderShop> getOrdersByShopId(long shopId) throws SQLException {
        String sql = """
                SELECT
                    os.OrderShopID, os.OrderID, os.ShopID, os.UpdateAt, os.Status AS ShopStatus, os.FinalAmount AS ShopFinalAmount, os.CreatedAt AS ShopCreatedAt,
                    o.OrderStatus AS OrderStatus, o.TotalAmount AS OrderTotal, u.FullName AS CustomerName,
                    oi.OrderItemID, oi.ProductID, oi.Quantity, oi.OriginalPrice, oi.SalePrice, oi.Subtotal, oi.VatRate,
                    p.Title, p.OriginalPrice AS ProductOriginalPrice, p.SalePrice AS ProductSalePrice, pi.Url AS PrimaryImageUrl
                FROM OrderShops os
                JOIN Orders o ON os.OrderID = o.OrderID
                JOIN Users u ON o.UserID = u.UserID
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
                        os.setOrderId(rs.getLong("OrderID"));
                        os.setShopId(rs.getLong("ShopID"));
                        os.setStatus(rs.getString("ShopStatus"));
                        os.setFinalAmount(rs.getDouble("ShopFinalAmount"));
                        os.setCreatedAt(rs.getTimestamp("ShopCreatedAt"));
                        os.setUpdateAt(rs.getTimestamp("UpdateAt"));

                        // Gán thông tin Order trực tiếp
                        os.setCustomerName(rs.getString("CustomerName"));
                        os.setOrderStatus(rs.getString("OrderStatus"));
                        os.setOrderTotal(rs.getDouble("OrderTotal"));

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
                        item.setSubtotal(rs.getDouble("Subtotal"));
                        item.setVatRate(rs.getDouble("VatRate"));

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

    public List<OrderShop> getOrdersByShopIdAndStatus(long shopId, String status) throws SQLException {
        String sql = """
                SELECT
                    os.OrderShopID, os.OrderID, os.ShopID, os.UpdateAt, os.Status AS ShopStatus, os.FinalAmount AS ShopFinalAmount, os.CreatedAt AS ShopCreatedAt,
                    os.Discount, o.OrderStatus AS OrderStatus, o.TotalAmount AS OrderTotal, u.FullName AS CustomerName,
                    oi.OrderItemID, oi.ProductID, oi.Quantity, oi.OriginalPrice, oi.SalePrice, oi.Subtotal, oi.VatRate,
                    p.Title, p.OriginalPrice AS ProductOriginalPrice, p.SalePrice AS ProductSalePrice, pi.Url AS PrimaryImageUrl,
                    vc.Code, vc.VoucherID
                FROM OrderShops os
                JOIN Orders o ON os.OrderID = o.OrderID
                JOIN Users u ON o.UserID = u.UserID
                LEFT JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                LEFT JOIN Products p ON oi.ProductID = p.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                LEFT JOIN Vouchers vc ON os.VoucherID = vc.VoucherID
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
                        os.setOrderId(rs.getLong("OrderID"));
                        os.setShopId(rs.getLong("ShopID"));
                        os.setStatus(rs.getString("ShopStatus"));
                        os.setFinalAmount(rs.getDouble("ShopFinalAmount"));
                        os.setCreatedAt(rs.getTimestamp("ShopCreatedAt"));
                        os.setDiscount(rs.getDouble("Discount"));
                        os.setVoucherCode(rs.getString("Code"));
                        os.setUpdateAt(rs.getTimestamp("UpdateAt"));
                        os.setVoucherId(rs.getLong("VoucherID"));

                        os.setCustomerName(rs.getString("CustomerName"));
                        os.setOrderStatus(rs.getString("OrderStatus"));
                        os.setOrderTotal(rs.getDouble("OrderTotal"));

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
                        item.setSubtotal(rs.getDouble("Subtotal"));
                        item.setVatRate(rs.getDouble("VatRate"));

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
                    os.OrderShopID, os.OrderID, os.ShopID, os.Status AS ShopStatus,
                    os.FinalAmount AS ShopFinalAmount, os.CreatedAt AS ShopCreatedAt,
                    os.ShippingFee, os.Discount, os.UpdateAt, os.CancelReason, os.ReturnReason,
                    o.TotalAmount AS OrderTotal, o.CreatedAt AS OrderCreatedAt,
                    o.OrderStatus AS OrderStatus, o.UserID,

                    u.FullName AS CustomerName, u.Email AS CustomerEmail, a.Phone AS CustomerPhone,

                    a.[Description], a.Ward, a.District, a.City,

                    oi.OrderItemID, oi.ProductID, oi.Quantity, oi.OriginalPrice, oi.SalePrice, oi.Subtotal,
                    p.Title AS ProductTitle, p.SalePrice AS ProductSalePrice, pi.Url AS ProductImage,
                    vc.Code
                FROM OrderShops os
                JOIN Orders o ON os.OrderID = o.OrderID
                JOIN Users u ON o.UserID = u.UserID
                LEFT JOIN Addresses a ON o.AddressID = a.AddressID
                LEFT JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                LEFT JOIN Products p ON oi.ProductID = p.ProductID
                LEFT JOIN Vouchers vc ON os.VoucherID = vc.VoucherID
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
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    if (orderShop == null) {
                        orderShop = new OrderShop();
                        orderShop.setOrderShopId(rs.getLong("OrderShopID"));
                        orderShop.setOrderId(rs.getLong("OrderID"));
                        orderShop.setShopId(rs.getLong("ShopID"));
                        orderShop.setStatus(rs.getString("ShopStatus"));
                        orderShop.setFinalAmount(rs.getDouble("ShopFinalAmount"));
                        orderShop.setCreatedAt(rs.getTimestamp("ShopCreatedAt"));
                        orderShop.setShippingFee(rs.getDouble("ShippingFee"));
                        orderShop.setDiscount(rs.getDouble("Discount"));
                        orderShop.setUpdateAt(rs.getTimestamp("UpdateAt"));
                        orderShop.setVoucherCode(rs.getString("Code"));
                        orderShop.setCancelReason(rs.getString("CancelReason"));
                        orderShop.setReturnReason(rs.getString("ReturnReason"));

                        orderShop.setCustomerName(rs.getString("CustomerName"));
                        orderShop.setOrderTotal(rs.getDouble("OrderTotal"));
                        orderShop.setOrderStatus(rs.getString("OrderStatus"));

                        User customer = new User();
                        customer.setFullName(rs.getString("CustomerName"));
                        customer.setEmail(rs.getString("CustomerEmail"));
                        customer.setPhone(rs.getString("CustomerPhone"));
                        orderShop.setUser(customer);

                        String address = Stream.of(
                                rs.getString("Description"),
                                rs.getString("Ward"),
                                rs.getString("District"),
                                rs.getString("City"))
                                .filter(Objects::nonNull)
                                .filter(s -> !s.isBlank())
                                .collect(Collectors.joining(", "));
                        orderShop.setShippingAddress(address);
                    }

                    long orderItemId = rs.getLong("OrderItemID");
                    if (!rs.wasNull()) {
                        OrderItem item = new OrderItem();
                        item.setOrderItemId(orderItemId);
                        item.setProductId(rs.getLong("ProductID"));
                        item.setQuantity(rs.getInt("Quantity"));
                        item.setOriginalPrice(rs.getDouble("OriginalPrice"));
                        item.setSalePrice(rs.getDouble("SalePrice"));
                        item.setSubtotal(rs.getDouble("Subtotal"));

                        Product p = new Product();
                        p.setTitle(rs.getString("ProductTitle"));
                        p.setSalePrice(rs.getDouble("ProductSalePrice"));
                        p.setPrimaryImageUrl(rs.getString("ProductImage"));
                        item.setProduct(p);

                        items.add(item);
                    }
                }
                if (!found) {
                    System.out.println("⚠️ Không có dữ liệu trả về cho OrderShop ID = " + orderShopId);
                }
                if (orderShop != null) {
                    orderShop.setItems(items);
                }

                return orderShop;
            }
        }
    }

    public Map<String, Integer> getOrderCountsByShopId(Long shopId) throws SQLException {
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

    public int countOrdersByShop(Long shopId) {
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

    public int cancelExpiredOrders() {
        String selectSql = """
                    SELECT os.OrderShopId, os.VoucherID
                    FROM OrderShops os
                    WHERE os.Status IN ('PENDING')
                      AND DATEDIFF(DAY, os.CreatedAt, DATEADD(HOUR, 7, SYSUTCDATETIME())) >= 3
                """;

        String cancelOrderSql = """
                    UPDATE OrderShops
                    SET Status = 'CANCELLED', UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME()), CancelReason = N'Hủy do quá hạng xác nhận đơn.'
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
                Long voucherId = rs.getLong("VoucherID");
                if (rs.wasNull())
                    voucherId = null;

                try (PreparedStatement psCancel = conn.prepareStatement(cancelOrderSql);
                        PreparedStatement psRestoreStock = conn.prepareStatement(restoreStockSql)) {

                    psCancel.setLong(1, orderShopId);
                    psCancel.executeUpdate();

                    psRestoreStock.setLong(1, orderShopId);
                    psRestoreStock.executeUpdate();

                    if (voucherId != null && voucherId > 0) {
                        try (PreparedStatement psRestoreVoucher = conn.prepareStatement(restoreVoucherSql)) {
                            psRestoreVoucher.setLong(1, voucherId);
                            psRestoreVoucher.executeUpdate();
                        }
                    }

                    cancelledCount++;
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cancelledCount;
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

    public boolean updateOrderShopStatusByBR(long orderShopId, String newStatus) {
        String selectSql = """
                    SELECT os.VoucherID
                    FROM OrderShops os
                    WHERE os.OrderShopId = ?
                """;

        String updateOrderSql = """
                    UPDATE OrderShops
                    SET Status = ?,
                        UpdateAt = DATEADD(HOUR, 7, SYSUTCDATETIME())
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

            Long voucherId = null;
            if (rs.next()) {
                voucherId = rs.getLong("VoucherID");
                if (rs.wasNull())
                    voucherId = null;
            }

            // 🔹 Cập nhật trạng thái đơn hàng
            try (PreparedStatement psUpdateOrder = conn.prepareStatement(updateOrderSql)) {
                psUpdateOrder.setString(1, newStatus);
                psUpdateOrder.setLong(2, orderShopId);
                psUpdateOrder.executeUpdate();
            }

            // 🔹 Hoàn lại số lượng sản phẩm
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

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int autoApproveReturnRequests() {
        String selectSql = """
                    SELECT OrderShopID, VoucherID
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
                Long voucherId = rs.getLong("VoucherID");
                if (rs.wasNull())
                    voucherId = null;

                // ✅ Cập nhật trạng thái
                try (PreparedStatement psUpdate = conn.prepareStatement(updateStatusSql)) {
                    psUpdate.setLong(1, orderShopId);
                    psUpdate.executeUpdate();
                }

                // ✅ Hoàn lại tồn kho (và phục hồi trạng thái sản phẩm nếu cần)
                try (PreparedStatement psRestoreStock = conn.prepareStatement(restoreStockSql)) {
                    psRestoreStock.setLong(1, orderShopId);
                    psRestoreStock.executeUpdate();
                }

                // ✅ Hoàn lại voucher (nếu có)
                if (voucherId != null && voucherId > 0) {
                    try (PreparedStatement psRestoreVoucher = conn.prepareStatement(restoreVoucherSql)) {
                        psRestoreVoucher.setLong(1, voucherId);
                        psRestoreVoucher.executeUpdate();
                    }
                }

                autoApprovedCount++;
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autoApprovedCount;
    }

    public Order getOrderById(long orderId) {
        String sql = """
                    SELECT
                        OrderID,
                        UserID,
                        AddressID,
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
                    order.setAddressId(rs.getLong("AddressID"));
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
                        AddressID,
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
                    order.setAddressId(rs.getLong("AddressID"));
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
