package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class ProductDAO {

    private Product mapToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getLong("ProductID"));
        p.setShopId(rs.getLong("ShopID"));
        p.setTitle(rs.getString("Title"));
        p.setDescription(rs.getString("Description"));
        p.setOriginalPrice(rs.getDouble("OriginalPrice"));
        p.setSalePrice(rs.getDouble("SalePrice"));
        p.setSoldCount(rs.getLong("SoldCount"));
        p.setStock(rs.getInt("Stock"));
        p.setStatus(rs.getString("Status"));
        p.setPublishedDate(rs.getDate("PublishedDate") != null ? rs.getDate("PublishedDate").toLocalDate() : null);
        p.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));
        p.setAvgRating(rs.getDouble("AvgRating"));

        Publisher pub = new Publisher();
        pub.setPublisherName(rs.getString("PublisherName"));
        p.setPublisher(pub);

        return p;
    }

    public List<Product> getSuggestedProductsByUser(Long userId) {
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

}