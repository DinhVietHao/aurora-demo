package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.Author;

public class AuthorDAO {
    public List<Author> getAuthorsByProductId(long productId) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = """
                SELECT a.AuthorID, a.AuthorName
                FROM BookAuthors ba
                JOIN Authors a ON ba.AuthorID = a.AuthorID
                WHERE ba.ProductID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author();
                    author.setAuthorId(rs.getLong("AuthorID"));
                    author.setAuthorName(rs.getString("AuthorName"));
                    authors.add(author);
                }
            }
        }
        return authors;
    }
}
