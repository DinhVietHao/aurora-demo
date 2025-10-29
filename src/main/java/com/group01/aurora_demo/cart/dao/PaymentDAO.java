package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.group01.aurora_demo.cart.model.Payment;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class PaymentDAO {
    public long createPayment(Connection conn, Payment payment) throws SQLException {
        String sql = """
                    INSERT INTO Payments(OrderShopID, GroupOrderCode, Amount, TransactionRef, Status)
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, payment.getOrderShopId());
            ps.setString(2, payment.getGroupOrderCode());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getTransactionRef());
            ps.setString(5, payment.getStatus());
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

    public boolean updatePaymentStatus(String groupOrderCode, String newStatus, String transactionNo) {
        String sql = "UPDATE Payments SET Status = ?, TransactionRef = ?, CreatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE GroupOrderCode = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, transactionNo);
            ps.setString(3, groupOrderCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Payment getPaymentByOrderShopId(Connection conn, long orderShopId) {
        String sql = "SELECT PaymentID, OrderShopID, Amount, RefundedAmount, TransactionRef, Status FROM Payments WHERE OrderShopID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderShopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
                    payment.setOrderShopId(rs.getLong("OrderShopID"));
                    payment.setAmount(rs.getDouble("Amount"));
                    payment.setRefundedAmount(rs.getDouble("RefundedAmount"));
                    payment.setTransactionRef(rs.getString("TransactionRef"));
                    payment.setStatus(rs.getString("Status"));
                    return payment;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean refundShopPayment(Connection conn, long orderShopId, double refundAmount) throws SQLException {
        String sql = "UPDATE Payments SET RefundedAmount = ?, Status = 'REFUNDED' WHERE OrderShopID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, refundAmount);
            ps.setLong(2, orderShopId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }
}
