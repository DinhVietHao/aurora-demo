package com.group01.aurora_demo.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.shop.model.Author;
import com.group01.aurora_demo.shop.model.BookDetail;
import com.group01.aurora_demo.shop.model.Category;
import com.group01.aurora_demo.shop.model.Product;

public class ProductDAO {
    public List<Product> getProductsByShopId(long shopId, int offset, int limit) throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = """
                        SELECT p.ProductID, p.ShopID, p.Title, p.Description,
                        p.OriginalPrice, p.SalePrice, p.SoldCount, p.Stock,
                        p.PublisherID, p.PublishedDate, p.Weight, p.CreatedAt, p.status,
                        b.Translator, b.[Version], b.CoverType, b.Pages,
                        b.LanguageCode, b.[Size], b.ISBN,
                        pi.Url AS PrimaryImageUrl
                FROM Products p
                LEFT JOIN BookDetails b ON p.ProductID = b.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                WHERE p.ShopID = ?
                ORDER BY p.CreatedAt DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                        """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getLong("ProductID"));
                    p.setShopId(rs.getLong("ShopID"));
                    p.setTitle(rs.getString("Title"));
                    p.setDescription(rs.getString("Description"));
                    p.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    p.setSalePrice(rs.getDouble("SalePrice"));
                    p.setSoldCount(rs.getLong("SoldCount"));
                    p.setStock(rs.getInt("Stock"));
                    p.setAuthors(getAuthorsByProductId(p.getProductId()));
                    p.setCategories(getCategoriesByProductId(p.getProductId()));
                    p.setStatus(rs.getString("status"));

                    long publisherId = rs.getLong("PublisherID");
                    if (!rs.wasNull()) {
                        p.setPublisherId(publisherId);
                    }

                    p.setPublishedDate(rs.getDate("PublishedDate"));
                    p.setWeight(rs.getDouble("Weight"));
                    p.setCreatedAt(rs.getDate("CreatedAt"));
                    p.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                    // Gán BookDetail
                    BookDetail b = new BookDetail();
                    b.setProductId(rs.getLong("ProductID"));
                    b.setTranslator(rs.getString("Translator"));
                    b.setVersion(rs.getString("Version"));
                    b.setCoverType(rs.getString("CoverType"));
                    b.setPages(rs.getInt("Pages"));
                    b.setLanguageCode(rs.getString("LanguageCode"));
                    b.setSize(rs.getString("Size"));
                    b.setIsbn(rs.getString("ISBN"));
                    p.setBookDetail(b);

                    // Danh sách ảnh (nếu bạn vẫn muốn lấy thêm ảnh phụ)
                    p.setImageUrls(getProductImages(p.getProductId()));

                    list.add(p);
                }
            }
        }
        return list;
    }

    private List<String> getProductImages(long productId) throws SQLException {
        List<String> urls = new ArrayList<>();
        String sql = "SELECT Url FROM ProductImages WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    urls.add(rs.getString("Url"));
                }
            }
        }
        return urls;
    }

    private List<Author> getAuthorsByProductId(long productId) throws SQLException {
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
                    author.setName(rs.getString("AuthorName"));
                    authors.add(author);
                }
            }
        }
        return authors;
    }

    private List<Category> getCategoriesByProductId(long productId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = """
                SELECT c.CategoryID, c.Name
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
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public int countProductsByShopId(long shopId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Products WHERE ShopID = ?";
    try (Connection cn = DataSourceProvider.get().getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setLong(1, shopId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    return 0;
}

}
