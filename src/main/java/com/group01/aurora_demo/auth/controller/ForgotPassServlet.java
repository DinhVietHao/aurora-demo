package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.auth.dao.RememberMeTokenDAO;
import com.group01.aurora_demo.common.service.EmailService;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.common.util.Json;
import com.group01.aurora_demo.auth.model.User;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import java.security.SecureRandom;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = { "/auth/forgot", "/auth/forgot/*" })
public class ForgotPassServlet extends HttpServlet {

    private static final String PROVIDER_LOCAL = "LOCAL";

    private static final int OTP_LIFETIME_SECONDS = 600;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_ATTEMPTS = 5;

    private static final String FK_EMAIL = "FORGOT_EMAIL";
    private static final String FK_CODE = "FORGOT_CODE";
    private static final String FK_EXPIRES = "FORGOT_EXPIRES_AT";
    private static final String FK_LAST = "FORGOT_LAST_SENT_AT";
    private static final String FK_ATTEMPT = "FORGOT_ATTEMPTS";
    private static final String FK_USERID = "FORGOT_USER_ID";

    private final SecureRandom rnd = new SecureRandom();

    private EmailService emailService;
    private UserDAO userDAO;
    private RememberMeTokenDAO rmDAO;

    @Override
    public void init() {
        this.emailService = new EmailService();
        this.userDAO = new UserDAO();
        this.rmDAO = new RememberMeTokenDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        var body = req.getReader().lines().collect(java.util.stream.Collectors.joining());
        var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);

        String action = json.path("action").asText("");

        if ("start".equalsIgnoreCase(action)) {
            handleStart(req, resp, json);
        } else if ("resend".equalsIgnoreCase(action)) {
            handleResend(req, resp, json);
        } else if ("complete".equalsIgnoreCase(action)) {
            handleComplete(req, resp, json);
        } else {
            Json.sendOk(resp, java.util.Map.of("ok", false, "message", "Hành động không hợp lệ."));
        }
    }

    private void handleStart(HttpServletRequest req, HttpServletResponse resp, JsonNode json) throws IOException {
        String email = json.path("email").asText(null);
        if (email == null || email.isBlank()) {
            Json.sendOk(resp, err("Vui lòng nhập email."));
            return;
        }
        email = email.trim().toLowerCase();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            Json.sendOk(resp, err("Email không hợp lệ."));
            return;
        }

        User u = userDAO.findByEmailAndProvider(email, PROVIDER_LOCAL);
        if (u == null) {
            Json.sendOk(resp, err("Email này chưa được đăng ký."));
            return;
        }

        HttpSession s = req.getSession(true);
        Long last = (Long) s.getAttribute(FK_LAST);
        long now = System.currentTimeMillis();
        if (last != null) {
            long remain = RESEND_COOLDOWN_SECONDS - ((now - last) / 1000);
            if (remain > 0) {
                Json.sendOk(resp, err("Vui lòng thử lại sau " + remain + " giây."));
                return;
            }
        }

        String otp = String.format("%06d", rnd.nextInt(1_000_000));
        String subject = "Mã đặt lại mật khẩu - Aurora";
        String html = emailHtml(otp, OTP_LIFETIME_SECONDS);

        boolean sent;
        try {
            sent = emailService.sendHtml(email, subject, html);
        } catch (Exception ex) {
            Json.sendOk(resp, err("Gửi email thất bại. Vui lòng thử lại."));
            return;
        }
        if (!sent) {
            Json.sendOk(resp, err("Không thể gửi email. Vui lòng thử lại."));
            return;
        }

        long expiresAt = now + OTP_LIFETIME_SECONDS * 1000L;
        s.setAttribute(FK_EMAIL, email);
        s.setAttribute(FK_CODE, otp);
        s.setAttribute(FK_EXPIRES, expiresAt);
        s.setAttribute(FK_LAST, now);
        s.setAttribute(FK_ATTEMPT, 0);
        s.setAttribute(FK_USERID, u.getId());

        Json.sendOk(resp, java.util.Map.of("ok", true, "masked", maskEmail(email)));
    }

    private void handleResend(HttpServletRequest req, HttpServletResponse resp, JsonNode json) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null) {
            Json.sendOk(resp, err("Phiên đã hết hạn. Vui lòng nhập email lại."));
            return;
        }

        String sessionEmail = (String) s.getAttribute(FK_EMAIL);
        if (sessionEmail == null) {
            Json.sendOk(resp, err("Chưa có email để gửi lại OTP."));
            return;
        }

        String clientEmail = json.path("email").asText(null);
        if (clientEmail != null && !clientEmail.isBlank() && !clientEmail.trim().equalsIgnoreCase(sessionEmail)) {
            Json.sendOk(resp, err("Email không khớp phiên hiện tại."));
            return;
        }

        Long last = (Long) s.getAttribute(FK_LAST);
        long now = System.currentTimeMillis();
        if (last != null) {
            long remain = RESEND_COOLDOWN_SECONDS - ((now - last) / 1000);
            if (remain > 0) {
                Json.sendOk(resp, err("Vui lòng thử lại sau " + remain + " giây."));
                return;
            }
        }

        String otp = String.format("%06d", rnd.nextInt(1_000_000));
        String subject = "Mã đặt lại mật khẩu - Aurora";
        String html = emailHtml(otp, OTP_LIFETIME_SECONDS);

        boolean sent;
        try {
            sent = emailService.sendHtml(sessionEmail, subject, html);
        } catch (Exception ex) {
            Json.sendOk(resp, err("Gửi email thất bại. Vui lòng thử lại."));
            return;
        }
        if (!sent) {
            Json.sendOk(resp, err("Không thể gửi email. Vui lòng thử lại."));
            return;
        }

        long expiresAt = now + OTP_LIFETIME_SECONDS * 1000L;
        s.setAttribute(FK_CODE, otp);
        s.setAttribute(FK_EXPIRES, expiresAt);
        s.setAttribute(FK_LAST, now);

        Json.sendOk(resp, ok());
    }

    private void handleComplete(HttpServletRequest req, HttpServletResponse resp, JsonNode json) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null) {
            Json.sendOk(resp, err("Phiên đặt lại hết hạn. Vui lòng làm lại."));
            return;
        }

        String otp = val(json, req, "otp");
        String newPassword = val(json, req, "password");
        String clientEmail = val(json, req, "email");

        if (otp.isBlank() || newPassword.isBlank()) {
            Json.sendOk(resp, err("Thiếu OTP hoặc mật khẩu mới."));
            return;
        }
        if (newPassword.length() < 6) {
            Json.sendOk(resp, err("Mật khẩu tối thiểu 6 ký tự."));
            return;
        }

        String sessionEmail = (String) s.getAttribute(FK_EMAIL);
        String sessionCode = (String) s.getAttribute(FK_CODE);
        Long expiresAt = (Long) s.getAttribute(FK_EXPIRES);
        Integer attempts = (Integer) s.getAttribute(FK_ATTEMPT);
        Long userId = (Long) s.getAttribute(FK_USERID);

        if (sessionEmail == null || sessionCode == null || expiresAt == null || attempts == null || userId == null) {
            Json.sendOk(resp, err("Phiên đặt lại không hợp lệ. Vui lòng làm lại."));
            return;
        }

        if (System.currentTimeMillis() > expiresAt) {
            clearSession(s);
            Json.sendOk(resp, err("Mã OTP đã hết hạn. Vui lòng yêu cầu lại."));
            return;
        }

        if (attempts >= MAX_ATTEMPTS) {
            clearSession(s);
            Json.sendOk(resp, err("Bạn đã nhập sai quá số lần cho phép."));
            return;
        }

        if (clientEmail != null && !clientEmail.isBlank() && !clientEmail.equalsIgnoreCase(sessionEmail)) {
            Json.sendOk(resp, err("Email không khớp phiên hiện tại."));
            return;
        }

        if (!sessionCode.equals(otp)) {
            s.setAttribute(FK_ATTEMPT, attempts + 1);
            Json.sendOk(resp, err("Mã OTP không đúng."));
            return;
        }

        String hash = org.mindrot.jbcrypt.BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt(10));
        boolean ok = userDAO.updateLocalPasswordByUserId(userId, hash);
        if (!ok) {
            Json.sendOk(resp, err("Không thể cập nhật mật khẩu. Vui lòng thử lại."));
            return;
        }

        try {
            rmDAO.deleteAllForUser(userId);
        } catch (Exception ignore) {
        }

        clearSession(s);
        Json.sendOk(resp, java.util.Map.of("ok", true, "message", "Đặt lại mật khẩu thành công."));
    }

    private static java.util.Map<String, Object> ok() {
        return java.util.Map.of("ok", true);
    }

    private static java.util.Map<String, Object> err(String msg) {
        return java.util.Map.of("ok", false, "message", msg);
    }

    private static void clearSession(HttpSession s) {
        s.removeAttribute(FK_EMAIL);
        s.removeAttribute(FK_CODE);
        s.removeAttribute(FK_EXPIRES);
        s.removeAttribute(FK_LAST);
        s.removeAttribute(FK_ATTEMPT);
        s.removeAttribute(FK_USERID);
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "*******";
        }
        String[] parts = email.split("@", 2);
        String local = parts[0], domain = parts[1];
        String head = local.length() <= 2 ? local.substring(0, 1) : local.substring(0, 2);
        return head + "*****@" + domain;
    }

    private static String emailHtml(String otp, int ttlSec) {
        String ttlLabel = (ttlSec % 60 == 0) ? (ttlSec / 60) + " phút" : ttlSec + " giây";
        return """
                    <div style="font-family:Arial,sans-serif;">
                      <h3>Đặt lại mật khẩu</h3>
                      <p>Mã OTP của bạn:</p>
                      <div style="font-size:28px;font-weight:bold;letter-spacing:4px;">%s</div>
                      <p>Mã có hiệu lực trong %s. Vui lòng không chia sẻ mã này.</p>
                      <hr/><p>Aurora Team</p>
                    </div>
                """.formatted(otp, ttlLabel);
    }

    private static String val(JsonNode json, HttpServletRequest req, String key) {
        if (json != null && json.has(key)) {
            return json.path(key).asText("");
        }
        String v = req.getParameter(key);
        return v == null ? "" : v;
    }
}