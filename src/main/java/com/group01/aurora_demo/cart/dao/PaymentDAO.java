package com.group01.aurora_demo.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.group01.aurora_demo.cart.model.Payment;

public class PaymentDAO {
    public boolean createPayment(Connection conn, Payment payment) throws SQLException {
        String sql = """
                    INSERT INTO Payments(OrderID, Amount, TransactionRef)
                    VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, payment.getOrderId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getTransactionRef());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
