package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.catalog.model.ProductImage;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import java.sql.PreparedStatement;
import jakarta.servlet.http.Part;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;
import java.io.File;

public class ImageDAO {

    public ProductImage getImagesByProductId(int productID) {
        String sql = "SELECT ImageID, ProductID, Url, IsPrimary "
                + "FROM ProductImages "
                + "WHERE ProductID = ? AND IsPrimary = 1";

        ProductImage img = null;
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, productID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                img = new ProductImage();
                img.setImageId(rs.getLong("ImageID"));
                img.setProductId(rs.getLong("ProductID"));
                img.setUrl(rs.getString("Url"));
                img.setIsPrimary(rs.getBoolean("IsPrimary"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return img;
    }

    public List<ProductImage> getProductImages(long productId) throws SQLException {
        List<ProductImage> images = new ArrayList<>();
        String sql = "SELECT ImageID, ProductID, Url, IsPrimary FROM ProductImages WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductImage pi = new ProductImage();
                    pi.setImageId(rs.getLong("ImageID"));
                    pi.setProductId(rs.getLong("ProductID"));
                    pi.setUrl(rs.getString("Url"));
                    pi.setIsPrimary(rs.getBoolean("IsPrimary"));
                    images.add(pi);
                }
            }
        }
        return images;
    }

    public List<ProductImage> handleImageUpload(HttpServletRequest request, long productId) throws Exception {
        Collection<Part> parts = request.getParts();
        List<ProductImage> productImages = new ArrayList<>();

        String uploadDir = request.getServletContext().getRealPath("/assets/images/catalog/products");
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists())
            uploadDirFile.mkdirs();

        boolean isFirst = true;
        for (Part part : parts) {
            if (part.getName().equals("ProductImages") && part.getSize() > 0) {
                if (part.getSize() > 5 * 1024 * 1024) {
                    throw new ServletException("Ảnh '" + part.getSubmittedFileName() + "' vượt 5MB.");
                }
                String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                String fullPath = uploadDir + File.separator + fileName;
                part.write(fullPath);

                ProductImage pi = new ProductImage();
                pi.setProductId(productId);
                pi.setUrl(fileName); // Hoặc fullPath nếu cần đường dẫn đầy đủ
                pi.setIsPrimary(isFirst); // Giả sử ảnh đầu tiên là primary
                isFirst = false;
                productImages.add(pi);
            }
        }

        if (productImages.size() < 2 || productImages.size() > 20) {
            throw new ServletException("Cần tải lên từ 2 đến 20 ảnh sản phẩm.");
        }

        return productImages;
    }

}