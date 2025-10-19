package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group01.aurora_demo.common.config.DataSourceProvider;

public class FlashSaleDAO {
    public boolean isProductInCurrentFlashSale(long productId, LocalDateTime now) throws SQLException {
    String sql = """
        SELECT COUNT(*) 
        FROM FlashSaleItems fsi
        JOIN FlashSales fs ON fsi.FlashSaleID = fs.FlashSaleID
        WHERE fsi.ProductID = ?
          AND fs.Status = 'ACTIVE'
          AND fs.StartAt <= ? AND fs.EndAt >= ?
    """;

    try (Connection cn = DataSourceProvider.get().getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setLong(1, productId);
        ps.setTimestamp(2, Timestamp.valueOf(now));
        ps.setTimestamp(3, Timestamp.valueOf(now));
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }
}
}
