package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class BookDetailDAO {
    public BookDetail getBookDetailByProductId(long productId) throws SQLException {
        String sql = """
                    SELECT
                        ProductID,
                        Translator,
                        Version,
                        CoverType,
                        Pages,
                        LanguageCode,
                        Size,
                        ISBN
                    FROM BookDetails
                    WHERE ProductID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BookDetail bd = new BookDetail();
                    bd.setProductId(rs.getLong("ProductID"));
                    bd.setTranslator(rs.getString("Translator"));
                    bd.setVersion(rs.getString("Version"));
                    bd.setCoverType(rs.getString("CoverType"));
                    bd.setPages(rs.getInt("Pages"));
                    if (rs.wasNull())
                        bd.setPages(null); // nếu Pages là NULL
                    bd.setLanguageCode(rs.getString("LanguageCode"));
                    bd.setSize(rs.getString("Size"));
                    bd.setIsbn(rs.getString("ISBN"));
                    return bd;
                }
            }
        }
        return null; // nếu không tìm thấy
    }
}
