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
                    INSERT INTO Payments(Amount, TransactionRef, Status, createdAt, UpdatedAt)
                    VALUES (?, ?, ?, DATEADD(HOUR, 7, SYSUTCDATETIME()), DATEADD(HOUR, 7, SYSUTCDATETIME()))
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, payment.getAmount());
            ps.setString(2, payment.getTransactionRef());
            ps.setString(3, payment.getStatus());
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

    public boolean updatePaymentStatusById(long paymentId, String newStatus, String transactionNo) {
        String sql = "UPDATE Payments SET Status = ?, TransactionRef = ?, UpdatedAt = DATEADD(HOUR, 7, SYSUTCDATETIME()) WHERE PaymentID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, transactionNo);
            ps.setLong(3, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Payment getPaymentById(Connection conn, long paymentId) {
        String sql = "SELECT PaymentID, Amount, RefundedAmount, TransactionRef, Status FROM Payments WHERE PaymentID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, paymentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
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

    public Payment getPaymentById(long paymentId) {
        String sql = "SELECT PaymentID, Amount, RefundedAmount, TransactionRef, Status FROM Payments WHERE PaymentID = ?";

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, paymentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
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

    public boolean partialRefund(Connection conn, long paymentId, double refundAmount) throws SQLException {
        Payment payment = getPaymentById(conn, paymentId);
        if (payment == null)
            return false;
        if (refundAmount <= 0)
            return false;

        double newRefunded = payment.getRefundedAmount() + refundAmount;
        if (newRefunded > payment.getAmount()) {
            return false;
        }
        double remaining = payment.getAmount() - newRefunded;

        String status = remaining <= 0 ? "REFUNDED" : "PARTIALLY_REFUNDED";

        String sql = "UPDATE Payments SET RefundedAmount = ?, Status = ? WHERE PaymentID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newRefunded);
            ps.setString(2, status);
            ps.setLong(3, paymentId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    public Payment findByTransactionRef(String transactionRef) {
        String sql = "SELECT PaymentID, Amount, RefundedAmount, TransactionRef, Status "
                + "FROM Payments WHERE TransactionRef = ?";

        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionRef);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
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
}
