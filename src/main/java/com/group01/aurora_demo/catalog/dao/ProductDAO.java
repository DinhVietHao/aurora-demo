package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImages;
import com.group01.aurora_demo.catalog.model.BookDetail;
import com.group01.aurora_demo.catalog.model.Product;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;
import java.sql.Date;
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

        // SQL query: get all products with their primary image (if any)
        String sql = "SELECT p.ProductID, p.ShopID, p.SKU, p.Description, p.Title, p.Price, p.Discount, "
                + "p.Publisher, p.SoldCount, p.Stock, p.CategoryID, p.PublishedDate, "
                + "i.Url AS PrimaryImageUrl "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                // Map ResultSet data into Product object
                product.setProductId(rs.getLong("ProductID"));
                product.setShopId(rs.getLong("ShopID"));
                product.setSku(rs.getString("SKU"));
                product.setTitle(rs.getString("Title"));
                product.setDescription(rs.getString("Description"));
                product.setPrice(rs.getDouble("Price"));

                // Discount can be null → must check before setting
                Double discount = rs.getObject("Discount") != null ? rs.getDouble("Discount") : null;
                product.setDiscount(discount);

                product.setSoldCount(rs.getInt("SoldCount"));
                product.setStock(rs.getInt("Stock"));
                product.setCategoryId(rs.getLong("CategoryID"));
                product.setPublisher(rs.getString("Publisher"));

                // PublishedDate can be null → check before converting
                Date publishedDate = rs.getDate("PublishedDate");
                if (publishedDate != null) {
                    product.setPublishedDate(publishedDate.toLocalDate());
                }

                // Primary image (may be null if product has no main image)
                product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                products.add(product);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return products;
    }

    public Product getProductById(long productId) {
        Product product = null;
        String sql = "SELECT p.ProductID, p.ShopID, p.SKU, p.Title, p.ShortDescription, p.Description, p.Price, p.Discount, p.SoldCount, p.Stock, p.CategoryID, p.Publisher, p.PublishedDate, "
                + "b.Author, b.Translator, b.Version, b.CoverType, b.Pages, b.Language, b.[Size], b.ISBN, i.ImageID, i.Url "
                + "FROM Products p "
                + "JOIN BookDetails b ON p.ProductID = b.ProductID "
                + "JOIN ProductImages i ON p.ProductID = i.ProductID "
                + "WHERE p.ProductID = ?";

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();

            List<ProductImages> images = new ArrayList<>();
            while (rs.next()) {
                if (product == null) {
                    product = new Product();
                    product.setProductId(rs.getLong("ProductID"));
                    product.setShopId(rs.getLong("ShopID"));
                    product.setSku(rs.getString("SKU"));
                    product.setTitle(rs.getString("Title"));
                    product.setShortDescription(rs.getString("ShortDescription"));
                    product.setDescription(rs.getString("Description"));
                    product.setPrice(rs.getDouble("Price"));
                    product.setDiscount(rs.getDouble("Discount"));
                    product.setSoldCount(rs.getInt("SoldCount"));
                    product.setStock(rs.getInt("Stock"));
                    product.setCategoryId(rs.getLong("CategoryID"));
                    product.setPublisher(rs.getString("Publisher"));
                    product.setPublishedDate(rs.getDate("PublishedDate").toLocalDate());

                    BookDetail bookDetail = new BookDetail();
                    bookDetail.setProductId(product.getProductId());
                    bookDetail.setAuthor(rs.getString("Author"));
                    bookDetail.setTranslator(rs.getString("Translator"));
                    bookDetail.setVersion(rs.getString("Version"));
                    bookDetail.setCoverType(rs.getString("CoverType"));
                    bookDetail.setPages(rs.getInt("Pages"));
                    bookDetail.setLanguage(rs.getString("Language"));
                    bookDetail.setSize(rs.getString("Size"));
                    bookDetail.setISBN(rs.getString("ISBN"));

                    product.setBookDetail(bookDetail);
                }

                ProductImages productImages = new ProductImages();
                productImages.setImageId(rs.getLong("ImageID"));
                productImages.setProductId(product.getProductId());
                productImages.setImageUrl(rs.getString("Url"));
                images.add(productImages);
            }

            if (product != null) {
                product.setImages(images);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
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

        // SQL query with pagination using OFFSET / FETCH NEXT
        String sql = "SELECT p.ProductID, p.ShopID, p.SKU, p.Description, p.Title, p.Price, p.Discount, "
                + "p.Publisher, p.SoldCount, p.Stock, p.CategoryID, p.PublishedDate, "
                + "i.Url AS PrimaryImageUrl "
                + "FROM Products p "
                + "LEFT JOIN ProductImages i ON p.ProductID = i.ProductID AND i.IsPrimary = 1 "
                + "ORDER BY p.ProductID "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            // Calculate offset from page number
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    // Map ResultSet data into Product object
                    product.setProductId(rs.getLong("ProductID"));
                    product.setShopId(rs.getLong("ShopID"));
                    product.setSku(rs.getString("SKU"));
                    product.setTitle(rs.getString("Title"));
                    product.setDescription(rs.getString("Description"));
                    product.setPrice(rs.getDouble("Price"));

                    Double discount = rs.getObject("Discount") != null ? rs.getDouble("Discount") : null;
                    product.setDiscount(discount);

                    product.setSoldCount(rs.getInt("SoldCount"));
                    product.setStock(rs.getInt("Stock"));
                    product.setCategoryId(rs.getLong("CategoryID"));
                    product.setPublisher(rs.getString("Publisher"));

                    Date publishedDate = rs.getDate("PublishedDate");
                    if (publishedDate != null) {
                        product.setPublishedDate(publishedDate.toLocalDate());
                    }

                    // Primary image for the product
                    product.setPrimaryImageUrl(rs.getString("PrimaryImageUrl"));

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return products;
    }

}