package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.ProductImage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import java.sql.PreparedStatement;
import jakarta.servlet.http.Part;
import java.sql.SQLException;
import java.sql.Statement;
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
            // Accept both create and update input names (case-insensitive) or any part that
            // contains 'productimages'
            String pname = part.getName() == null ? "" : part.getName().toLowerCase();
            if (pname.contains("productimages") && part.getSize() > 0) {

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

        // Do not enforce total count here: caller (create/update) should validate based
        // on context.

        return imageNames;
    }

    public void deleteImageById(long imageId) throws SQLException {
        String sql = "DELETE FROM ProductImages WHERE ImageID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, imageId);
            ps.executeUpdate();
        }
    }

    public String uploadAvatar(Part filePart, String uploadDir) throws IOException, ServletException {

        String originalFileName = filePart.getSubmittedFileName();
        String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String fileName = System.currentTimeMillis() + "_" + sanitizedFileName;

        String fullPath = uploadDir + File.separator + fileName;
        filePart.write(fullPath);

        return fileName;
    }

    public void deleteAuthorsByProductId(long productId) throws SQLException {
        String sql = "DELETE FROM BookAuthors WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        }
    }

    public long insertImage(long productId, String url, boolean isPrimary) throws SQLException {
        String sql = "INSERT INTO ProductImages (ProductID, Url, IsPrimary) VALUES (?, ?, ?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, productId);
            ps.setString(2, url);
            ps.setBoolean(3, isPrimary);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        }
        return -1;
    }

    public void deleteImageByUrl(String url) throws SQLException {
        String sql = "DELETE FROM ProductImages WHERE Url = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.executeUpdate();
        }
    }

    public void resetAllPrimaryImages(long productId) throws SQLException {
        String sql = "UPDATE ProductImages SET IsPrimary = 0 WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        }
    }

    public void updatePrimaryImageById(long productId, long imageId) throws SQLException {
        String sql = "UPDATE ProductImages SET IsPrimary = 1 WHERE ProductID = ? AND ImageID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setLong(2, imageId);
            ps.executeUpdate();
        }
    }

    public void updatePrimaryImage(long productId, String imageUrl) throws SQLException {
        String sql = "UPDATE ProductImages SET IsPrimary = 1 WHERE ProductID = ? AND Url = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setString(2, imageUrl);
            ps.executeUpdate();
        }
    }

    public List<ProductImage> getImagesByProductId(long productId) throws SQLException {
        List<ProductImage> list = new ArrayList<>();
        String sql = "SELECT ImageID, ProductID, Url, IsPrimary FROM ProductImages WHERE ProductID = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductImage img = new ProductImage();
                img.setImageId(rs.getLong("ImageID"));
                img.setProductId(rs.getLong("ProductID"));
                img.setUrl(rs.getString("Url"));
                img.setIsPrimary(rs.getBoolean("IsPrimary"));
                list.add(img);
            }
        }
        return list;
    }

}