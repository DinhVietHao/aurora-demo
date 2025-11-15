package com.group01.aurora_demo.catalog.dao;

import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class CategoryDAO {

    public List<Category> getCategoriesByProductId(long productId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = """
                SELECT c.CategoryID, c.Name, pc.IsPrimary
                FROM ProductCategory pc
                JOIN Category c ON pc.CategoryID = c.CategoryID
                WHERE pc.ProductID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getLong("CategoryID"));
                    category.setName(rs.getString("Name"));
                    category.setPrimary(rs.getBoolean("IsPrimary"));
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryID, Name FROM Category ORDER BY Name ASC";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getLong("CategoryID"));
                c.setName(rs.getString("Name"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteCategoriesByProductId(long productId) throws SQLException {
        String sql = "DELETE FROM ProductCategory WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        }
    }

    public void addCategoryToProduct(long productId, long categoryId, boolean isPrimary) throws SQLException {
        String sql = "INSERT INTO ProductCategory (ProductID, CategoryID, IsPrimary) VALUES (?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setLong(2, categoryId);
            ps.setBoolean(3, isPrimary);
            ps.executeUpdate();
        }
    }

    public List<Category> getCategoriesByShopId(long shopId) throws SQLException {
        String sql = """
                    SELECT DISTINCT c.CategoryID, c.Name
                    FROM Category c
                    INNER JOIN ProductCategory pc ON c.CategoryID = pc.CategoryID
                    INNER JOIN Products p ON pc.ProductID = p.ProductID
                    WHERE p.ShopID = ? AND p.Status IN ('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'PENDING')
                    ORDER BY c.Name
                """;

        List<Category> categories = new ArrayList<>();

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getLong("CategoryID"));
                category.setName(rs.getString("Name"));
                categories.add(category);
            }
        }

        return categories;
    }
}