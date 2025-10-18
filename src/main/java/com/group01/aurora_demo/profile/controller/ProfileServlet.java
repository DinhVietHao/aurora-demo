package com.group01.aurora_demo.profile.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.ImageDAO;
import com.group01.aurora_demo.common.service.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private EmailService emailService = new EmailService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) (session != null ? session.getAttribute("AUTH_USER") : null);
        if (user == null) {
            request.getRequestDispatcher(request.getContextPath() + "/home").forward(request, response);
            return;
        }

        // Đọc cooldown từ cookie
        boolean emailChangeLocked = false;
        long remainingMs = 0;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("email_change_lock".equals(cookie.getName())) {
                    try {
                        long lockUntil = Long.parseLong(cookie.getValue());
                        long now = System.currentTimeMillis();

                        if (now < lockUntil) {
                            emailChangeLocked = true;
                            remainingMs = lockUntil - now;
                        } else {
                            // Hết hạn → Xóa cookie
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                        }
                    } catch (NumberFormatException e) {
                        // Cookie lỗi → Xóa
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                    break;
                }
            }
        }

        request.setAttribute("user", user);
        request.setAttribute("emailChangeRemainingMs", remainingMs);
        request.setAttribute("emailChangeLocked", emailChangeLocked);
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
                    handleUploadAvatar(request, response, session, out, json, user);
                    break;

                case "changePassword":
                    handleChangePassword(request, response, session, out, json, user);
                    break;

                case "verifyPassword":
                    String password = request.getParameter("password");
                    json.put("success", userDAO.checkPassword(user.getUserID(), password));
                    out.print(json.toString());
                    break;

                case "changeEmail":
                    handleChangeEmail(request, response, json, out, user, session);
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

    private void handleUploadAvatar(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            PrintWriter out, JSONObject json, User user) throws IOException, ServletException {
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
                user.setAvatarUrl(fileName);
                session.setAttribute("AUTH_USER", user);
                session.setMaxInactiveInterval(60 * 60 * 2);

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

    private void sendEmailChangeNotifications(String oldEmail, String newEmail, String userName) {
        try {
            // Email cũ
            String subjectOld = "⚠️ Thông báo thay đổi email - Aurora";
            String htmlOld = String.format(
                    """
                                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;border:1px solid #ddd;border-radius:8px;">
                                    <h2 style="color:#e74c3c;text-align:center;">⚠️ Email tài khoản đã được thay đổi</h2>
                                    <p>Xin chào <strong>%s</strong>,</p>
                                    <p>Email đăng nhập của tài khoản Aurora đã được thay đổi:</p>
                                    <div style="background:#f8f9fa;padding:15px;border-left:4px solid #e74c3c;margin:20px 0;">
                                    <strong>Email cũ:</strong> %s<br>
                                    <strong>Email mới:</strong> %s
                                    </div>
                                    <p>Nếu <strong>KHÔNG PHẢI BẠN</strong> thực hiện, vui lòng liên hệ ngay:</p>
                                    <ul>
                                    <li>Email: support@aurora.com</li>
                                    <li>Hotline: 1900-xxxx</li>
                                    </ul>
                                    <hr style="margin:20px 0;"/>
                                    <p style="color:#777;font-size:12px;text-align:center;">
                                    Thời gian: %s<br>
                                    Email này được gửi tự động, vui lòng không trả lời.
                                    </p>
                                </div>
                            """,
                    userName, oldEmail, newEmail, new Timestamp(System.currentTimeMillis()));
            emailService.sendHtml(oldEmail, subjectOld, htmlOld);

            // Email mới
            String subjectNew = "✅ Chào mừng bạn với email mới - Aurora";
            String htmlNew = String.format(
                    """
                                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;border:1px solid #ddd;border-radius:8px;">
                                    <h2 style="color:#27ae60;text-align:center;">✅ Email tài khoản đã cập nhật thành công</h2>
                                    <p>Xin chào <strong>%s</strong>,</p>
                                    <p>Email đăng nhập Aurora của bạn đã được thay đổi thành công:</p>
                                    <div style="background:#d4edda;padding:15px;border-left:4px solid #27ae60;margin:20px 0;">
                                    <strong>Email mới:</strong> %s
                                    </div>
                                    <p>Từ giờ, vui lòng sử dụng email này để đăng nhập và nhận thông báo từ Aurora.</p>
                                    <p><strong>Lưu ý:</strong> Bạn có thể đổi email lại sau <strong>7 ngày</strong>.</p>
                                    <hr style="margin:20px 0;"/>
                                    <p style="color:#777;font-size:12px;text-align:center;">
                                    Trân trọng,<br/>Aurora Team
                                    </p>
                                </div>
                            """,
                    userName, newEmail);

            emailService.sendHtml(newEmail, subjectNew, htmlNew);
        } catch (Exception e) {
            System.out.println("[ERROR] sendEmailChangeNotifications: " + e.getMessage());
        }
    }

    private void handleChangeEmail(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user, HttpSession session) {
        boolean flag = true;
        String message = "";
        try {
            String oldEmail = request.getParameter("oldEmail");
            String newEmail = request.getParameter("newEmail");

            if (!newEmail.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
                flag = false;
                message = "Email không hợp lệ.";
            }

            if (flag && userDAO.findByEmailAndProvider(newEmail, "LOCAL") != null) {
                flag = false;
                message = "Email đã được sử dụng. Vui lòng dùng email khác.";
            }

            if (flag) {
                flag = userDAO.updateEmail(user.getUserID(), newEmail);
                message = flag ? "✅ Email đã được thay đổi, vui lòng đăng nhập lại."
                        : "Không thể đổi email. Vui lòng thử lại.";
                if (flag) {
                    // Gửi thông báo
                    sendEmailChangeNotifications(oldEmail, newEmail, user.getFullName());

                    // Đặt khóa 7 ngày (7 * 24h * 60m * 60s * 1000ms)
                    long now = System.currentTimeMillis();
                    long sevenDaysMs = 7L * 24 * 60 * 60 * 1000;
                    long lockUntil = now + sevenDaysMs;

                    Cookie lockCookie = new Cookie("email_change_lock", String.valueOf(lockUntil));
                    lockCookie.setMaxAge(7 * 24 * 60 * 60);
                    lockCookie.setPath("/");
                    lockCookie.setHttpOnly(true);
                    response.addCookie(lockCookie);

                    // Xóa session và cookie remember
                    if (session != null) {
                        session.invalidate();
                    }

                    Cookie cookie = new Cookie("remember_account", "");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleChangeEmail\" function: " + e.getMessage());
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }
}