package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImage;
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
import java.io.IOException;

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

    public List<String> getListImageUrlsByProductId(long productId) throws SQLException {
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

    public List<String> handleImageUpload(HttpServletRequest request) throws Exception {
        Collection<Part> parts = request.getParts();
        List<String> imageNames = new ArrayList<>();

        String uploadDir = request.getServletContext().getRealPath("/assets/images/catalog/products");
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists())
            uploadDirFile.mkdirs();

        for (Part part : parts) {
            if (part.getName().equals("ProductImages") && part.getSize() > 0) {

                if (part.getSize() > 5 * 1024 * 1024) {
                    throw new ServletException("Ảnh '" + part.getSubmittedFileName() + "' vượt 5MB.");
                }

                String originalFileName = part.getSubmittedFileName();
                String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                String fileName = System.currentTimeMillis() + "_" + sanitizedFileName;

                String fullPath = uploadDir + File.separator + fileName;
                part.write(fullPath);
                imageNames.add(fileName);
            }
        }

        if (imageNames.size() < 2 || imageNames.size() > 20) {
            throw new ServletException("Cần tải lên từ 2 đến 20 ảnh sản phẩm.");
        }

        return imageNames;
    }
    
    public String uploadAvatar(Part filePart, String uploadDir) throws IOException, ServletException {
        
        String originalFileName = filePart.getSubmittedFileName();
        String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String fileName = System.currentTimeMillis() + "_" + sanitizedFileName;

        String fullPath = uploadDir + File.separator + fileName;
        filePart.write(fullPath);

        
        return fileName;
    }
}