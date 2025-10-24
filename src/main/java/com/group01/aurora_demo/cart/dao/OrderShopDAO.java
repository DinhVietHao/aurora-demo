package com.group01.aurora_demo.cart.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class OrderShopDAO {
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
                    SET Status = 'RETURN_REQUESTED',
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
