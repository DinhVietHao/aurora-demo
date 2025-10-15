package com.group01.aurora_demo.profile.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.ImageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) (session != null ? session.getAttribute("AUTH_USER") : null);
        if (user == null) {
            request.getRequestDispatcher(request.getContextPath() + "/home").forward(request, response);
            return;
        }
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        try {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;
            if (user == null) {
                request.getRequestDispatcher(request.getContextPath() + "/home").forward(request, response);
                return;
            }

            String action = request.getParameter("action");
            switch (action) {
                case "uploadAvatar":
                    handleUploadAvatar(request, response, out, json, user);
                    break;

                case "changePassword":
                    handleChangePassword(request, response, session, out, json, user);
                    break;

                default:
                    json.put("success", false);
                    json.put("message", "Hành động không hợp lệ.");
                    out.print(json.toString());
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] ProfileServlet#doPost: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    private void handleUploadAvatar(HttpServletRequest request, HttpServletResponse response, PrintWriter out,
            JSONObject json, User user)
            throws IOException, ServletException {
        try {
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
            String uploadDir = request.getServletContext().getRealPath("/assets/images/avatars");
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
        } catch (Exception e) {
            System.out.println("[ERROR] ProfileServlet#handleUploadAvatar: " + e.getMessage());
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            PrintWriter out, JSONObject json, User user) throws IOException {
        try {
            Integer wrongCount = (Integer) session.getAttribute("changePwdWrongCount");
            Long lockUntil = (Long) session.getAttribute("changePwdLockUntil");
            long now = System.currentTimeMillis();

            if (lockUntil != null && now < lockUntil) {
                long minutes = (lockUntil - now) / 60000 + 1;
                json.put("success", false);
                json.put("message",
                        "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + minutes + " phút.");
                out.print(json.toString());
                return;
            }

            if (wrongCount == null)
                wrongCount = 0;

            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmNewPassword = request.getParameter("confirmNewPassword");

            if (!newPassword.equals(confirmNewPassword)) {
                json.put("success", false);
                json.put("message", "Mật khẩu xác nhận không khớp.");
                out.print(json.toString());
                return;
            }

            if (newPassword.length() < 8) {
                json.put("success", false);
                json.put("message", "Mật khẩu mới chưa đủ mạnh.");
                out.print(json.toString());
                return;
            }

            if (!userDAO.checkPassword(user.getId(), currentPassword)) {
                wrongCount++;
                session.setAttribute("changePwdWrongCount", wrongCount);

                if (wrongCount >= 12) {
                    session.setAttribute("changePwdLockUntil", now + 60 * 60 * 1000); // 1h
                    json.put("message", "Bạn nhập sai quá nhiều lần. Đã khóa chức năng đổi mật khẩu 1 giờ.");
                } else if (wrongCount >= 10) {
                    session.setAttribute("changePwdLockUntil", now + 30 * 60 * 1000); // 30p
                    json.put("message", "Bạn nhập sai quá nhiều lần. Đã khóa chức năng đổi mật khẩu 30 phút.");
                } else if (wrongCount >= 8) {
                    session.setAttribute("changePwdLockUntil", now + 15 * 60 * 1000); // 15p
                    json.put("message", "Bạn nhập sai quá nhiều lần. Đã khóa chức năng đổi mật khẩu 15 phút.");
                } else if (wrongCount >= 5) {
                    session.setAttribute("changePwdLockUntil", now + 5 * 60 * 1000); // 5p
                    json.put("message", "Bạn nhập sai quá nhiều lần. Đã khóa chức năng đổi mật khẩu 5 phút.");
                } else {
                    json.put("message", "Mật khẩu hiện tại không đúng.");
                }
                json.put("success", false);
                out.print(json.toString());
                return;
            }

            // Nếu đúng mật khẩu cũ, reset số lần sai và thời gian khóa
            session.removeAttribute("changePwdWrongCount");
            session.removeAttribute("changePwdLockUntil");

            boolean result = userDAO.updatePassword(user.getId(), newPassword);
            if (result) {
                json.put("success", true);
                json.put("message", "Đổi mật khẩu thành công.");
            } else {
                json.put("success", false);
                json.put("message", "Đổi mật khẩu thất bại.");
            }
            out.print(json.toString());
        } catch (Exception e) {
            System.out.println("[ERROR] ProfileServlet#handleChangePassword: " + e.getMessage());
        }
    }
}