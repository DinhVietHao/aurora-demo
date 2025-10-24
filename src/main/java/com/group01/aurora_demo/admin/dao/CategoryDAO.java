package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.Category;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Category operations in admin module
 *
 * @author Aurora Team
 */
public class CategoryDAO {
    private final DataSource dataSource;

    public CategoryDAO() {
        this.dataSource = DataSourceProvider.get();
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryID, Name, VATCode FROM Category ORDER BY Name ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getLong("CategoryID"));
                c.setName(rs.getString("Name"));
                c.setVatCode(rs.getString("VATCode"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get categories by product ID
     */
    public List<Category> getCategoriesByProductId(long productId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.CategoryID, c.Name, c.VATCode " +
                     "FROM ProductCategory pc " +
                     "JOIN Category c ON pc.CategoryID = c.CategoryID " +
                     "WHERE pc.ProductID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getLong("CategoryID"));
                    category.setName(rs.getString("Name"));
                    category.setVatCode(rs.getString("VATCode"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(long categoryId) {
        String sql = "SELECT CategoryID, Name, VATCode FROM Category WHERE CategoryID = ?";
        Category category = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setCategoryId(rs.getLong("CategoryID"));
                    category.setName(rs.getString("Name"));
                    category.setVatCode(rs.getString("VATCode"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    /**
     * Add new category
     */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO Category (Name, VATCode) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getVatCode());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update category
     */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Category SET Name = ?, VATCode = ? WHERE CategoryID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getVatCode());
            ps.setLong(3, category.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete category
     */
    public boolean deleteCategory(long categoryId) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if category is in use by any product
     */
    public boolean isCategoryInUse(long categoryId) {
        String sql = "SELECT COUNT(*) FROM ProductCategory WHERE CategoryID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if category name exists (for uniqueness validation)
     */
    public boolean categoryNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM Category WHERE Name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if category name exists excluding a specific category ID (for update validation)
     */
    public boolean categoryNameExistsExcludingId(String name, long excludeCategoryId) {
        String sql = "SELECT COUNT(*) FROM Category WHERE Name = ? AND CategoryID != ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setLong(2, excludeCategoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get categories with pagination and filtering
     */
    public List<Category> getCategoriesWithPagination(String searchTerm, String vatCodeFilter,
                                                       int offset, int limit) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT CategoryID, Name, VATCode FROM Category WHERE 1=1"
        );

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND Name LIKE ?");
        }

        // Add VAT code filter
        if (vatCodeFilter != null && !vatCodeFilter.trim().isEmpty()) {
            sql.append(" AND VATCode = ?");
        }

        sql.append(" ORDER BY Name ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameter
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchTerm.trim() + "%");
            }

            // Set VAT code filter parameter
            if (vatCodeFilter != null && !vatCodeFilter.trim().isEmpty()) {
                ps.setString(paramIndex++, vatCodeFilter.trim());
            }

            // Set pagination parameters
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category c = new Category();
                    c.setCategoryId(rs.getLong("CategoryID"));
                    c.setName(rs.getString("Name"));
                    c.setVatCode(rs.getString("VATCode"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get total count of categories with filtering
     */
    public int getCategoryCount(String searchTerm, String vatCodeFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Category WHERE 1=1");

        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND Name LIKE ?");
        }

        // Add VAT code filter
        if (vatCodeFilter != null && !vatCodeFilter.trim().isEmpty()) {
            sql.append(" AND VATCode = ?");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameter
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchTerm.trim() + "%");
            }

            // Set VAT code filter parameter
            if (vatCodeFilter != null && !vatCodeFilter.trim().isEmpty()) {
                ps.setString(paramIndex, vatCodeFilter.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
