package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VATDao {
    public double getVATRateFromPrimaryCategory(Connection conn, long productId) throws SQLException {
        String sql = """
                SELECT v.VATRate
                FROM VAT v
                INNER JOIN Category c ON v.VATCode = c.VATCode
                INNER JOIN ProductCategory pc ON c.CategoryID = pc.CategoryID
                WHERE pc.ProductID = ? AND pc.IsPrimary = 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("VATRate");
                }
            }
        }
        return 0.0;
    }
}
