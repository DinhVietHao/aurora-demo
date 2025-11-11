package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.common.util.Json;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "RegisterServlet", urlPatterns = { "/auth/register" })
public class RegisterServlet extends HttpServlet {

    private static final String OTP_EMAIL_KEY = "REGISTER_OTP_EMAIL";
    private static final String OTP_CODE_KEY = "REGISTER_OTP_CODE";
    private static final String OTP_EXPIRES_KEY = "REGISTER_OTP_EXPIRES_AT";

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            var body = request.getReader().lines().collect(java.util.stream.Collectors.joining());
            var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);

            String fullName = trim(json.path("fullName").asText(null));
            String email = trim(json.path("email").asText(null));
            String password = trim(json.path("password").asText(null));
            String confirm = trim(json.path("confirmPassword").asText(null));
            String otpInput = trim(json.path("otp").asText(null));

            if (isEmpty(fullName)) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập họ và tên.");
                return;
            }

            if (isEmpty(email)) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập email.");
                return;
            }

            if (!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Email không hợp lệ.");
                return;
            }

            if (isEmpty(password) || password.length() < 6) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Mật khẩu phải có ít nhất 6 ký tự.");
                return;
            }

            if (!password.equals(confirm)) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Xác nhận mật khẩu không khớp.");
                return;
            }

            if (isEmpty(otpInput) || !otpInput.matches("^\\d{6}$")) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập mã OTP 6 số.");
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                Json.send(response, HttpServletResponse.SC_OK, Map.of(
                        "ok", false,
                        "message", "Phiên làm việc đã hết. Vui lòng bấm Gửi OTP để nhận mã mới.",
                        "otpExpired", true));
                return;
            }

            String otpEmail = (String) session.getAttribute(OTP_EMAIL_KEY);
            String otpCode = (String) session.getAttribute(OTP_CODE_KEY);
            Long otpExpires = (Long) session.getAttribute(OTP_EXPIRES_KEY);

            if (otpEmail == null || otpCode == null || otpExpires == null) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Chưa có mã OTP. Vui lòng bấm Gửi OTP để nhận mã.");
                return;
            }

            long now = System.currentTimeMillis();
            if (now > otpExpires) {
                Json.send(response, HttpServletResponse.SC_OK, Map.of(
                        "ok", false,
                        "message", "Mã OTP đã hết hạn. Vui lòng bấm Gửi OTP để nhận mã mới.",
                        "otpExpired", true));
                return;
            }

            if (!email.equalsIgnoreCase(otpEmail)) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Email đăng ký không trùng với email đã nhận OTP.");
                return;
            }

            if (!otpInput.equals(otpCode)) {
                Json.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Mã OTP không đúng.");
                return;
            }

            User existed = userDAO.findByEmailAndProvider(email, "LOCAL");
            if (existed != null) {
                Json.sendError(response, HttpServletResponse.SC_CONFLICT,
                        "Email đã được sử dụng. Vui lòng dùng email khác.");
                return;
            }

            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            User user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPasswordHash(hash);
            user.setAuthProvider("LOCAL");

            Boolean created = userDAO.createAccount(user);
            if (!created) {
                Json.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Không thể tạo tài khoản lúc này. Vui lòng thử lại sau.");
                return;
            }

            session.removeAttribute(OTP_EMAIL_KEY);
            session.removeAttribute(OTP_CODE_KEY);
            session.removeAttribute(OTP_EXPIRES_KEY);

            Json.sendOk(response, java.util.Map.of(
                    "ok", true));

        } catch (IOException ex) {
            Json.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.");
        }
    }

}