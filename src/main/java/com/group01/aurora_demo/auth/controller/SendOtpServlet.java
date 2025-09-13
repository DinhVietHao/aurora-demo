package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.common.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.group01.aurora_demo.auth.model.OtpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.common.util.Json;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.security.SecureRandom;
import java.io.IOException;
import java.util.Locale;

@WebServlet(name = "SendOtpServlet", urlPatterns = { "/auth/send-otp" })
public class SendOtpServlet extends HttpServlet {

    private static final int OTP_LIFETIME_SECONDS = 60;
    private static final int RESEND_COOLDOWN_SECONDS = 30;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private EmailService emailService;

    @Override
    public void init() {
        this.emailService = new EmailService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String body = req.getReader().lines().collect(java.util.stream.Collectors.joining());
        String email;
        try {
            com.fasterxml.jackson.databind.JsonNode json = MAPPER.readTree(body);
            email = json.path("email").asText(null);
        } catch (JsonProcessingException ex) {
            Json.sendError(resp, 400, "Payload không hợp lệ.");
            return;
        }

        if (email == null || email.isBlank()) {
            Json.sendError(resp, 400, "Thiếu email.");
            return;
        }
        email = email.trim().toLowerCase(Locale.ROOT);

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            Json.sendError(resp, 400, "Định dạng email không hợp lệ.");
            return;
        }

        UserDAO userDao = new UserDAO();
        if (userDao.findByEmailAndProvider(email, "LOCAL") != null) {
            Json.sendError(resp, 409, "Email đã được sử dụng.");
            return;
        }

        HttpSession session = req.getSession(true);

        Long lastSentAt = (Long) session.getAttribute("REGISTER_OTP_LAST_SENT_AT");
        long now = System.currentTimeMillis();
        if (lastSentAt != null) {
            long elapsed = (now - lastSentAt) / 1000;
            long remain = RESEND_COOLDOWN_SECONDS - elapsed;
            if (remain > 0) {
                Json.sendError(resp, 429, "Vui lòng thử lại sau " + remain + " giây.");
                return;
            }
        }

        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        String subject = "Mã xác thực OTP - Aurora";
        String html = renderOtpEmailHtml(otp, OTP_LIFETIME_SECONDS);

        boolean sent;
        try {
            sent = emailService.sendHtml(email, subject, html);
        } catch (Exception ex) {
            Json.sendError(resp, 502, "Gửi email thất bại: " + ex.getMessage());
            return;
        }
        if (!sent) {
            Json.sendError(resp, 502, "Gửi email thất bại. Vui lòng thử lại.");
            return;
        }

        long expiresAt = now + OTP_LIFETIME_SECONDS * 1000L;
        session.setAttribute("REGISTER_OTP_EMAIL", email);
        session.setAttribute("REGISTER_OTP_CODE", otp);
        session.setAttribute("REGISTER_OTP_EXPIRES_AT", expiresAt);
        session.setAttribute("REGISTER_OTP_LAST_SENT_AT", now);

        Json.sendOk(resp, new OtpResponse(true, "Đã gửi OTP về email.", OTP_LIFETIME_SECONDS));
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
}