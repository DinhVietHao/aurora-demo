package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Category;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.catalog.model.Publisher;
import com.group01.aurora_demo.catalog.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
                WHERE pc.CategoryID IN (
                    -- Thể loại mà user đã mua
                    SELECT DISTINCT pc2.CategoryID
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    JOIN ProductCategory pc2 ON oi.ProductID = pc2.ProductID
                    WHERE o.UserID = ? AND o.OrderStatus IN (N'Hoàn thành')

                    UNION

                    -- Thể loại mà user đang có trong giỏ hàng
                    SELECT DISTINCT pc3.CategoryID
                    FROM CartItems ci
                    JOIN ProductCategory pc3 ON ci.ProductID = pc3.ProductID
                    WHERE ci.UserID = ?
                )
                AND p.Status = 'ACTIVE'
                AND p.ProductID NOT IN (
                    -- Loại bỏ những sản phẩm user đã mua hoặc có trong giỏ
                    SELECT oi.ProductID
                    FROM Orders o
                    JOIN OrderShops os ON o.OrderID = os.OrderID
                    JOIN OrderItems oi ON os.OrderShopID = oi.OrderShopID
                    WHERE o.UserID = ?

                    UNION

                    SELECT ci.ProductID
                    FROM CartItems ci
                    WHERE ci.UserID = ?
                )
                LEFT JOIN OrderItems oi2 ON p.ProductID = oi2.ProductID
                LEFT JOIN Reviews r ON oi2.OrderItemID = r.OrderItemID
                LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
                LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                GROUP BY p.ProductID, p.Title, p.SalePrice, p.OriginalPrice,
                         p.SoldCount, img.Url, pub.Name
                ORDER BY p.SoldCount DESC, AvgRating DESC;
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
            // ImageDAO imageDAO = new ImageDAO();
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
                    p.setCreatedAt(rs.getDa("CreatedAt"));
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
                    b.setISBN(rs.getString("ISBN"));
                    p.setBookDetail(b);

                    // Danh sách ảnh (nếu bạn vẫn muốn lấy thêm ảnh phụ)
                    // p.setImages(imageDAO.getProductImages(p.getProductId())); -- Error !!!

                    list.add(p);
                }
            }
        }
        return list;
    }

    public int countProductsByShopId(long shopId) throws SQLException {
        String sql = "SELECT COUNT() FROM Products WHERE ShopID = ?";
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
                    ps.setString(8, bd.getISBN());
                    ps.executeUpdate();
                }
            }

            // 3️⃣ Insert ProductImages (mỗi ảnh 1 executeUpdate) -- Error
            // if (product.getImageUrls() != null) {
            // try (PreparedStatement ps = cn.prepareStatement(sqlInsertProductImage)) {
            // for (int i = 0; i < product.getImageUrls().size(); i++) {
            // ps.setLong(1, productId);
            // ps.setString(2, product.getImageUrls().get(i));
            // ps.setBoolean(3, i == 0);
            // ps.executeUpdate();
            // }
            // }
            // }

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

}