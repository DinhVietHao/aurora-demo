package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImages;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Data Access Object (DAO) for the ProductImages table. Provides methods to
 * work with product image data.
 *
 * Author: Phạm Thanh Lượng
 */
public class ImageDAO {

    /**
     * Retrieve the primary image (IsPrimary = 1) for a given product.
     *
     * @param productID ID of the product
     * @return ProductImages object containing the primary image info, or null
     *         if not found
     */
    public ProductImages getImagesByProductId(int productID) {
        // SQL: select primary image by ProductID
        String sql = "SELECT ImageID, ProductID, Url, IsPrimary "
                + "FROM ProductImages "
                + "WHERE ProductID = ? AND IsPrimary = 1";

        ProductImages img = null;
        try (Connection cn = DataSourceProvider.get().getConnection(); // Get DB connection
                PreparedStatement ps = cn.prepareStatement(sql)) { // Prepare statement

            ps.setInt(1, productID); // Bind productID parameter

            ResultSet rs = ps.executeQuery(); // Execute query

            if (rs.next()) {
                img = new ProductImages();
                // Map ResultSet data into ProductImages object
                img.setImageId(rs.getLong("ImageID"));
                img.setProductId(rs.getLong("ProductID"));
                img.setImageUrl(rs.getString("Url")); // Image URL
                img.setPrimary(rs.getBoolean("IsPrimary")); // true if primary image
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return img;
    }
}