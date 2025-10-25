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
                    INSERT INTO Payments(OrderID, Amount, TransactionRef, status)
                    VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, payment.getOrderId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getTransactionRef());
            ps.setString(4, payment.getStatus());
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

    public boolean updatePaymentStatus(long orderId, String newStatus, String transactionNo) {
        String sql = "UPDATE Payments SET Status = ?, TransactionRef = ?, CreatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE OrderID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, transactionNo);
            ps.setLong(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Payment getPaymentByOrderId(Connection conn, long orderId) {
        String sql = "SELECT PaymentID, OrderID, Amount, RefundedAmount, TransactionRef, Status FROM Payments WHERE OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
                    payment.setOrderId(rs.getLong("OrderID"));
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

    public boolean partialRefund(Connection conn, long orderId, double refundAmount) throws SQLException {
        Payment payment = getPaymentByOrderId(conn, orderId);
        if (payment == null)
            return false;

        double newRefunded = payment.getRefundedAmount() + refundAmount;
        double remaining = payment.getAmount() - newRefunded;

        String status = remaining <= 0 ? "REFUNDED" : "PARTIALLY_REFUNDED";

        String sql = "UPDATE Payments SET RefundedAmount = ?, Status = ? WHERE OrderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newRefunded);
            ps.setString(2, status);
            ps.setLong(3, orderId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }
}
