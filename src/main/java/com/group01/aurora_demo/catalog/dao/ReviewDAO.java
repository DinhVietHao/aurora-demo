package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.catalog.model.Review;
import com.group01.aurora_demo.catalog.model.ReviewImage;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.common.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Review> getReviewsByProductId(long productId, int offset, int limit) {
        List<Review> reviews = new ArrayList<>();
        String sql = """
                    SELECT
                        r.ReviewID,
                        r.Rating,
                        r.Comment,
                        r.CreatedAt,
                        u.UserID,
                        u.FullName,
                        u.AvatarUrl
                    FROM Reviews r
                    INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                    INNER JOIN Users u ON r.UserID = u.UserID
                    WHERE oi.ProductID = ?
                    ORDER BY r.CreatedAt DESC
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getLong("ReviewID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setCreatedAt(rs.getTimestamp("CreatedAt"));

                User user = new User();
                user.setId(rs.getLong("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setAvatarUrl(rs.getString("AvatarUrl"));
                review.setUser(user);

                review.setImages(getReviewImages(review.getReviewId()));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Error in \"getReviewsByProductId\" method of ReviewDAO: " + e.getMessage());
        }
        return reviews;
    }

    public List<Review> getReviewsByProductIdWithFilter(
            long productId,
            int offset,
            int limit,
            Integer rating,
            Boolean hasComment,
            Boolean hasImage) {

        List<Review> reviews = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        r.ReviewID,
                        r.OrderItemID,
                        r.UserID,
                        r.Rating,
                        r.Comment,
                        r.CreatedAt,
                        u.FullName,
                        u.AvatarUrl
                    FROM Reviews r
                    INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                    INNER JOIN Users u ON r.UserID = u.UserID
                    WHERE oi.ProductID = ?
                """);

        if (rating != null && rating >= 1 && rating <= 5) {
            sql.append(" AND r.Rating = ?");
        }

        if (hasComment != null && hasComment) {
            sql.append(" AND r.Comment IS NOT NULL AND r.Comment <> ''");
        }

        if (hasImage != null && hasImage) {
            sql.append(" AND EXISTS (SELECT 1 FROM ReviewImages ri WHERE ri.ReviewID = r.ReviewID)");
        }

        sql.append(" ORDER BY r.CreatedAt DESC");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql.toString());
            int paramIndex = 1;
            ps.setLong(paramIndex++, productId);

            if (rating != null && rating >= 1 && rating <= 5) {
                ps.setInt(paramIndex++, rating);
            }

            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getLong("ReviewID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setCreatedAt(rs.getTimestamp("CreatedAt"));

                User user = new User();
                user.setUserID(rs.getLong("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setAvatarUrl(rs.getString("AvatarUrl"));
                review.setUser(user);

                review.setImages(getReviewImages(review.getReviewId()));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("Error in \"getReviewsByProductIdWithFilter\" function: " + e.getMessage());
        }
        return reviews;
    }

    public int countReviewsByProductIdWithFilter(
            long productId,
            Integer rating,
            Boolean hasComment,
            Boolean hasImage) {

        StringBuilder sql = new StringBuilder("""
                    SELECT COUNT(DISTINCT r.ReviewID)
                    FROM Reviews r
                    INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                    WHERE oi.ProductID = ?
                """);

        if (rating != null && rating >= 1 && rating <= 5) {
            sql.append(" AND r.Rating = ?");
        }

        if (hasComment != null && hasComment) {
            sql.append(" AND r.Comment IS NOT NULL AND r.Comment <> ''");
        }

        if (hasImage != null && hasImage) {
            sql.append(" AND EXISTS (SELECT 1 FROM ReviewImages ri WHERE ri.ReviewID = r.ReviewID)");
        }

        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql.toString());
            int paramIndex = 1;
            ps.setLong(paramIndex++, productId);

            if (rating != null && rating >= 1 && rating <= 5) {
                ps.setInt(paramIndex++, rating);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in \"countReviewsByProductIdWithFilter\" function: " + e.getMessage());
        }
        return 0;
    }

    private List<ReviewImage> getReviewImages(long reviewId) {
        List<ReviewImage> images = new ArrayList<>();
        String sql = """
                    SELECT ReviewImageID, Url, Caption, IsPrimary
                    FROM ReviewImages
                    WHERE ReviewID = ?
                    ORDER BY IsPrimary DESC, ReviewImageID
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, reviewId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReviewImage img = new ReviewImage();
                img.setReviewImageId(rs.getLong("ReviewImageID"));
                img.setUrl(rs.getString("Url"));
                img.setCaption(rs.getString("Caption"));
                img.setPrimary(rs.getBoolean("IsPrimary"));
                images.add(img);
            }
        } catch (SQLException e) {
            System.out.println("Error in \"getReviewImages\" method of ReviewDAO: " + e.getMessage());
        }
        return images;
    }

    public int countReviewsByProductId(long productId) {
        String sql = """
                    SELECT COUNT(DISTINCT r.ReviewID)
                    FROM Reviews r
                    INNER JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                    WHERE oi.ProductID = ?
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error in \"countReviewsByProductId\" method of ReviewDAO: " + e.getMessage());
        }
        return 0;
    }

    public boolean addReview(Review review, List<String> imageUrls) throws SQLException {
        String sqlReview = "INSERT INTO Reviews (OrderItemID, UserID, Rating, Comment, CreatedAt) " +
                "VALUES (?, ?, ?, ?, CAST(DATEADD(HOUR, 7, SYSDATETIMEOFFSET()) AS DATETIME2))";

        String sqlImage = "INSERT INTO ReviewImages (ReviewID, Url, IsPrimary, CreatedAt) " +
                "VALUES (?, ?, ?, CAST(DATEADD(HOUR, 7, SYSDATETIMEOFFSET()) AS DATETIME2))";

        Connection conn = null;
        PreparedStatement psReview = null;
        PreparedStatement psImage = null;
        ResultSet generatedKeys = null;

        try {
            conn = DataSourceProvider.get().getConnection();
            conn.setAutoCommit(false);

            psReview = conn.prepareStatement(sqlReview, Statement.RETURN_GENERATED_KEYS);
            psReview.setLong(1, review.getOrderItemId());
            psReview.setLong(2, review.getUserId());
            psReview.setInt(3, review.getRating());
            psReview.setString(4, review.getComment());

            if (psReview.executeUpdate() == 0) {
                throw new SQLException("Thêm review thất bại, không có dòng nào được chèn.");
            }

            long reviewId = 0;
            generatedKeys = psReview.getGeneratedKeys();
            if (generatedKeys.next()) {
                reviewId = generatedKeys.getLong(1);
            } else {
                throw new SQLException("Không lấy được ReviewID vừa tạo.");
            }

            if (imageUrls != null && !imageUrls.isEmpty()) {
                psImage = conn.prepareStatement(sqlImage);

                for (int i = 0; i < imageUrls.size(); i++) {
                    psImage.setLong(1, reviewId);
                    psImage.setString(2, imageUrls.get(i));
                    psImage.setBoolean(3, (i == 0));
                    psImage.addBatch();
                }
                psImage.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println(
                            "[ERROR] catalog/dao/ReviewDAO addReview have exception SQL (1): " + ex.getMessage());
                }
            }
            System.out.println("[ERROR] catalog/dao/ReviewDAO addReview have exception SQL (2): " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null)
                generatedKeys.close();
            if (psReview != null)
                psReview.close();
            if (psImage != null)
                psImage.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean updateReview(long reviewId, long userId, int rating, String comment) {
        String sql = """
                UPDATE Reviews
                SET Rating = ?, Comment = ?, CreatedAt = ?
                WHERE ReviewID = ? AND UserID = ?
                """;
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            Timestamp vietnamNow = new Timestamp(System.currentTimeMillis());
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setTimestamp(3, vietnamNow);
            ps.setLong(4, reviewId);
            ps.setLong(5, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[ERROR] catalog/dao/ReviewDAO updateReview: " + e.getMessage());
            return false;
        }
    }

    public List<String> getOldImageUrls(Connection conn, long reviewId) throws SQLException {
        List<String> urls = new ArrayList<>();
        String sql = "SELECT Url FROM ReviewImages WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    urls.add(rs.getString("Url"));
                }
            }
        }
        return urls;
    }

    private void deleteOldImagesFromDB(Connection conn, long reviewId) throws SQLException {
        String sql = "DELETE FROM ReviewImages WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reviewId);
            ps.executeUpdate();
        }
    }

    private void insertNewImagesToDB(Connection conn, long reviewId, List<String> newImageUrls, Timestamp timestamp)
            throws SQLException {
        String sql = "INSERT INTO ReviewImages (ReviewID, Url, IsPrimary, CreatedAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < newImageUrls.size(); i++) {
                ps.setLong(1, reviewId);
                ps.setString(2, newImageUrls.get(i));
                ps.setBoolean(3, (i == 0));
                ps.setTimestamp(4, timestamp);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<String> updateReviewAndReplaceImages(Review review, List<String> newImageUrls) throws SQLException {
        Connection conn = null;
        List<String> oldImageUrls = new ArrayList<>();
        Timestamp vietnamNow = new Timestamp(System.currentTimeMillis());

        try {
            conn = DataSourceProvider.get().getConnection();
            conn.setAutoCommit(false);

            oldImageUrls = getOldImageUrls(conn, review.getReviewId());

            String sqlReview = "UPDATE Reviews SET Rating = ?, Comment = ?, CreatedAt = ? WHERE ReviewID = ? AND UserID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlReview)) {
                ps.setInt(1, review.getRating());
                ps.setString(2, review.getComment());
                ps.setTimestamp(3, vietnamNow);
                ps.setLong(4, review.getReviewId());
                ps.setLong(5, review.getUserId());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Cập nhật thất bại, ReviewID hoặc UserID không khớp.");
                }
            }

            deleteOldImagesFromDB(conn, review.getReviewId());

            if (newImageUrls != null && !newImageUrls.isEmpty()) {
                insertNewImagesToDB(conn, review.getReviewId(), newImageUrls, vietnamNow);
            }

            conn.commit();
            return oldImageUrls;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            System.out.println("[ERROR] catalog/dao/ReviewDAO updateReviewAndReplaceImages: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}