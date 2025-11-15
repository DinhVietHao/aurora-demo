package com.group01.aurora_demo.auth.controller;

import java.util.UUID;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.HttpServletResponse;
import com.group01.aurora_demo.auth.dao.UserDAO;
import jakarta.servlet.annotation.MultipartConfig;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.auth.service.GoogleLogin;
import com.group01.aurora_demo.auth.model.GoogleAccount;
import com.group01.aurora_demo.common.service.EmailService;

@MultipartConfig
@WebServlet(name = "AuthenticationServlet", urlPatterns = { "/auth" })
public class AuthenticationServlet extends HttpServlet {

    private UserDAO userDAO;
    private SecureRandom random;
    private EmailService emailService;
    private int OTP_LIFETIME_SECONDS = 60;

    @Override
    public void init() {
        this.userDAO = new UserDAO();
        this.random = new SecureRandom();
        this.emailService = new EmailService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");
            if (code != null) {
                handleGoogleLogin(request, response, code);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            JSONObject json = new JSONObject();
            PrintWriter out = response.getWriter();
            String action = request.getParameter("action");
            switch (action) {
                case "send-otp":
                    handleSendOtp(request, response, json, out);
                    break;

                case "register":
                    handleRegisterAccount(request, response, json, out);
                    break;

                case "loginLocal":
                    handleLocalLogin(request, response, json, out);
                    break;

                case "forgotPassword":
                    handleForgotPassword(request, response, json, out);
                    break;

                case "logout":
                    handleLogoutAccount(request, response);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String renderOtpEmailHtml(String otp, int ttlSeconds) {
        String ttlLabel = (ttlSeconds % 60 == 0) ? (ttlSeconds / 60) + " phút" : ttlSeconds + " giây";
        return """
                <div style="font-family:Arial,sans-serif;">
                  <h2>Xin chào,</h2>
                  <p>Mã OTP của bạn là:</p>
                  <div style="font-size:28px;font-weight:bold;letter-spacing:4px;">%s</div>
                  <p>Mã có hiệu lực trong %s. Vui lòng không chia sẻ mã này.</p>
                  <hr/>
                  <p>Trân trọng,<br/>Aurora Team</p>
                </div>
                """.formatted(otp, ttlLabel);
    }

    private void handleSendOtp(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out) {
        boolean flag = true;
        String message = "";
        try {
            String email = request.getParameter("email");
            String purpose = request.getParameter("purpose");

            if (!email.trim().toLowerCase().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                flag = false;
                message = "Định dạng email không hợp lệ.";
            }

            if (flag) {
                User existingUser = userDAO.findByEmailAndProvider(email, "LOCAL");

                if ("register".equals(purpose) || "change-email-verify-new".equals(purpose)) {
                    if (existingUser != null) {
                        flag = false;
                        message = "Email đã được sử dụng.";
                    }
                } else if ("forgot-password".equals(purpose) || "change-email-verify-old".equals(purpose)) {
                    if (existingUser == null) {
                        flag = false;
                        message = "Email chưa được đăng ký.";
                    }
                }
            }

            if (flag) {
                String otp = String.format("%06d", random.nextInt(1_000_000));
                String subject = "";
                switch (purpose) {
                    case "register":
                        subject = "Mã xác thực OTP - Đăng ký tài khoản Aurora";
                        break;
                    case "forgot-password":
                        subject = "Mã xác thực OTP - Đặt lại mật khẩu Aurora";
                        break;
                    case "change-email-verify-old":
                    case "change-email-verify-new":
                        subject = "Mã xác thực OTP - Thay đổi email tài khoản Aurora";
                        break;
                }
                String html = renderOtpEmailHtml(otp, OTP_LIFETIME_SECONDS);

                if (emailService.sendHtml(email, subject, html)) {
                    json.put("otp", otp);
                    json.put("expiresIn", OTP_LIFETIME_SECONDS);
                    message = "Đã gửi mã OTP. Vui lòng kiểm tra email.";
                } else {
                    flag = false;
                    message = "Gửi mã OTP đến email thất bại. Vui lòng thử lại.";
                }
            }
        } catch (Exception e) {
            flag = false;
            message = "Error in \"handleSendOtp\" function: " + e.getMessage();
            System.out.println("Error in \"handleSendOtp\" function: " + e.getMessage());
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }

    private void handleRegisterAccount(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out) {
        boolean flag = true;
        String message = "";
        try {

            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirm = request.getParameter("confirmPassword");

            if (fullName.isEmpty()) {
                flag = false;
                message = "Vui lòng nhập họ và tên.";
            }

            if (flag && email.isEmpty()) {
                flag = false;
                message = "Vui lòng nhập email.";
            }

            if (flag && !email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
                flag = false;
                message = "Email không hợp lệ.";
            }

            if (flag && (password.isEmpty() || password.length() < 8)) {
                flag = false;
                message = "Mật khẩu phải có ít nhất 8 ký tự.";
            }

            if (flag && !password.equals(confirm)) {
                flag = false;
                message = "Xác nhận mật khẩu không khớp.";
            }

            if (flag && userDAO.findByEmailAndProvider(email, "LOCAL") != null) {
                flag = false;
                message = "Email đã được sử dụng. Vui lòng dùng email khác.";
            }

            if (flag) {
                User user = new User();
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
                user.setAuthProvider("LOCAL");

                if (!userDAO.createAccount(user)) {
                    flag = false;
                    message = "Không thể tạo tài khoản lúc này do lỗi database. Vui lòng thử lại sau.";
                }
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleRegisterAccount\" function: " + e.getMessage());
            flag = false;
            message = "Error in \"handleRegisterAccount\" function: " + e.getMessage();
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }

    private String handleLoginSuccess(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            User user) {
        HttpSession session = request.getSession(true);
        String msg = "";
        try {
            session.setAttribute("AUTH_USER", user);
            session.setMaxInactiveInterval(60 * 60 * 2);

            CartItemDAO cartItemDAO = new CartItemDAO();
            int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
            session.setAttribute("cartCount", cartCount);

            boolean remember = Boolean.parseBoolean(request.getParameter("rememberMe"));
            if (remember) {
                Cookie cookie = new Cookie("remember_account", user.getEmail());
                cookie.setMaxAge(5 * 24 * 60 * 60);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            String redirectUrl = request.getContextPath() + "/home";
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                if (user.getRoles().contains("ADMIN")) {
                    redirectUrl = request.getContextPath() + "/admin/dashboard";
                }
            }

            if (json != null) {
                json.put("redirect", redirectUrl);
            }

            session.setAttribute("loginRedirectUrl", redirectUrl);
        } catch (Exception e) {
            System.out.println("Error in \"handleLoginSuccess\" function: " + e.getMessage());
            msg = "Error in \"handleLoginSuccess\" function: " + e.getMessage();
            session.setAttribute("cartCount", 0);
        }
        return msg;
    }

    private void handleLocalLogin(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out) {
        boolean flag = true;
        String message = "";
        try {
            String email = request.getParameter("email").trim();
            String password = request.getParameter("password").trim();

            User user = userDAO.findByEmailAndProvider(email, "LOCAL");
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                flag = false;
                message = "Email hoặc mật khẩu không đúng.";
            } else {
                message = handleLoginSuccess(request, response, json, user);
                if (!message.isEmpty())
                    flag = false;
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleLocalLogin\" function: " + e.getMessage());
            message = "Error in \"handleLocalLogin\" function: " + e.getMessage();
            flag = false;
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }

    private void handleGoogleLogin(HttpServletRequest request, HttpServletResponse response, String code) {
        try {
            String randomPw = UUID.randomUUID().toString();
            String hash = BCrypt.hashpw(randomPw, BCrypt.gensalt(10));

            String accessToken = GoogleLogin.getToken(code);
            GoogleAccount ga = GoogleLogin.getUserInfo(accessToken);
            String email = ga.getEmail().trim().toLowerCase();
            User user = userDAO.findByEmailAndProvider(email, "GOOGLE");
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setFullName(ga.getName());
                user.setPassword(hash);
                user.setAuthProvider("GOOGLE");

                userDAO.createAccount(user);
                user = userDAO.findByEmailAndProvider(email, "GOOGLE");
            }

            handleLoginSuccess(request, response, null, user);
            HttpSession session = request.getSession(false);
            String redirectUrl = request.getContextPath() + "/home";
            if (session != null) {
                String savedRedirect = (String) session.getAttribute("loginRedirectUrl");
                if (savedRedirect != null && !savedRedirect.isEmpty()) {
                    redirectUrl = savedRedirect;
                    session.removeAttribute("loginRedirectUrl");
                }
            }

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            System.out.println("Error in \"handleLocalLogin\" function: " + e.getMessage());
        }
    }

    private void handleLogoutAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            Cookie cookie = new Cookie("remember_account", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            System.out.println("Error in \"handleLogoutAccount\" function: " + e.getMessage());
        }
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out) {
        boolean flag = true;
        String message = "";
        try {
            String email = request.getParameter("email");
            String resetPassword = request.getParameter("password");

            if (resetPassword.isEmpty() || resetPassword.length() < 8) {
                flag = false;
                message = "Mật khẩu phải có ít nhất 8 ký tự.";
            }

            if (flag && !email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
                flag = false;
                message = "Email không hợp lệ.";
            }

            if (flag) {
                flag = userDAO.updatePasswordByEmail(email, BCrypt.hashpw(resetPassword, BCrypt.gensalt(10)));
                message = flag ? "🎉Đặt lại mật khẩu thành công!" : "Không thể cập nhật mật khẩu. Vui lòng thử lại.";
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleForgotPassword\" function: " + e.getMessage());
            message = "Error in \"handleForgotPassword\" function: " + e.getMessage();
            flag = false;
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }

    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("123456", BCrypt.gensalt(10)));
    }
}