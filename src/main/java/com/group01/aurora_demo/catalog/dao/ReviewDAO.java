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
}