package com.group01.aurora_demo.profile.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.io.IOException;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.NotificationDAO;
import com.group01.aurora_demo.catalog.model.Notification;

import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.dao.UserDAO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import com.group01.aurora_demo.common.service.AvatarService;
import com.group01.aurora_demo.common.service.EmailService;

@MultipartConfig
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private EmailService emailService = new EmailService();
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) (session != null ? session.getAttribute("AUTH_USER") : null);
            if (user == null) {
                request.getRequestDispatcher(request.getContextPath() + "/home").forward(request, response);
                return;
            }

            String action = request.getParameter("action");
            if (action == null)
                action = "profile";
            switch (action) {
                default:
                    handleProfileView(request, response, user);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in ProfileServlet: " + e.getMessage());
        }
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

    private void handleProfileView(HttpServletRequest request, HttpServletResponse response, User user) {
        try {
            // ===== Email change cooldown =====
            String cookieName = "email_change_lock_" + user.getId();
            boolean emailChangeLocked = false;
            long remainingMs = 0;

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
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

            // ===== Password change lockout =====
            String pwdCookiePrefix = "pwd_lock_" + user.getId();
            boolean passwordChangeLocked = false;
            long passwordRemainingMs = 0;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ((pwdCookiePrefix + "_until").equals(cookie.getName())) {
                        try {
                            long lockUntil = Long.parseLong(cookie.getValue());
                            long now = System.currentTimeMillis();

                            if (now < lockUntil) {
                                passwordChangeLocked = true;
                                passwordRemainingMs = lockUntil - now;
                            } else {
                                // Hết hạn → Xóa cookie lockUntil
                                cookie.setMaxAge(0);
                                cookie.setPath("/");
                                response.addCookie(cookie);
                            }
                        } catch (NumberFormatException e) {
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
            request.setAttribute("passwordChangeLocked", passwordChangeLocked);
            request.setAttribute("passwordChangeRemainingMs", passwordRemainingMs);
            request.getRequestDispatcher("/WEB-INF/views/customer/profile/profile.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("Error in \"handleProfileView\" function of ProfileServlet: " + e.getMessage());
        }
    }

    private void handleUploadAvatar(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            PrintWriter out, JSONObject json, User user) throws IOException, ServletException {
        try {
            Part filePart = request.getPart("avatarCustomer");
            String uploadDir = request.getServletContext().getRealPath("/assets/images/avatars");
            String newFilename = AvatarService.uploadAvatar(filePart, uploadDir, "customer");
            String oldAvatar = user.getAvatarUrl();

            if (userDAO.updateAvatarCustomer(user.getId(), newFilename)) {
                if (oldAvatar != null && !oldAvatar.isEmpty()) {
                    AvatarService.deleteOldAvatar(uploadDir, oldAvatar);
                }

                user.setAvatarUrl(newFilename);
                session.setAttribute("AUTH_USER", user);
                session.setMaxInactiveInterval(60 * 60 * 2);

                json.put("success", true);
                json.put("message", "Cập nhật avatar thành công.");
                json.put("avatarUrl", request.getContextPath() + "/assets/images/avatars/" + newFilename);
            } else {
                AvatarService.deleteOldAvatar(uploadDir, newFilename);
                json.put("success", false);
                json.put("message", "Không thể cập nhật avatar. Vui lòng thử lại.");
            }
            out.print(json.toString());
        } catch (IllegalArgumentException e) {
            json.put("success", false);
            json.put("message", e.getMessage());
            out.print(json.toString());
        } catch (Exception e) {
            System.err.println("[ERROR] ProfileServlet#handleUploadAvatar: " + e.getMessage());
            json.put("success", false);
            json.put("message", "Đã xảy ra lỗi. Vui lòng thử lại.");
            out.print(json.toString());
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            PrintWriter out, JSONObject json, User user) throws IOException {
        try {
            String cookiePrefix = "pwd_lock_" + user.getId();
            Integer wrongCount = null;
            Long lockUntil = null;
            Integer lockLevel = null;

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ((cookiePrefix + "_count").equals(cookie.getName())) {
                        wrongCount = Integer.parseInt(cookie.getValue());
                    } else if ((cookiePrefix + "_until").equals(cookie.getName())) {
                        lockUntil = Long.parseLong(cookie.getValue());
                    } else if ((cookiePrefix + "_level").equals(cookie.getName())) {
                        lockLevel = Integer.parseInt(cookie.getValue());
                    }
                }
            }

            long now = System.currentTimeMillis();

            // Kiểm tra đang bị khóa
            if (lockUntil != null && now < lockUntil) {
                long secondsLeft = (lockUntil - now) / 1000;
                long minutesLeft = secondsLeft / 60 + 1;
                long hoursLeft = minutesLeft / 60;

                String timeMsg;
                if (hoursLeft > 0) {
                    timeMsg = hoursLeft + " giờ";
                } else {
                    timeMsg = minutesLeft + " phút";
                }

                json.put("success", false);
                json.put("message", "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeMsg + ".");
                json.put("locked", true);
                out.print(json.toString());
                return;
            }

            if (wrongCount == null)
                wrongCount = 0;
            if (lockLevel == null)
                lockLevel = 0;

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
                json.put("message", "Mật khẩu mới phải có ít nhất 8 ký tự.");
                out.print(json.toString());
                return;
            }

            if (!userDAO.checkPassword(user.getId(), currentPassword)) {
                wrongCount++;

                boolean shouldLock = false;
                int lockDurationMinutes = 0;
                String lockMessage = "";

                // Logic khóa theo level (giữ nguyên)
                if (lockLevel == 0) {
                    if (wrongCount >= 5) {
                        shouldLock = true;
                        lockDurationMinutes = 5;
                        lockLevel = 1;
                        lockMessage = "⛔ Bạn đã nhập sai " + wrongCount
                                + " lần. Đã khóa chức năng đổi mật khẩu 5 phút.";
                    } else {
                        int attemptsLeft = 5 - wrongCount;
                        json.put("message", "❌ Mật khẩu hiện tại không đúng. " +
                                "Còn " + attemptsLeft + " lần thử trước khi bị khóa 5 phút.");
                    }
                } else if (lockLevel == 1) {
                    shouldLock = true;
                    lockDurationMinutes = 15;
                    lockLevel = 2;
                    lockMessage = "⛔ Bạn đã nhập sai sau khi mở khóa. Khóa chức năng đổi mật khẩu 15 phút.";
                } else if (lockLevel == 2) {
                    shouldLock = true;
                    lockDurationMinutes = 30;
                    lockLevel = 3;
                    lockMessage = "⛔ Bạn đã nhập sai sau khi mở khóa. Khóa chức năng đổi mật khẩu 30 phút.";
                } else if (lockLevel == 3) {
                    shouldLock = true;
                    lockDurationMinutes = 60;
                    lockLevel = 4;
                    lockMessage = "⛔ Bạn đã nhập sai sau khi mở khóa. Khóa chức năng đổi mật khẩu 1 giờ.";
                } else {
                    shouldLock = true;
                    lockDurationMinutes = 60;
                    lockMessage = "⛔ Bạn đã nhập sai sau khi mở khóa. Khóa chức năng đổi mật khẩu 1 giờ.";
                }

                // LƯU VÀO COOKIE
                if (shouldLock) {
                    long lockDurationMs = lockDurationMinutes * 60 * 1000L;
                    lockUntil = now + lockDurationMs;

                    // Cookie lockUntil
                    Cookie cookieLockUntil = new Cookie(cookiePrefix + "_until", String.valueOf(lockUntil));
                    cookieLockUntil.setMaxAge(lockDurationMinutes * 60);
                    cookieLockUntil.setPath("/");
                    cookieLockUntil.setHttpOnly(true);
                    response.addCookie(cookieLockUntil);

                    // Cookie lockLevel
                    Cookie cookieLockLevel = new Cookie(cookiePrefix + "_level", String.valueOf(lockLevel));
                    cookieLockLevel.setMaxAge(24 * 60 * 60); // 24h
                    cookieLockLevel.setPath("/");
                    cookieLockLevel.setHttpOnly(true);
                    response.addCookie(cookieLockLevel);

                    json.put("message", lockMessage);
                    json.put("locked", true);
                }

                // Cookie wrongCount
                Cookie cookieWrongCount = new Cookie(cookiePrefix + "_count", String.valueOf(wrongCount));
                cookieWrongCount.setMaxAge(24 * 60 * 60); // 24h
                cookieWrongCount.setPath("/");
                cookieWrongCount.setHttpOnly(true);
                response.addCookie(cookieWrongCount);

                json.put("success", false);
                json.put("wrongCount", wrongCount);
                json.put("lockLevel", lockLevel);
                out.print(json.toString());
                return;
            }

            // NHẬP ĐÚNG → XÓA TẤT CẢ COOKIE
            Cookie cookieWrongCount = new Cookie(cookiePrefix + "_count", "");
            cookieWrongCount.setMaxAge(0);
            cookieWrongCount.setPath("/");
            response.addCookie(cookieWrongCount);

            Cookie cookieLockUntil = new Cookie(cookiePrefix + "_until", "");
            cookieLockUntil.setMaxAge(0);
            cookieLockUntil.setPath("/");
            response.addCookie(cookieLockUntil);

            Cookie cookieLockLevel = new Cookie(cookiePrefix + "_level", "");
            cookieLockLevel.setMaxAge(0);
            cookieLockLevel.setPath("/");
            response.addCookie(cookieLockLevel);

            boolean result = userDAO.updatePassword(user.getId(), newPassword);
            if (result) {
                json.put("success", true);
                json.put("message", "✅ Đổi mật khẩu thành công.");
            } else {
                json.put("success", false);
                json.put("message", "Đổi mật khẩu thất bại.");
            }
            out.print(json.toString());
        } catch (Exception e) {
            System.out.println("[ERROR] ProfileServlet#handleChangePassword: " + e.getMessage());
            e.printStackTrace();
            json.put("success", false);
            json.put("message", "Đã xảy ra lỗi. Vui lòng thử lại.");
            out.print(json.toString());
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

                    Cookie lockCookie = new Cookie("email_change_lock_" + user.getId(), String.valueOf(lockUntil));
                    lockCookie.setMaxAge(7 * 24 * 60 * 60);
                    lockCookie.setPath("/");
                    lockCookie.setHttpOnly(true);
                    response.addCookie(lockCookie);

                    // Xóa session và cookie remember
                    if (session != null) {
                        session.invalidate(); // Cần xem lại chỗ này!
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

    private String formatTimeAgo(Timestamp createdAt) {
        if (createdAt == null)
            return "";

        LocalDateTime created = createdAt.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(created, now);

        if (minutes < 1)
            return "vừa xong";
        if (minutes < 60)
            return minutes + " phút trước";
        if (minutes < 1440)
            return (minutes / 60) + " giờ trước";
        if (minutes < 10080)
            return (minutes / 1440) + " ngày trước";
        return (minutes / 10080) + " tuần trước";
    }
}