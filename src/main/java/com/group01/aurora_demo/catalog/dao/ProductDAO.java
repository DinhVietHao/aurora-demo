package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImages;
import com.group01.aurora_demo.catalog.model.Author;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.catalog.model.Publisher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

/**
 * ProductDAO handles database operations related to products.
 * 
 * Author: Phạm Thanh Lượng
 */
public class ProductDAO {

    /**
     * Retrieve all products (including primary image if available).
     *
     * @return list of products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        // SQL: lấy sản phẩm, ảnh chính, nhà xuất bản, và tác giả
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
                + "ORDER BY p.ProductID";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            Long lastProductId = null;
            Product product = null;

            while (rs.next()) {
                Long currentProductId = rs.getLong("ProductID");

                // Nếu sang sản phẩm mới thì tạo object mới
                if (!currentProductId.equals(lastProductId)) {
                    product = new Product();
                    product.setProductId(currentProductId);
                    product.setShopId(rs.getLong("ShopID"));
                    product.setTitle(rs.getString("Title"));
                    product.setDescription(rs.getString("Description"));
                    product.setOriginalPrice(rs.getDouble("OriginalPrice"));
                    product.setSalePrice(rs.getDouble("SalePrice"));
                    product.setSoldCount(rs.getLong("SoldCount"));
                    product.setStock(rs.getInt("Stock"));
                    product.setIsBundle(rs.getBoolean("IsBundle"));
                    product.setCategoryId(rs.getLong("CategoryID"));

                    Date publishedDate = rs.getDate("PublishedDate");
                    if (publishedDate != null) {
                        product.setPublishedDate(publishedDate.toLocalDate());
                    }

                    product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                    // Gán Publisher
                    Long publisherId = (Long) rs.getObject("PublisherID");
                    String publisherName = rs.getString("PublisherName");
                    if (publisherId != null) {
                        Publisher publisher = new Publisher();
                        publisher.setPublisherId(publisherId);
                        publisher.setPublisherName(publisherName);
                        product.setPublisher(publisher);
                    }

                    // Init danh sách authors
                    product.setAuthors(new ArrayList<>());

                    products.add(product);
                    lastProductId = currentProductId;
                }

                // Thêm author nếu có
                Long authorId = (Long) rs.getObject("AuthorID");
                String authorName = rs.getString("AuthorName");
                if (authorId != null && product != null) {
                    Author author = new Author(authorId, authorName);
                    product.getAuthors().add(author);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(long productId) {
        Product product = null;
        String sql = """
                SELECT p.ProductID, p.ShopID, p.Title, p.Description,
                    p.OriginalPrice, p.SalePrice, p.SoldCount, p.Stock, p.IsBundle, p.CategoryID,
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
                product.setStock(rs.getInt("Stock"));
                product.setIsBundle(rs.getBoolean("IsBundle"));
                product.setCategoryId(rs.getLong("CategoryID"));
                product.setPublisherString(rs.getString("PublisherName"));

                if (rs.getDate("PublishedDate") != null) {
                    product.setPublishedDate(rs.getDate("PublishedDate").toLocalDate());
                }
                BookDetail bookDetail = new BookDetail();

                bookDetail.setProductId(product.getProductId());
                bookDetail.setAuthor(rs.getString("Author"));
                bookDetail.setTranslator(rs.getString("Translator"));
                bookDetail.setVersion(rs.getString("Version"));
                bookDetail.setCoverType(rs.getString("CoverType"));
                bookDetail.setPages(rs.getInt("Pages"));
                bookDetail.setLanguage(rs.getString("LanguageName"));
                bookDetail.setSize(rs.getString("Size"));
                bookDetail.setISBN(rs.getString("ISBN"));

                product.setBookDetail(bookDetail);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (product != null) {
            List<ProductImages> images = new ArrayList<>();
            String imgSql = "SELECT ImageID, Url FROM ProductImages WHERE ProductID = ? ORDER BY IsPrimary DESC";
            try (Connection cn = DataSourceProvider.get().getConnection()) {
                PreparedStatement ps = cn.prepareStatement(imgSql);
                ps.setLong(1, productId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ProductImages productImages = new ProductImages();
                    productImages.setImageId(rs.getLong("ImageID"));
                    productImages.setProductId(productId);
                    productImages.setImageUrl(rs.getString("Url"));
                    images.add(productImages);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
                product.setImages(images);
            }

        return product;
    }

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
                        product.setStock(rs.getInt("Stock"));
                        product.setIsBundle(rs.getBoolean("IsBundle"));
                        product.setCategoryId(rs.getLong("CategoryID"));

                        Date publishedDate = rs.getDate("PublishedDate");
                        if (publishedDate != null) {
                            product.setPublishedDate(publishedDate.toLocalDate());
                        }

                        product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                        // Gán Publisher
                        Long publisherId = (Long) rs.getObject("PublisherID");
                        String publisherName = rs.getString("PublisherName");
                        if (publisherId != null) {
                            Publisher publisher = new Publisher();
                            publisher.setPublisherId(publisherId);
                            publisher.setPublisherName(publisherName);
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

}