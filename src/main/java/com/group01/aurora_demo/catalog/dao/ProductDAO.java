package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.model.Language;
import com.group01.aurora_demo.catalog.dao.dto.ProductDTO;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.catalog.model.Publisher;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.ProductImage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.*;

public class ProductDAO {

    private Product mapToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getLong("ProductID"));
        p.setTitle(rs.getString("Title"));
        p.setSalePrice(rs.getDouble("SalePrice"));
        p.setOriginalPrice(rs.getDouble("OriginalPrice"));
        p.setSoldCount(rs.getLong("SoldCount"));
        p.setAvgRating(rs.getObject("AvgRating", Double.class) != null ? rs.getDouble("AvgRating") : 0.0);
        p.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));
        String publisherName = rs.getString("PublisherName");
        if (publisherName != null) {
            Publisher pub = new Publisher();
            pub.setName(publisherName);
            p.setPublisher(pub);
        }
        return p;
    }

    public List<Product> getSuggestedProductsForCustomer(Long userId) {
        String sql = """
                SELECT TOP 10
                    p.ProductID,
                    p.Title,
                    p.SalePrice,
                    p.OriginalPrice,
                    p.SoldCount,
                    ISNULL(AVG(r.Rating), 0) AS AvgRating,
                    img.Url AS PrimaryImageUrl,
                    pub.Name AS PublisherName
                FROM Products p
                JOIN ProductCategory pc ON p.ProductID = pc.ProductID
                LEFT JOIN OrderItems oi2 ON p.ProductID = oi2.ProductID
                LEFT JOIN Reviews r ON oi2.OrderItemID = r.OrderItemID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                WHERE pc.CategoryID IN (
                    -- Thể loại từ lịch sử mua (hoàn thành)
                    SELECT DISTINCT pc2.CategoryID
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    JOIN ProductCategory pc2 ON oi.ProductID = pc2.ProductID
                    WHERE o.UserID = ? AND o.OrderStatus IN (N'SUCCESS')
                    UNION
                    -- Thể loại từ giỏ hàng
                    SELECT DISTINCT pc3.CategoryID
                    FROM CartItems ci
                    JOIN ProductCategory pc3 ON ci.ProductID = pc3.ProductID
                    WHERE ci.UserID = ?
                )
                AND p.Status = 'ACTIVE'
                AND p.ProductID NOT IN (
                    -- Loại trừ sản phẩm đã mua
                    SELECT oi.ProductID
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    WHERE o.UserID = ?
                    UNION
                    -- Loại trừ sản phẩm trong giỏ
                    SELECT ci.ProductID
                    FROM CartItems ci
                    WHERE ci.UserID = ?
                )
                GROUP BY p.ProductID, p.Title, p.SalePrice, p.OriginalPrice, p.SoldCount, img.Url, pub.Name
                ORDER BY p.SoldCount DESC, ISNULL(AVG(r.Rating), 0) DESC;
                    """;
        List<Product> products = new ArrayList<>();
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            stmt.setLong(3, userId);
            stmt.setLong(4, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return products;
    }

    public List<Product> getSuggestedProductsForGuest() {
        String sql = """
                SELECT TOP 10
                    p.ProductID,
                    p.Title,
                    p.SalePrice,
                    p.OriginalPrice,
                    p.SoldCount,
                    ISNULL(AVG(r.Rating), 0) AS AvgRating,
                    img.Url AS PrimaryImageUrl,
                    pub.Name AS PublisherName
                FROM Products p
                LEFT JOIN OrderItems oi ON p.ProductID = oi.ProductID
                LEFT JOIN Reviews r ON oi.OrderItemID = r.OrderItemID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                WHERE p.Status = 'ACTIVE'
                GROUP BY p.ProductID, p.Title, p.SalePrice, p.OriginalPrice,
                         p.SoldCount, img.Url, pub.Name
                ORDER BY p.SoldCount DESC, ISNULL(AVG(r.Rating), 0) DESC;
                """;
        List<Product> products = new ArrayList<>();
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return products;
    }

    public List<Product> getLatestProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = """
                SELECT TOP (?)
                    p.ProductID,
                    p.Title,
                    p.SalePrice,
                    p.OriginalPrice,
                    p.SoldCount,
                    ISNULL(AVG(r.Rating), 0) AS AvgRating,
                    img.Url AS PrimaryImageUrl,
                    pub.Name AS PublisherName
                FROM Products p
                LEFT JOIN OrderItems oi ON p.ProductID = oi.ProductID
                LEFT JOIN Reviews r ON oi.OrderItemID = r.OrderItemID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                WHERE p.Status = 'ACTIVE'
                GROUP BY p.ProductID, p.Title, p.SalePrice, p.OriginalPrice, p.SoldCount, img.Url, pub.Name
                ORDER BY MAX(p.CreatedAt) DESC
                    """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getLatestProducts: " + e.getMessage());
        }
        return products;
    }

    public int countAllProducts() {
        String sql = "SELECT COUNT(*) FROM Products WHERE Status = 'ACTIVE'";
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error in countAllProducts: " + e.getMessage());
        }
        return 0;
    }

    public int countProductsWithSold() {
        String sql = "SELECT COUNT(*) FROM Products WHERE Status = 'ACTIVE' AND SoldCount > 0";
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error (Bookstore page) in countProductsWithSold: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> getAllProducts(int offset, int limit, String sort) {
        String orderBy;
        switch (sort) {
            case "pop":
                orderBy = "ISNULL(r.AvgRating, 0) DESC, p.SoldCount DESC";
                break;
            case "best":
                orderBy = "p.SoldCount DESC, ISNULL(r.AvgRating, 0) DESC";
                break;
            case "priceAsc":
                orderBy = "p.SalePrice ASC";
                break;
            case "priceDesc":
                orderBy = "p.SalePrice DESC";
                break;
            case "newest":
                orderBy = "p.CreatedAt DESC";
                break;
            default:
                orderBy = "p.SoldCount DESC, ISNULL(r.AvgRating, 0) DESC";
                break;
        }

        String sql = """
                SELECT
                  p.ProductID,
                  p.Title,
                  p.SalePrice,
                  p.OriginalPrice,
                  p.SoldCount,
                  ISNULL(r.AvgRating, 0) AS AvgRating,
                  img.Url AS PrimaryImageUrl,
                  pub.Name AS PublisherName,
                  p.CreatedAt
                FROM Products p
                LEFT JOIN (
                    SELECT oi.ProductID, AVG(CAST(r.Rating AS FLOAT)) AS AvgRating
                    FROM OrderItems oi
                    JOIN Reviews r ON oi.OrderItemID = r.OrderItemID
                    GROUP BY oi.ProductID
                ) r ON r.ProductID = p.ProductID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                WHERE p.[Status] = 'ACTIVE'
                """
                + " ORDER BY " + orderBy + " "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";

        List<Product> products = new ArrayList<>();
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offset);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error (Bookstore page) in getAllProducts: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getAllProductsByKeyword(String keyword, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts(0, Integer.MAX_VALUE, "best");
        }
        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT DISTINCT p.ProductID, p.Title, p.SalePrice, p.OriginalPrice, p.SoldCount, "
                + "ISNULL(r.AvgRating, 0) AS AvgRating, img.Url AS PrimaryImageUrl, pub.Name AS PublisherName "
                + "FROM Products p "
                + "LEFT JOIN ("
                + "    SELECT oi.ProductID, AVG(CAST(rv.Rating AS FLOAT)) AS AvgRating "
                + "    FROM OrderItems oi "
                + "    JOIN Reviews rv ON oi.OrderItemID = rv.OrderItemID "
                + "    GROUP BY oi.ProductID"
                + ") r ON r.ProductID = p.ProductID "
                + "LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1 "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "LEFT JOIN ProductCategory pc ON p.ProductID = pc.ProductID "
                + "LEFT JOIN Category c ON pc.CategoryID = c.CategoryID "
                + "WHERE (p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ? "
                + "OR pub.Name LIKE ? OR c.Name LIKE ?) "
                + "AND p.Status = 'ACTIVE' "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, searchPattern); // Title
            ps.setString(2, searchPattern); // Description
            ps.setString(3, searchPattern); // AuthorName
            ps.setString(4, searchPattern); // PublisherName
            ps.setString(5, searchPattern); // CategoryName
            ps.setInt(6, offset);
            ps.setInt(7, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllProductsByKeyword: " + e.getMessage());
        }
        return products;
    }

    public int countSearchResultsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countAllProducts();
        }
        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(DISTINCT p.ProductID) FROM Products p "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "LEFT JOIN ProductCategory pc ON p.ProductID = pc.ProductID "
                + "LEFT JOIN Category c ON pc.CategoryID = c.CategoryID "
                + "WHERE (p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ? "
                + "OR pub.Name LIKE ? OR c.Name LIKE ?) "
                + "AND p.Status = 'ACTIVE'";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error in countSearchResultsByKeyword: " + e.getMessage());
        }
        return 0;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT c.Name FROM Category c "
                + "JOIN ProductCategory pc ON c.CategoryID = pc.CategoryID "
                + "JOIN Products p ON pc.ProductID = p.ProductID "
                + "WHERE p.Status = 'ACTIVE' ORDER BY c.Name";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getCategories: " + e.getMessage());
        }
        return categories;
    }

    public List<String> getAuthors() {
        List<String> authors = new ArrayList<>();
        String sql = "SELECT DISTINCT a.AuthorName FROM Authors a "
                + "JOIN BookAuthors ba ON a.AuthorID = ba.AuthorID "
                + "JOIN Products p ON ba.ProductID = p.ProductID "
                + "WHERE p.Status = 'ACTIVE' ORDER BY a.AuthorName";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                authors.add(rs.getString("AuthorName"));
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getAuthors: " + e.getMessage());
        }
        return authors;
    }

    public List<String> getPublishers() {
        List<String> publishers = new ArrayList<>();
        String sql = "SELECT DISTINCT pub.Name FROM Publishers pub "
                + "JOIN Products p ON pub.PublisherID = p.PublisherID "
                + "WHERE p.Status = 'ACTIVE' ORDER BY pub.Name";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                publishers.add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getPublishers: " + e.getMessage());
        }
        return publishers;
    }

    public List<String> getLanguages() {
        List<String> languages = new ArrayList<>();
        String sql = "SELECT DISTINCT l.LanguageName FROM BookDetails bd "
                + "JOIN Languages l ON bd.LanguageCode = l.LanguageCode "
                + "JOIN Products p ON bd.ProductID = p.ProductID "
                + "WHERE p.Status = 'ACTIVE' ORDER BY l.LanguageName";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                languages.add(rs.getString("LanguageName"));
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getLanguages: " + e.getMessage());
        }
        return languages;
    }

    public List<Product> getAllProductsByFilter(int offset, int limit, String category, String author,
            String publisher, String language, Double minPrice, Double maxPrice) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT p.ProductID, p.Title, p.SalePrice, p.OriginalPrice, p.SoldCount, "
                        + "ISNULL(r.AvgRating, 0) AS AvgRating, img.Url AS PrimaryImageUrl, pub.Name AS PublisherName "
                        + "FROM Products p "
                        + "LEFT JOIN ("
                        + "    SELECT oi.ProductID, AVG(CAST(rv.Rating AS FLOAT)) AS AvgRating "
                        + "    FROM OrderItems oi "
                        + "    JOIN Reviews rv ON oi.OrderItemID = rv.OrderItemID "
                        + "    GROUP BY oi.ProductID"
                        + ") r ON r.ProductID = p.ProductID "
                        + "LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1 "
                        + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                        + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                        + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                        + "LEFT JOIN ProductCategory pc ON p.ProductID = pc.ProductID "
                        + "LEFT JOIN Category c ON pc.CategoryID = c.CategoryID "
                        + "LEFT JOIN BookDetails bd ON p.ProductID = bd.ProductID "
                        + "LEFT JOIN Languages l ON bd.LanguageCode = l.LanguageCode "
                        + "WHERE p.Status = 'ACTIVE' ");

        // Add filter conditions dynamically
        if (category != null && !category.isEmpty()) {
            sql.append("AND c.Name = ? ");
        }
        if (author != null && !author.isEmpty()) {
            sql.append("AND a.AuthorName = ? ");
        }
        if (publisher != null && !publisher.isEmpty()) {
            sql.append("AND pub.Name = ? ");
        }
        if (language != null && !language.isEmpty()) {
            sql.append("AND l.LanguageName = ? ");
        }
        if (minPrice != null) {
            sql.append("AND p.SalePrice >= ? ");
        }
        if (maxPrice != null) {
            sql.append("AND p.SalePrice <= ? ");
        }

        sql.append("ORDER BY p.SoldCount DESC, ISNULL(r.AvgRating, 0) DESC ")
                .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            // Bind parameters dynamically
            int index = 1;
            if (category != null && !category.isEmpty()) {
                ps.setString(index++, category);
            }
            if (author != null && !author.isEmpty()) {
                ps.setString(index++, author);
            }
            if (publisher != null && !publisher.isEmpty()) {
                ps.setString(index++, publisher);
            }
            if (language != null && !language.isEmpty()) {
                ps.setString(index++, language);
            }
            if (minPrice != null) {
                ps.setDouble(index++, minPrice);
            }
            if (maxPrice != null) {
                ps.setDouble(index++, maxPrice);
            }
            ps.setInt(index++, offset);
            ps.setInt(index++, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error (ProductDAO) in getAllProductsByFilter: " + e.getMessage());
        }
        return products;
    }

    public int countProductsByFilter(String category, String author, String publisher, String language, Double minPrice,
            Double maxPrice) {
        int count = 0;

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT p.ProductID) "
                        + "FROM Products p "
                        + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                        + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                        + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                        + "LEFT JOIN ProductCategory pc ON p.ProductID = pc.ProductID "
                        + "LEFT JOIN Category c ON pc.CategoryID = c.CategoryID "
                        + "LEFT JOIN BookDetails bd ON p.ProductID = bd.ProductID "
                        + "LEFT JOIN Languages l ON bd.LanguageCode = l.LanguageCode "
                        + "WHERE p.Status = 'ACTIVE' ");

        // Add filter conditions dynamically (same as getAllProductsByFilter)
        if (category != null && !category.isEmpty()) {
            sql.append("AND c.Name = ? ");
        }
        if (author != null && !author.isEmpty()) {
            sql.append("AND a.AuthorName = ? ");
        }
        if (publisher != null && !publisher.isEmpty()) {
            sql.append("AND pub.Name = ? ");
        }
        if (language != null && !language.isEmpty()) {
            sql.append("AND l.LanguageName = ? ");
        }
        if (minPrice != null) {
            sql.append("AND p.SalePrice >= ? ");
        }
        if (maxPrice != null) {
            sql.append("AND p.SalePrice <= ? ");
        }

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            // Bind parameters dynamically
            int index = 1;
            if (category != null && !category.isEmpty()) {
                ps.setString(index++, category);
            }
            if (author != null && !author.isEmpty()) {
                ps.setString(index++, author);
            }
            if (publisher != null && !publisher.isEmpty()) {
                ps.setString(index++, publisher);
            }
            if (language != null && !language.isEmpty()) {
                ps.setString(index++, language);
            }
            if (minPrice != null) {
                ps.setDouble(index++, minPrice);
            }
            if (maxPrice != null) {
                ps.setDouble(index++, maxPrice);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in countProductsByFilter: " + e.getMessage());
        }
        return count;
    }

    // ----- DuyHT -----

    /**
     * Count total number of products in the database.
     *
     * @return number of products
     */
    public int countProducts() {
        String sql = "SELECT COUNT(*) FROM Products";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1); // Return COUNT result
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieve products with pagination.
     *
     * @param page     current page number (starting from 1)
     * @param pageSize number of products per page
     * @return list of products for the given page
     */
    public List<Product> getProductsByPage(int page, int pageSize) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.ProductID, p.ShopID, p.Title, p.Description, "
                + "p.OriginalPrice, p.SalePrice, p.SoldCount, p.Stock, p.IsBundle, "
                + "p.CategoryID, p.PublishedDate, "
                + "i.Url AS PrimaryImageUrl, "
                + "pub.PublisherID, pub.Name AS PublisherName, "
                + "a.AuthorID, a.AuthorName "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1 "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                Map<Long, Product> productMap = new LinkedHashMap<>();

                while (rs.next()) {
                    long productId = rs.getLong("ProductID");
                    Product product = productMap.get(productId);

                    if (product == null) {
                        product = new Product();
                        product.setProductId(productId);
                        product.setShopId(rs.getLong("ShopID"));
                        product.setTitle(rs.getString("Title"));
                        product.setDescription(rs.getString("Description"));
                        product.setOriginalPrice(rs.getDouble("OriginalPrice"));
                        product.setSalePrice(rs.getDouble("SalePrice"));
                        product.setSoldCount(rs.getLong("SoldCount"));
                        product.setQuantity(rs.getInt("Quantity"));
                        // !!! Error
                        // product.setCategoryId(rs.getLong("CategoryID"));

                        Date publishedDate = rs.getDate("PublishedDate");
                        if (publishedDate != null) {
                            product.setPublishedDate(publishedDate);
                        }

                        product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                        // Gán Publisher
                        Long publisherId = (Long) rs.getObject("PublisherID");
                        String publisherName = rs.getString("PublisherName");
                        if (publisherId != null) {
                            Publisher publisher = new Publisher();
                            publisher.setPublisherId(publisherId);
                            publisher.setName(publisherName);
                            product.setPublisher(publisher);
                        }

                        // Init authors list
                        product.setAuthors(new ArrayList<>());

                        productMap.put(productId, product);
                    }

                    // Thêm tác giả nếu có
                    long authorId = rs.getLong("AuthorID");
                    if (!rs.wasNull()) {
                        Author author = new Author();
                        author.setAuthorId(authorId);
                        author.setAuthorName(rs.getString("AuthorName"));
                        product.getAuthors().add(author);
                    }
                }

                products.addAll(productMap.values());
            }

        } catch (SQLException e) {
            System.out.println("Error in getProductsByPage: " + e.getMessage());
        }

        return products;
    }

    /**
     * Search products by keyword with pagination.
     *
     * @param keyword  search keyword (searches in title, description, and author
     *                 name)
     * @param page     current page number (starting from 1)
     * @param pageSize number of products per page
     * @return list of products matching the search criteria
     */
    public List<Product> searchProducts(String keyword, int page, int pageSize) {
        List<Product> products = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductsByPage(page, pageSize);
        }

        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT DISTINCT p.ProductID, p.ShopID, p.Title, p.Description, "
                + "p.OriginalPrice, p.SalePrice, p.SoldCount, p.Stock, p.IsBundle, "
                + "p.CategoryID, p.PublishedDate, "
                + "i.Url AS PrimaryImageUrl, "
                + "pub.PublisherID, pub.Name AS PublisherName, "
                + "a.AuthorID, a.AuthorName "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1 "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "WHERE p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ? "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, (page - 1) * pageSize);
            ps.setInt(5, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                Map<Long, Product> productMap = new LinkedHashMap<>();

                while (rs.next()) {
                    long productId = rs.getLong("ProductID");
                    Product product = productMap.get(productId);

                    if (product == null) {
                        product = new Product();
                        product.setProductId(productId);
                        product.setShopId(rs.getLong("ShopID"));
                        product.setTitle(rs.getString("Title"));
                        product.setDescription(rs.getString("Description"));
                        product.setOriginalPrice(rs.getDouble("OriginalPrice"));
                        product.setSalePrice(rs.getDouble("SalePrice"));
                        product.setSoldCount(rs.getLong("SoldCount"));
                        product.setQuantity(rs.getInt("Quantity"));
                        // product.setCategoryId(rs.getLong("CategoryID"));

                        Date publishedDate = rs.getDate("PublishedDate");
                        if (publishedDate != null) {
                            product.setPublishedDate(publishedDate);
                        }

                        product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                        // Gán Publisher
                        Long publisherId = (Long) rs.getObject("PublisherID");
                        String publisherName = rs.getString("PublisherName");
                        if (publisherId != null) {
                            Publisher publisher = new Publisher();
                            publisher.setPublisherId(publisherId);
                            publisher.setName(publisherName);
                            product.setPublisher(publisher);
                        }

                        // Init authors list
                        product.setAuthors(new ArrayList<>());

                        productMap.put(productId, product);
                    }

                    // Thêm tác giả nếu có
                    long authorId = rs.getLong("AuthorID");
                    if (!rs.wasNull()) {
                        Author author = new Author();
                        author.setAuthorId(authorId);
                        author.setAuthorName(rs.getString("AuthorName"));
                        product.getAuthors().add(author);
                    }
                }

                products.addAll(productMap.values());
            }

        } catch (SQLException e) {
            System.out.println("Error in searchProducts: " + e.getMessage());
        }

        return products;
    }

    /**
     * Count products matching search keyword.
     *
     * @param keyword search keyword
     * @return number of products matching the search
     */
    public int countSearchResults(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countProducts();
        }

        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(DISTINCT p.ProductID) FROM Products p "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "WHERE p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in countSearchResults: " + e.getMessage());
        }
        return 0;
    }

    // ----- Phạm Thanh Lượng -----

    public List<Product> getProductsByShopId(long shopId)
            throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = """
                SELECT p.ProductID, p.ShopID, p.Title, p.Description,
                p.OriginalPrice, p.SalePrice, p.SoldCount, p.Quantity,
                p.PublisherID, p.PublishedDate, p.Weight, p.CreatedAt, p.status,
                b.Translator, b.[Version], b.CoverType, b.Pages,
                b.LanguageCode, b.[Size], b.ISBN,
                pi.Url AS PrimaryImageUrl
                FROM Products p
                LEFT JOIN BookDetails b ON p.ProductID = b.ProductID
                LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                WHERE p.ShopID = ?
                ORDER BY p.CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);
            CategoryDAO cateDAO = new CategoryDAO();
            AuthorDAO authorDAO = new AuthorDAO();
            ImageDAO imageDAO = new ImageDAO();
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
                    p.setQuantity(rs.getInt("Quantity"));
                    p.setAuthors(authorDAO.getAuthorsByProductId(p.getProductId()));
                    p.setCategories(cateDAO.getCategoriesByProductId(p.getProductId()));
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

                    p.setImageUrls(imageDAO.getListImageUrlsByProductId(p.getProductId()));

                    list.add(p);
                }
            }
        }
        return list;
    }

    public int countProductsByShopId(long shopId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Products WHERE ShopID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return 0;
    }

    public long insertProduct(Product product) throws SQLException {
        String sqlInsertProduct = """
                INSERT INTO Products (ShopID, Title, Description, OriginalPrice, SalePrice,
                Quantity, PublisherID,
                Weight, PublishedDate, [Status])
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
                """;

        String sqlInsertBookDetail = """
                INSERT INTO BookDetails (ProductID, Translator, [Version], CoverType, Pages,
                LanguageCode, [Size], ISBN)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlInsertProductImage = """
                INSERT INTO ProductImages (ProductID, Url, IsPrimary)
                VALUES (?, ?, ?)
                """;

        String sqlInsertCategory = """
                INSERT INTO ProductCategory (ProductID, CategoryID)
                VALUES (?, ?)
                """;

        String sqlInsertAuthor = """
                INSERT INTO BookAuthors (ProductID, AuthorID)
                VALUES (?, ?)
                """;

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            cn.setAutoCommit(false);
            long productId = 0;

            // Insert Products & lấy ProductID
            try (PreparedStatement ps = cn.prepareStatement(sqlInsertProduct,
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, product.getShopId());
                ps.setString(2, product.getTitle());
                ps.setString(3, product.getDescription());
                ps.setDouble(4, product.getOriginalPrice());
                ps.setDouble(5, product.getSalePrice());
                ps.setInt(6, product.getQuantity());
                if (product.getPublisherId() != null) {
                    ps.setLong(7, product.getPublisherId());
                } else {
                    ps.setNull(7, Types.BIGINT);
                }
                ps.setDouble(8, product.getWeight());
                ps.setDate(9, product.getPublishedDate() != null
                        ? new java.sql.Date(product.getPublishedDate().getTime())
                        : null);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    cn.rollback();
                    throw new SQLException("Chèn product thất bại (0 rows).");
                }
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        productId = rs.getLong(1);
                }
            }

            if (productId == 0) {
                cn.rollback();
                throw new SQLException("Không lấy được ProductID.");
            }

            // Insert BookDetails (1 record)
            if (product.getBookDetail() != null) {
                BookDetail bd = product.getBookDetail();
                try (PreparedStatement ps = cn.prepareStatement(sqlInsertBookDetail)) {
                    ps.setLong(1, productId);
                    ps.setString(2, bd.getTranslator());
                    ps.setString(3, bd.getVersion());
                    ps.setString(4, bd.getCoverType());
                    ps.setInt(5, bd.getPages());
                    ps.setString(6, bd.getLanguageCode());
                    ps.setString(7, bd.getSize());
                    ps.setString(8, bd.getIsbn());
                    ps.executeUpdate();
                }
            }

            // Insert ProductImages (mỗi ảnh 1 executeUpdate) -- Error
            if (product.getImageUrls() != null) {
                try (PreparedStatement ps = cn.prepareStatement(sqlInsertProductImage)) {
                    for (int i = 0; i < product.getImageUrls().size(); i++) {
                        ps.setLong(1, productId);
                        ps.setString(2, product.getImageUrls().get(i));
                        ps.setBoolean(3, i == 0);
                        ps.executeUpdate();
                    }
                }
            }

            // Insert Categories
            if (product.getCategories() != null) {
                try (PreparedStatement ps = cn.prepareStatement(sqlInsertCategory)) {
                    for (Category c : product.getCategories()) {
                        ps.setLong(1, productId);
                        ps.setLong(2, c.getCategoryId());
                        ps.executeUpdate();
                    }
                }
            }

            // Insert Authors
            if (product.getAuthors() != null) {
                try (PreparedStatement ps = cn.prepareStatement(sqlInsertAuthor)) {
                    for (Author a : product.getAuthors()) {
                        ps.setLong(1, productId);
                        ps.setLong(2, a.getAuthorId());
                        ps.executeUpdate();
                    }
                }
            }

            cn.commit();
            return productId;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Long findAuthorIdByName(String name) throws SQLException {
        String sql = "SELECT AuthorID FROM Authors WHERE AuthorName = ?";
        try (Connection c = DataSourceProvider.get().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        }
        return null;
    }

    public Long insertAuthor(String name) throws SQLException {
        String sql = "INSERT INTO Authors (AuthorName) OUTPUT INSERTED.AuthorID VALUES (?)";
        try (Connection c = DataSourceProvider.get().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        }
        throw new SQLException("Không thể thêm tác giả: " + name);
    }

    public boolean deleteProduct(long productId) {
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setLong(1, productId);
            int affectedRows = stmt.executeUpdate();

            // Nếu trigger chặn, SQL Server sẽ không xóa dòng nào → affectedRows = 0
            return affectedRows > 0;

        } catch (SQLException e) {
            // Kiểm tra nếu lỗi do trigger RAISERROR gửi ra
            if (e.getMessage().contains("Không thể xóa sản phẩm")) {
                return false; // trigger gửi lỗi nghiệp vụ
            }
            e.printStackTrace();
            return false;
        }
    }

    public int countReviewsByProductId(long productId) {
        String sql = "SELECT COUNT(*) FROM Reviews WHERE OrderItemID IN (SELECT OrderItemID FROM OrderItems WHERE ProductID = ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in ProductDAO.countReviewsByProductId: " + e.getMessage());
        }
        return 0;
    }

    public Product getProductById(long productId) {
        Product product = null;
        String sql = """
                    SELECT
                        p.ProductID, p.ShopID, p.Title, p.Description,
                        p.OriginalPrice, p.SalePrice, p.SoldCount, p.Quantity,
                        p.Status, p.PublishedDate, p.Weight,
                        p.RejectReason, p.CreatedAt,
                        p.PublisherID, pub.Name AS PublisherName,
                        b.Translator, b.Version, b.CoverType, b.Pages,
                        b.LanguageCode, l.LanguageName, b.[Size], b.ISBN,
                        (SELECT AVG(CAST(r.Rating AS FLOAT))
                         FROM Reviews r
                         INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                         WHERE oi.ProductID = p.ProductID) AS AvgRating
                    FROM Products p
                    LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                    LEFT JOIN BookDetails b ON p.ProductID = b.ProductID
                    LEFT JOIN Languages l ON b.LanguageCode = l.LanguageCode
                    WHERE p.ProductID = ?
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                product = new Product();
                product.setProductId(rs.getLong("ProductID"));
                product.setShopId(rs.getLong("ShopID"));
                product.setTitle(rs.getString("Title"));
                product.setDescription(rs.getString("Description"));
                product.setOriginalPrice(rs.getDouble("OriginalPrice"));
                product.setSalePrice(rs.getDouble("SalePrice"));
                product.setSoldCount(rs.getLong("SoldCount"));
                product.setQuantity(rs.getInt("Quantity"));
                product.setStatus(rs.getString("Status"));
                product.setPublishedDate(rs.getDate("PublishedDate"));
                product.setWeight(rs.getDouble("Weight"));
                product.setRejectReason(rs.getString("RejectReason"));
                product.setCreatedAt(rs.getDate("CreatedAt"));
                product.setPublisherId(rs.getLong("PublisherID"));

                // Set avgRating (default to 0.0 if null)
                Double avgRating = rs.getDouble("AvgRating");
                product.setAvgRating(rs.wasNull() ? 0.0 : avgRating);

                // Publisher (N:1)
                if (rs.getString("PublisherName") != null) {
                    Publisher pub = new Publisher();
                    pub.setPublisherId(rs.getLong("PublisherID"));
                    pub.setName(rs.getString("PublisherName"));
                    product.setPublisher(pub);
                }

                // BookDetail (1:1)
                BookDetail bd = new BookDetail();
                bd.setProductId(productId);
                bd.setTranslator(rs.getString("Translator"));
                bd.setVersion(rs.getString("Version"));
                bd.setCoverType(rs.getString("CoverType"));

                int pages = rs.getInt("Pages");
                bd.setPages(rs.wasNull() ? null : pages);

                bd.setLanguageCode(rs.getString("LanguageCode"));
                bd.setSize(rs.getString("Size"));
                bd.setIsbn(rs.getString("ISBN"));

                // Language object
                if (rs.getString("LanguageCode") != null) {
                    Language lang = new Language();
                    lang.setLanguageCode(rs.getString("LanguageCode"));
                    lang.setLanguageName(rs.getString("LanguageName"));
                    bd.setLanguage(lang);
                }

                product.setBookDetail(bd);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error in getProductById main query: " + e.getMessage());
        }

        if (product == null)
            return null;

        // Load Authors
        try {
            AuthorDAO authorDAO = new AuthorDAO();
            product.setAuthors(authorDAO.getAuthorsByProductId(productId));
        } catch (SQLException e) {
            System.err.println("⚠ Error loading authors: " + e.getMessage());
        }

        // Load Categories
        try {
            CategoryDAO cateDAO = new CategoryDAO();
            product.setCategories(cateDAO.getCategoriesByProductId(productId));
        } catch (SQLException e) {
            System.err.println("⚠ Error loading categories: " + e.getMessage());
        }

        // Load Images
        List<ProductImage> images = new ArrayList<>();
        String imgSql = """
                    SELECT ImageID, Url, IsPrimary
                    FROM ProductImages
                    WHERE ProductID = ?
                    ORDER BY IsPrimary DESC, ImageID ASC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(imgSql)) {

            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductImage img = new ProductImage();
                img.setImageId(rs.getLong("ImageID"));
                img.setProductId(productId);
                img.setUrl(rs.getString("Url"));
                img.setIsPrimary(rs.getBoolean("IsPrimary"));
                images.add(img);
            }

        } catch (SQLException e) {
            System.err.println("⚠ Error loading product images: " + e.getMessage());
        }

        product.setImages(images);
        product.setPrimaryImageUrl(images.stream()
                .filter(ProductImage::getIsPrimary)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0).getUrl()));

        return product;
    }

    public boolean updateStock(Connection conn, long productId, int quantity) {
        String sql = """
                    UPDATE Products
                    SET Quantity = Quantity - ?,
                        SoldCount = SoldCount + ?,
                        Status = CASE WHEN Quantity - ? <= 0 THEN 'OUT_OF_STOCK' ELSE 'ACTIVE' END
                    WHERE ProductID = ? AND Quantity >= ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, quantity);
            ps.setLong(4, productId);
            ps.setInt(5, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreStock(Connection conn, long productId, int quantity) {
        String sql = """
                    UPDATE Products
                    SET Quantity = Quantity + ?,
                        SoldCount = CASE WHEN SoldCount - ? < 0 THEN 0 ELSE SoldCount - ? END,
                        Status = CASE WHEN Quantity + ? <= 0 THEN 'OUT_OF_STOCK' ELSE 'ACTIVE' END
                    WHERE ProductID = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, quantity);
            ps.setInt(4, quantity);
            ps.setLong(5, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasEnoughQuantity(Connection conn, long productId, int quantity) {
        String sql = "SELECT Quantity FROM Products WHERE ProductID = ? AND Quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setInt(2, quantity);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getBasicProductById(long productId) {
        Product product = null;
        String sql = """
                    SELECT ProductID, ShopID, Title, SalePrice, Quantity, SoldCount, Status
                    FROM Products
                    WHERE ProductID = ?
                """;
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new Product();
                product.setProductId(rs.getLong("ProductID"));
                product.setShopId(rs.getLong("ShopID"));
                product.setTitle(rs.getString("Title"));
                product.setSalePrice(rs.getDouble("SalePrice"));
                product.setQuantity(rs.getInt("Quantity"));
                product.setSoldCount(rs.getLong("SoldCount"));
                product.setStatus(rs.getString("Status"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return product;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String updateProductSql = """
                UPDATE Products
                SET
                    Title = ?,
                    Description = ?,
                    OriginalPrice = ?,
                    SalePrice = ?,
                    Quantity = ?,
                    PublisherID = ?,
                    PublishedDate = ?,
                    Weight = ?
                WHERE ProductID = ?
                """;

        String updateBookDetailSql = """
                UPDATE BookDetails
                SET
                    Translator = ?,
                    Version = ?,
                    CoverType = ?,
                    Pages = ?,
                    LanguageCode = ?,
                    Size = ?,
                    ISBN = ?
                WHERE ProductID = ?
                """;

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            cn.setAutoCommit(false);
            try (
                    PreparedStatement psProd = cn.prepareStatement(updateProductSql);
                    PreparedStatement psBD = cn.prepareStatement(updateBookDetailSql)) {
                // --- Cập nhật bảng Products ---
                psProd.setString(1, product.getTitle());
                psProd.setString(2, product.getDescription());
                psProd.setDouble(3, product.getOriginalPrice());
                psProd.setDouble(4, product.getSalePrice());
                psProd.setInt(5, product.getQuantity());
                psProd.setLong(6, product.getPublisherId());
                psProd.setDate(7, product.getPublishedDate());
                psProd.setDouble(8, product.getWeight());
                psProd.setLong(9, product.getProductId());

                int updatedRows = psProd.executeUpdate();
                BookDetail bd = product.getBookDetail();
                if (bd != null) {
                    psBD.setString(1, bd.getTranslator());
                    psBD.setString(2, bd.getVersion());
                    psBD.setString(3, bd.getCoverType());
                    psBD.setObject(4, bd.getPages());
                    psBD.setString(5, bd.getLanguageCode());
                    psBD.setString(6, bd.getSize());
                    psBD.setString(7, bd.getIsbn());
                    psBD.setLong(8, product.getProductId());
                    psBD.executeUpdate();
                }

                cn.commit();
                return updatedRows > 0;

            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public boolean existsProductInActiveOrders(long productId) throws SQLException {
        String sql = """
                    SELECT COUNT(*)
                    FROM OrderItems oi
                    JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID
                    WHERE oi.ProductID = ?
                      AND os.[Status] IN ('PENDING', 'SHIPPING', 'WAITING_SHIP', 'CONFIRM', 'PENDING_PAYMENT', 'RETURNED_REQUESTED')
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean updateProductStatus(long productId, String newStatus) throws SQLException {
        String sql = "UPDATE Products SET Status = ? WHERE ProductID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<ProductDTO> getActiveProductsByShop(long shopId) throws SQLException {
        List<ProductDTO> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductID,
                        p.Title AS ProductName,
                        p.SalePrice,
                        p.Quantity,
                        p.OriginalPrice,
                        STRING_AGG(c.Name, ', ') AS CategoryNames,
                        pi.Url AS ImageUrl,
                        MAX(p.CreatedAt) AS CreatedAt
                    FROM Products p
                    LEFT JOIN ProductCategory pc ON p.ProductID = pc.ProductID
                    LEFT JOIN Category c ON pc.CategoryID = c.CategoryID
                    LEFT JOIN ProductImages pi ON p.ProductID = pi.ProductID AND pi.IsPrimary = 1
                    WHERE p.ShopID = ?
                      AND p.Status = 'ACTIVE'
                    GROUP BY
                        p.ProductID,
                        p.Title,
                        p.SalePrice,
                        p.Quantity,
                        p.OriginalPrice,
                        pi.Url
                    ORDER BY MAX(p.CreatedAt) DESC;
                """;

        try (
                Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDTO p = new ProductDTO();
                    p.setProductId(rs.getLong("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setSalePrice(rs.getDouble("SalePrice"));
                    p.setQuantity(rs.getInt("Quantity"));
                    p.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    p.setCategoryNames(rs.getString("CategoryNames"));
                    p.setImageUrl(rs.getString("ImageUrl"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<Product> getRelatedProducts(long productId, List<Long> categoryIds, int limit) {
        List<Product> products = new ArrayList<>();
        String categoryIdList = categoryIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String sql = String.format("""
                    SELECT TOP (?)
                        p.ProductID,
                        p.Title,
                        p.SalePrice,
                        p.OriginalPrice,
                        p.SoldCount,
                        p.CreatedAt,
                        ISNULL(r.AvgRating, 0) AS AvgRating,
                        img.Url AS PrimaryImageUrl,
                        pub.Name AS PublisherName,
                        CASE
                            WHEN p.SoldCount > 0 OR ISNULL(r.AvgRating, 0) > 0 THEN 0
                            ELSE 1
                        END AS SortPriority
                    FROM Products p
                    INNER JOIN ProductCategory pc ON p.ProductID = pc.ProductID
                    LEFT JOIN (
                        SELECT oi.ProductID, AVG(CAST(rv.Rating AS FLOAT)) AS AvgRating
                        FROM OrderItems oi
                        JOIN Reviews rv ON oi.OrderItemID = rv.OrderItemID
                        GROUP BY oi.ProductID
                    ) r ON r.ProductID = p.ProductID
                    LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                    LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                    WHERE pc.CategoryID IN (%s)
                      AND p.ProductID <> ?
                      AND p.Status = 'ACTIVE'
                    GROUP BY
                        p.ProductID,
                        p.Title,
                        p.SalePrice,
                        p.OriginalPrice,
                        p.SoldCount,
                        p.CreatedAt,
                        r.AvgRating,
                        img.Url,
                        pub.Name
                    ORDER BY
                        SortPriority ASC,
                        p.SoldCount DESC,
                        ISNULL(r.AvgRating, 0) DESC,
                        p.CreatedAt DESC
                """, categoryIdList);
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setLong(2, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in \"getRelatedProducts\" function: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getProductsByShopId(long shopId, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT
                        p.ProductID,
                        p.Title,
                        p.SalePrice,
                        p.OriginalPrice,
                        p.SoldCount,
                        ISNULL(r.AvgRating, 0) AS AvgRating,
                        img.Url AS PrimaryImageUrl,
                        pub.Name AS PublisherName
                    FROM Products p
                    LEFT JOIN (
                        SELECT oi.ProductID, AVG(CAST(rv.Rating AS FLOAT)) AS AvgRating
                        FROM OrderItems oi
                        JOIN Reviews rv ON oi.OrderItemID = rv.OrderItemID
                        GROUP BY oi.ProductID
                    ) r ON r.ProductID = p.ProductID
                    LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                    LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                    WHERE p.ShopID = ? AND p.Status = 'ACTIVE'
                    ORDER BY p.CreatedAt DESC
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in \"getProductsByShopId\" of viewShop feature: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getBestsellerByShopId(long shopId, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT TOP (?)
                        p.ProductID,
                        p.Title,
                        p.SalePrice,
                        p.OriginalPrice,
                        p.SoldCount,
                        ISNULL(r.AvgRating, 0) AS AvgRating,
                        img.Url AS PrimaryImageUrl,
                        pub.Name AS PublisherName
                    FROM Products p
                    LEFT JOIN (
                        SELECT oi.ProductID, AVG(CAST(rv.Rating AS FLOAT)) AS AvgRating
                        FROM OrderItems oi
                        JOIN Reviews rv ON oi.OrderItemID = rv.OrderItemID
                        GROUP BY oi.ProductID
                    ) r ON r.ProductID = p.ProductID
                    LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                    LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                    WHERE p.ShopID = ? AND p.Status = 'ACTIVE'
                    ORDER BY p.SoldCount DESC, ISNULL(r.AvgRating, 0) DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setLong(2, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in \"getBestsellerByShopId\" of viewShop feature: " + e.getMessage());
        }
        return products;
    }
}