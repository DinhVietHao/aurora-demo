package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.catalog.model.Publisher;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.ProductImage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

public class ProductDAO {

    // ========== Home page =========
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
        try (Connection conn = DataSourceProvider.get().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    // ============ Bookstore page ============ //

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
        // whitelist / enum-like mapping cho sort -> ORDER BY
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

    // ========== Search books by keyword =========== //

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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapToProduct(rs));
                }
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

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in countSearchResultsByKeyword: " + e.getMessage());
        }
        return 0;
    }

    // ================ Filter ================= //

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
     * @return number of products with ACTIVE status
     */
    public int countProducts() {
        return countProductsByStatus("ACTIVE");
    }
    
    /**
     * Count total number of products with the given status.
     *
     * @param status the status to filter by
     * @return number of products with the specified status
     */
    public int countProductsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Products WHERE Status = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
                
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return COUNT result
                }
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
        return getProductsByPageAndStatus(page, pageSize, "ACTIVE");
    }
    
    /**
     * Retrieve products with pagination and status filter.
     *
     * @param page     current page number (starting from 1)
     * @param pageSize number of products per page
     * @param status   the status to filter by (ACTIVE, PENDING, REJECTED)
     * @return list of products for the given page and status
     */
    public List<Product> getProductsByPageAndStatus(int page, int pageSize, String status) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.ProductID, p.ShopID, p.Title, p.Description, "
                + "p.OriginalPrice, p.SalePrice, p.SoldCount, p.Quantity, "
                + "p.PublisherID, p.PublishedDate, p.Status, "
                + "i.Url AS PrimaryImageUrl, "
                + "pub.PublisherID, pub.Name AS PublisherName, "
                + "a.AuthorID, a.AuthorName "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1 "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "WHERE p.Status = ? "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);

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
                        product.setStatus(rs.getString("Status"));

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
            System.out.println("Error in getProductsByPageAndStatus: " + e.getMessage());
        }

        return products;
    }

    /**
     * Search products by keyword with pagination.
     *
     * @param keyword  search keyword (searches in title, description, and author name)
     * @param page     current page number (starting from 1)
     * @param pageSize number of products per page
     * @return list of ACTIVE products matching the search criteria
     */
    public List<Product> searchProducts(String keyword, int page, int pageSize) {
        return searchProductsByStatus(keyword, page, pageSize, "ACTIVE");
    }
    
    /**
     * Search products by keyword and status with pagination.
     *
     * @param keyword  search keyword (searches in title, description, and author name)
     * @param page     current page number (starting from 1)
     * @param pageSize number of products per page
     * @param status   the status to filter by
     * @return list of products matching the search criteria and status
     */
    public List<Product> searchProductsByStatus(String keyword, int page, int pageSize, String status) {
        List<Product> products = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductsByPageAndStatus(page, pageSize, status);
        }

        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT DISTINCT p.ProductID, p.ShopID, p.Title, p.Description, "
                + "p.OriginalPrice, p.SalePrice, p.SoldCount, p.Quantity, "
                + "p.PublisherID, p.PublishedDate, p.Status, "
                + "i.Url AS PrimaryImageUrl, "
                + "pub.PublisherID, pub.Name AS PublisherName, "
                + "a.AuthorID, a.AuthorName "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1 "
                + "LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "WHERE (p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ?) "
                + "AND p.Status = ? "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, status);
            ps.setInt(5, (page - 1) * pageSize);
            ps.setInt(6, pageSize);

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
                        product.setStatus(rs.getString("Status"));

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
            System.out.println("Error in searchProductsByStatus: " + e.getMessage());
        }

        return products;
    }

    /**
     * Count products matching search keyword.
     *
     * @param keyword search keyword
     * @return number of ACTIVE products matching the search
     */
    public int countSearchResults(String keyword) {
        return countSearchResultsByStatus(keyword, "ACTIVE");
    }
    
    /**
     * Count products matching search keyword and status.
     *
     * @param keyword search keyword
     * @param status  the status to filter by
     * @return number of products matching the search and status
     */
    public int countSearchResultsByStatus(String keyword, String status) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countProductsByStatus(status);
        }

        String searchPattern = "%" + keyword.trim() + "%";
        String sql = "SELECT COUNT(DISTINCT p.ProductID) FROM Products p "
                + "LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID "
                + "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID "
                + "WHERE (p.Title LIKE ? OR p.Description LIKE ? OR a.AuthorName LIKE ?) "
                + "AND p.Status = ?";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in countSearchResultsByStatus: " + e.getMessage());
        }
        return 0;
    }

    // ----- Phạm Thanh Lượng -----

    public List<Product> getProductsByShopId(long shopId, int offset, int limit)
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
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
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

            // 1️⃣ Insert Products & lấy ProductID
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

            // 2️⃣ Insert BookDetails (1 record)
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

            // 3️⃣ Insert ProductImages (mỗi ảnh 1 executeUpdate) -- Error
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

            // 4️⃣ Insert Categories
            if (product.getCategories() != null) {
                try (PreparedStatement ps = cn.prepareStatement(sqlInsertCategory)) {
                    for (Category c : product.getCategories()) {
                        ps.setLong(1, productId);
                        ps.setLong(2, c.getCategoryId());
                        ps.executeUpdate();
                    }
                }
            }

            // 5️⃣ Insert Authors
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

    public Product getProductById(long productId) {
        Product product = null;
        String sql = """
                 SELECT p.ProductID, p.ShopID, p.Title, p.Description,
                    p.OriginalPrice, p.SalePrice, p.SoldCount, p.Quantity,
                    pub.Name AS PublisherName, p.PublishedDate,
                    b.Translator, b.Version, b.CoverType, b.Pages, l.LanguageName, b.[Size], b.ISBN,
                    a.AuthorName AS Author
                FROM Products p
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                LEFT JOIN BookDetails b ON p.ProductID = b.ProductID
                LEFT JOIN Languages l ON b.LanguageCode = l.LanguageCode
                LEFT JOIN BookAuthors ba ON p.ProductID = ba.ProductID
                LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID
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

                BookDetail bookDetail = new BookDetail();
                bookDetail.setProductId(product.getProductId());
                bookDetail.setTranslator(rs.getString("Translator"));
                bookDetail.setVersion(rs.getString("Version"));
                bookDetail.setCoverType(rs.getString("CoverType"));
                bookDetail.setPages(rs.getInt("Pages"));
                bookDetail.setSize(rs.getString("Size"));

                product.setBookDetail(bookDetail);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (product != null) {
            List<ProductImage> images = new ArrayList<>();
            String imgSql = "SELECT ImageID, Url FROM ProductImages WHERE ProductID = ? ORDER BY IsPrimary DESC";
            try (Connection cn = DataSourceProvider.get().getConnection()) {
                PreparedStatement ps = cn.prepareStatement(imgSql);
                ps.setLong(1, productId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ProductImage productImages = new ProductImage();
                    productImages.setImageId(rs.getLong("ImageID"));
                    productImages.setProductId(productId);
                    productImages.setUrl(rs.getString("Url"));
                    images.add(productImages);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            product.setImages(images);
        }

        return product;
    }
}