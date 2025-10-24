package com.group01.aurora_demo.admin.dao;

import com.group01.aurora_demo.admin.model.Author;
import com.group01.aurora_demo.admin.model.BookDetail;
import com.group01.aurora_demo.admin.model.Product;
import com.group01.aurora_demo.admin.model.Publisher;
import com.group01.aurora_demo.admin.model.ProductImages;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminProductDAO {

    /**
     * Find products by their status with pagination
     *
     * @param status    Product status (PENDING, ACTIVE, REJECTED)
     * @param page      Current page number (1-based)
     * @param pageSize  Number of items per page
     * @param totalRows Output parameter to return total count of products matching the status
     * @return List of products matching the status
     * @throws SQLException If a database error occurs
     */
    public List<Product> findProductsByStatus(String status, int page, int pageSize, int[] totalRows) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        // Calculate pagination parameters
        int offset = (page - 1) * pageSize;
        
        // First, get the total count
        String countSql = "SELECT COUNT(*) FROM Products WHERE Status = ?";
        
        String querySql = """
            SELECT p.ProductID, p.ShopID, p.Title, p.Description, p.OriginalPrice, 
                   p.SalePrice, p.SoldCount, p.Quantity, p.Status, p.RejectReason,
                   p.CreatedAt, pub.PublisherID, pub.Name as PublisherName,
                   bd.Translator, bd.Version, bd.CoverType, bd.Pages, 
                   bd.LanguageCode, bd.Size, bd.ISBN,
                   img.ImageID, img.Url as ImageUrl, img.IsPrimary
            FROM Products p
            LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
            LEFT JOIN BookDetails bd ON p.ProductID = bd.ProductID
            LEFT JOIN ProductImages img ON p.ProductID = img.ProductID AND img.IsPrimary = 1
            WHERE p.Status = ?
            ORDER BY p.CreatedAt DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
        
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            // Get total count
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setString(1, status);
                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        totalRows[0] = countRs.getInt(1);
                    }
                }
            }
            
            // Get products
            try (PreparedStatement stmt = conn.prepareStatement(querySql)) {
                stmt.setString(1, status);
                stmt.setInt(2, offset);
                stmt.setInt(3, pageSize);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    Map<Long, Product> productMap = new LinkedHashMap<>();
                    
                    while (rs.next()) {
                        long productId = rs.getLong("ProductID");
                        
                        // If we haven't seen this product yet, create a new object
                        if (!productMap.containsKey(productId)) {
                            Product p = new Product();
                            p.setProductId(productId);
                            p.setShopId(rs.getLong("ShopID"));
                            p.setTitle(rs.getString("Title"));
                            p.setDescription(rs.getString("Description"));
                            p.setOriginalPrice(rs.getDouble("OriginalPrice"));
                            p.setSalePrice(rs.getDouble("SalePrice"));
                            p.setSoldCount(rs.getLong("SoldCount"));
                            p.setQuantity(rs.getInt("Quantity"));
                            p.setStatus(rs.getString("Status"));
                            p.setRejectReason(rs.getString("RejectReason"));
                            p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                            
                            // Set publisher
                            Long publisherId = rs.getObject("PublisherID", Long.class);
                            if (publisherId != null) {
                                Publisher publisher = new Publisher();
                                publisher.setPublisherId(publisherId);
                                publisher.setName(rs.getString("PublisherName"));
                                p.setPublisher(publisher);
                            }
                            
                            // Set book details
                            BookDetail bookDetail = new BookDetail();
                            bookDetail.setProductId(productId);
                            bookDetail.setTranslator(rs.getString("Translator"));
                            bookDetail.setVersion(rs.getString("Version"));
                            bookDetail.setCoverType(rs.getString("CoverType"));
                            bookDetail.setPages(rs.getInt("Pages"));
                            bookDetail.setLanguageCode(rs.getString("LanguageCode"));
                            bookDetail.setSize(rs.getString("Size"));
                            bookDetail.setISBN(rs.getString("ISBN"));
                            p.setBookDetail(bookDetail);
                            
                            // Set image
                            String imageUrl = rs.getString("ImageUrl");
                            if (imageUrl != null) {
                                p.setPrimaryImageUrl(imageUrl);
                                
                                ProductImages image = new ProductImages();
                                image.setImageId(rs.getLong("ImageID"));
                                image.setProductId(productId);
                                image.setUrl(imageUrl);
                                image.setPrimary(rs.getBoolean("IsPrimary"));
                                
                                List<ProductImages> images = new ArrayList<>();
                                images.add(image);
                                p.setImages(images);
                            }
                            
                            productMap.put(productId, p);
                        }
                    }
                    
                    products.addAll(productMap.values());
                }
            }
            
            // Load authors for products
            if (!products.isEmpty()) {
                loadAuthorsForProducts(conn, products);
            }
        }
        
        return products;
    }
    
    /**
     * Update product status and optionally set a rejection reason
     *
     * @param productId    Product ID to update
     * @param status       New status value
     * @param rejectReason Rejection reason (can be null if not rejected)
     * @return Number of rows updated
     * @throws SQLException If a database error occurs
     */
    public int updateProductStatus(long productId, String status, String rejectReason) throws SQLException {
        String sql = "UPDATE Products SET Status = ?, RejectReason = ? WHERE ProductID = ?";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            
            if (rejectReason != null && !rejectReason.trim().isEmpty()) {
                stmt.setString(2, rejectReason);
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            
            stmt.setLong(3, productId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Get a list of all product statuses from the database
     *
     * @return List of status values
     * @throws SQLException If a database error occurs
     */
    public List<String> getProductStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT DISTINCT Status FROM Products ORDER BY Status";
        
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                statuses.add(rs.getString("Status"));
            }
        }
        
        return statuses;
    }
    
    /**
     * Load authors for a list of products
     *
     * @param conn     Database connection
     * @param products List of products to load authors for
     * @throws SQLException If a database error occurs
     */
    private void loadAuthorsForProducts(Connection conn, List<Product> products) throws SQLException {
        if (products == null || products.isEmpty()) {
            return;
        }
        
        // Build IN clause with product IDs
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            inClause.append(i == 0 ? "?" : ", ?");
        }
        
        String sql = "SELECT ba.ProductID, a.AuthorID, a.AuthorName " +
                     "FROM BookAuthors ba " +
                     "JOIN Authors a ON ba.AuthorID = a.AuthorID " +
                     "WHERE ba.ProductID IN (" + inClause + ")";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set product IDs as parameters
            for (int i = 0; i < products.size(); i++) {
                stmt.setLong(i + 1, products.get(i).getProductId());
            }
            
            Map<Long, List<Author>> authorsByProduct = new LinkedHashMap<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long productId = rs.getLong("ProductID");
                    
                    Author author = new Author();
                    author.setAuthorId(rs.getLong("AuthorID"));
                    author.setAuthorName(rs.getString("AuthorName"));
                    
                    authorsByProduct.computeIfAbsent(productId, k -> new ArrayList<>()).add(author);
                }
            }
            
            // Assign authors to products
            for (Product product : products) {
                List<Author> authors = authorsByProduct.get(product.getProductId());
                if (authors != null) {
                    product.setAuthors(authors);
                }
            }
        }
    }
}
