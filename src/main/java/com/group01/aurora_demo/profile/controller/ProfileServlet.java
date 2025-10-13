package com.group01.aurora_demo.profile.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.ImageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("AUTH_USER");
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject json = new JSONObject();

            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("AUTH_USER");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            String action = request.getParameter("action");
            UserDAO userDAO = new UserDAO();
            switch (action) {
                case "uploadAvatar":
                    Part filePart = request.getPart("avatarCustomer");
                    if (filePart == null || filePart.getSize() == 0) {
                        json.put("success", false);
                        json.put("message", "Ảnh không tồn tại hoặc chưa chọn ảnh.");
                        out.print(json.toString());
                        return;
                    }
                    if (filePart.getSize() > 5 * 1024 * 1024) {
                        json.put("success", false);
                        json.put("message", "Ảnh vượt quá dung lượng cho phép (5MB).");
                        out.print(json.toString());
                        return;
                    }

                    String contentType = filePart.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        json.put("success", false);
                        json.put("message", "Tệp tải lên không phải hình ảnh hợp lệ.");
                        out.print(json.toString());
                        return;
                    }
                    String uploadDir = request.getServletContext().getRealPath("/assets/images/catalog/avatars");
                    File uploadDirFile = new File(uploadDir);
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();

                    }
                    ImageDAO imageDAO = new ImageDAO();
                    String fileName = imageDAO.uploadAvatar(filePart, uploadDir);

                    if (userDAO.updateAvatarCustomer(user.getId(), fileName)) {
                        json.put("success", true);
                        json.put("message", "Upload avatar thành công.");
                    } else {
                        json.put("success", false);
                        json.put("message", "Upload avatar thất bại.");
                    }
                    out.print(json.toString());
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}