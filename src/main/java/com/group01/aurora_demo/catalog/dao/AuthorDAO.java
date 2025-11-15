package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public void deleteAuthorsByProductId(long productId) throws SQLException {
        String sql = "DELETE FROM BookAuthors WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        }
    }

    public void addAuthorToProduct(long productId, long authorId) throws SQLException {
        String sql = "INSERT INTO BookAuthors (ProductID, AuthorID) VALUES (?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setLong(2, authorId);
            ps.executeUpdate();
        }
    }

    public Long findAuthorIdByName(String name) throws SQLException {
        String sql = "SELECT AuthorID FROM Authors WHERE AuthorName = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong("AuthorID");
                return null;
            }
        }
    }

    public Long insertAuthor(String name) throws SQLException {
        String sql = "INSERT INTO Authors (AuthorName) VALUES (?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }
}
