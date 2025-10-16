package com.group01.aurora_demo.auth.controller;

import java.io.PrintWriter;
import org.json.JSONObject;
import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.HttpServletResponse;
import com.group01.aurora_demo.auth.dao.UserDAO;
import jakarta.servlet.annotation.MultipartConfig;
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

            if (!email.trim().toLowerCase().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                flag = false;
                message = "Định dạng email không hợp lệ.";
            }

            if (flag && userDAO.findByEmailAndProvider(email, "LOCAL") != null) {
                flag = false;
                message = "Email đã được sử dụng.";
            }

            if (flag) {
                String otp = String.format("%06d", random.nextInt(1_000_000));
                String subject = "Mã xác thực OTP - Aurora";
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
}