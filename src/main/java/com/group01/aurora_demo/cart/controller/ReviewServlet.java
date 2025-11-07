package com.group01.aurora_demo.cart.controller;

import com.group01.aurora_demo.cart.service.ReviewImageService;
import com.group01.aurora_demo.catalog.dao.ReviewDAO;
import com.group01.aurora_demo.catalog.model.Review;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;
import jakarta.servlet.http.Part;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.List;

@WebServlet("/review")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 30)
public class ReviewServlet extends HttpServlet {

    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            JSONObject json = new JSONObject();
            PrintWriter out = response.getWriter();

            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;

            if (user == null) {
                json.put("success", false);
                json.put("message", "Bạn cần đăng nhập để thực hiện việc này.");
                out.print(json.toString());
                out.flush();
                return;
            }

            String action = request.getParameter("action");
            switch (action) {
                case "create":
                    createReview(request, response, json, out, user);
                    break;

                case "update":
                    updateReview(request, response, json, out, user);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] cart/controller/ReviewServlet: " + e.getMessage());
        }
    }

    private void createReview(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user) {
        boolean flag = true;
        String message = "";
        try {
            long orderItemId = Long.parseLong(request.getParameter("orderItemId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            String baseUploadDir = getServletContext().getRealPath("/assets/images");
            List<String> savedImageNames = new ArrayList<>();
            List<Part> imageParts = request.getParts().stream()
                    .filter(part -> "reviewImages".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

            if (imageParts.size() > 5) {
                flag = false;
                message = "Bạn chỉ được tải lên tối đa 5 hình ảnh.";
            } else {
                for (Part filePart : imageParts) {
                    String newFilename = ReviewImageService.uploadReviewImage(filePart, baseUploadDir);
                    savedImageNames.add(newFilename);
                }

                Review review = new Review();
                review.setOrderItemId(orderItemId);
                review.setUserId(user.getId());
                review.setRating(rating);
                review.setComment(comment);

                flag = reviewDAO.addReview(review, savedImageNames);
                if (flag) {
                    message = "Đánh giá của bạn đã được gửi thành công!";
                } else {
                    message = "Đã xảy ra lỗi trong quá trình gửi đánh giá.";
                }
            }
        } catch (Exception e) {
            flag = false;
            message = "[ERROR] cart/controller/ReviewServlet createReview: " + e.getMessage();
            System.out.println("[ERROR] cart/controller/ReviewServlet createReview: " + e.getMessage());
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }

    private void updateReview(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user) {
        boolean flag = false;
        String message = "";
        try {
            long reviewId = Long.parseLong(request.getParameter("reviewId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            String baseUploadDir = getServletContext().getRealPath("/assets/images");

            List<String> newSavedImageNames = new ArrayList<>();
            List<Part> imageParts = request.getParts().stream()
                    .filter(part -> "reviewImages".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

            if (imageParts.size() > 5) {
                throw new IllegalArgumentException("Bạn chỉ được tải lên tối đa 5 hình ảnh.");
            }

            Review review = new Review();
            review.setReviewId(reviewId);
            review.setUserId(user.getId());
            review.setRating(rating);
            review.setComment(comment);

            if (imageParts.isEmpty()) {
                flag = reviewDAO.updateReview(reviewId, user.getId(), rating, comment);
                if (flag) {
                    message = "Cập nhật đánh giá thành công!";
                } else {
                    message = "Cập nhật thất bại. Có thể bạn không phải chủ sở hữu.";
                }
            } else {
                for (Part filePart : imageParts) {
                    String newFilename = ReviewImageService.uploadReviewImage(filePart, baseUploadDir);
                    newSavedImageNames.add(newFilename);
                }

                List<String> oldImageFiles = reviewDAO.updateReviewAndReplaceImages(review, newSavedImageNames);
                for (String oldFile : oldImageFiles) {
                    ReviewImageService.deleteOldReviewImage(baseUploadDir, oldFile);
                }

                flag = true;
                message = "Cập nhật đánh giá và hình ảnh thành công!";
            }
        } catch (Exception e) {
            flag = false;
            message = "[ERROR] ReviewServlet updateReview: " + e.getMessage();
            System.out.println(message);
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
            out.flush();
        }
    }
}