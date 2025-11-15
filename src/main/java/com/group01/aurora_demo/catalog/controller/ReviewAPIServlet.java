package com.group01.aurora_demo.catalog.controller;

import com.group01.aurora_demo.catalog.model.ReviewImage;
import com.group01.aurora_demo.catalog.dao.ReviewDAO;
import com.group01.aurora_demo.catalog.model.Review;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.PrintWriter;
import org.json.JSONArray;
import java.util.List;

@WebServlet("/api/reviews")
public class ReviewAPIServlet extends HttpServlet {

    private ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

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
                    System.err.println("[ERROR] ReviewAPIServlet.doGet: Invalid rating parameter - " + e.getMessage());
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
            User currentUser = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;
            Long currentUserId = (currentUser != null) ? currentUser.getId() : null;

            responseData.put("success", true);
            responseData.put("reviews", convertReviewsToJSON(reviews, currentUserId));
            responseData.put("currentPage", page);
            responseData.put("totalPages", totalPages);
            responseData.put("totalReviews", totalReviews);

            out.print(responseData.toString());

        } catch (NumberFormatException e) {
            System.err.println("[ERROR] ReviewAPIServlet.doGet: Number format error - " + e.getMessage());
            responseData.put("success", false);
            responseData.put("message", "Invalid number format: " + e.getMessage());
            if (out != null)
                out.print(responseData.toString());
        } catch (Exception e) {
            System.err.println("[ERROR] ReviewAPIServlet.doGet: Server error - " + e.getMessage());
            responseData.put("success", false);
            responseData.put("message", "Internal server error: " + e.getMessage());
            if (out != null)
                out.print(responseData.toString());
        }
    }

    private JSONArray convertReviewsToJSON(List<Review> reviews, Long currentUserId) {
        JSONArray reviewsArray = new JSONArray();
        for (Review review : reviews) {
            try {
                JSONObject reviewObj = new JSONObject();
                reviewObj.put("reviewId", review.getReviewId());
                reviewObj.put("rating", review.getRating());
                reviewObj.put("comment", review.getComment() != null ? review.getComment() : "");
                reviewObj.put("createdAt", review.getCreatedAt().getTime());

                boolean isOwner = (currentUserId != null &&
                        currentUserId.equals(review.getUser().getId()));
                reviewObj.put("isOwner", isOwner);

                JSONObject userObj = new JSONObject();
                userObj.put("id", review.getUser().getUserID());
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
                System.err.println("[ERROR] ReviewAPIServlet.convertReviewsToJSON: Error converting review "
                        + review.getReviewId() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        return reviewsArray;
    }
}