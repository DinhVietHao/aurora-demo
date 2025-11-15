package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.model.ReviewImage;
import com.group01.aurora_demo.catalog.dao.ReviewDAO;
import com.group01.aurora_demo.catalog.model.Review;
import jakarta.servlet.annotation.WebServlet;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.PrintWriter;
import org.json.JSONArray;
import java.util.List;

@WebServlet("/api/reviews")
public class ReviewAPIServlet extends HttpServlet {

    private ReviewDAO reviewDAO = new ReviewDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject responseData = new JSONObject();
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String productIdParam = request.getParameter("productId");
            String pageParam = request.getParameter("page");
            String limitParam = request.getParameter("limit");
            if (productIdParam == null || pageParam == null || limitParam == null) {
                responseData.put("success", false);
                responseData.put("message", "Missing required parameters");
                out.print(responseData.toString());
                return;
            }

            long productId = Long.parseLong(productIdParam);
            int page = Integer.parseInt(pageParam);
            int limit = Integer.parseInt(limitParam);
            if (productId <= 0 || page <= 0 || limit <= 0 || limit > 100) {
                responseData.put("success", false);
                responseData.put("message", "Invalid parameter values");
                out.print(responseData.toString());
                return;
            }

            String ratingParam = request.getParameter("rating");
            String filterParam = request.getParameter("filter");

            Integer filterRating = null;
            Boolean hasComment = null;
            Boolean hasImage = null;
            if (ratingParam != null && !ratingParam.equals("all")) {
                try {
                    filterRating = Integer.parseInt(ratingParam);
                    if (filterRating < 1 || filterRating > 5) {
                        filterRating = null;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[ERROR - Line 69] ReviewAPIServlet: " + e.getMessage());
                }
            }

            if ("comment".equals(filterParam)) {
                hasComment = true;
            } else if ("image".equals(filterParam)) {
                hasImage = true;
            }

            int offset = (page - 1) * limit;

            List<Review> reviews = reviewDAO.getReviewsByProductIdWithFilter(
                    productId, offset, limit, filterRating, hasComment, hasImage);

            int totalReviews = reviewDAO.countReviewsByProductIdWithFilter(
                    productId, filterRating, hasComment, hasImage);

            int totalPages = (int) Math.ceil((double) totalReviews / limit);

            responseData.put("success", true);
            responseData.put("reviews", convertReviewsToJSON(reviews));
            responseData.put("currentPage", page);
            responseData.put("totalPages", totalPages);
            responseData.put("totalReviews", totalReviews);

            out.print(responseData.toString());
        } catch (NumberFormatException e) {
            responseData.put("success", false);
            responseData.put("message", "Invalid number format: " + e.getMessage());
            if (out != null)
                out.print(responseData.toString());
        } catch (Exception e) {
            System.err.println("[ERROR - Line 101] ReviewAPIServlet: " + e.getMessage());
            responseData.put("success", false);
            responseData.put("message", "Internal server error: " + e.getMessage());
            if (out != null)
                out.print(responseData.toString());
        }
    }

    private JSONArray convertReviewsToJSON(List<Review> reviews) {
        JSONArray reviewsArray = new JSONArray();
        for (Review review : reviews) {
            try {
                JSONObject reviewObj = new JSONObject();
                reviewObj.put("reviewId", review.getReviewId());
                reviewObj.put("rating", review.getRating());
                reviewObj.put("comment", review.getComment() != null ? review.getComment() : "");
                reviewObj.put("createdAt", dateFormat.format(review.getCreatedAt()));

                JSONObject userObj = new JSONObject();
                userObj.put("userId", review.getUser().getUserID());
                userObj.put("fullName", review.getUser().getFullName());
                userObj.put("avatarUrl", review.getUser().getAvatarUrl() != null
                        ? review.getUser().getAvatarUrl()
                        : "");
                reviewObj.put("user", userObj);

                JSONArray imagesArray = new JSONArray();
                if (review.getImages() != null) {
                    for (ReviewImage img : review.getImages()) {
                        JSONObject imgObj = new JSONObject();
                        imgObj.put("imageId", img.getReviewImageId());
                        imgObj.put("url", img.getUrl());
                        imagesArray.put(imgObj);
                    }
                }
                reviewObj.put("images", imagesArray);
                reviewsArray.put(reviewObj);
            } catch (Exception e) {
                System.err.println("[ERROR] Converting review to JSON: " + e.getMessage());
            }
        }
        return reviewsArray;
    }
}