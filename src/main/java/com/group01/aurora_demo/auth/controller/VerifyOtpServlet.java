package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.common.util.Json;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;

@WebServlet(name = "VerifyOtpServlet", urlPatterns = { "/auth/verify-otp" })
public class VerifyOtpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        var body = req.getReader().lines().collect(java.util.stream.Collectors.joining());
        var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
        String otp = json.path("otp").asText("");
        String email = json.path("email").asText("");

        HttpSession session = req.getSession(false);
        if (session == null) {
            Json.sendError(resp, 400, "Chưa có OTP.");
            return;
        }

        String sEmail = (String) session.getAttribute("REGISTER_OTP_EMAIL");
        String sCode = (String) session.getAttribute("REGISTER_OTP_CODE");
        Long sExpAt = (Long) session.getAttribute("REGISTER_OTP_EXPIRES_AT");
        if (sEmail == null || sCode == null || sExpAt == null) {
            Json.sendError(resp, 400, "Chưa có OTP.");
            return;
        }

        boolean valid = sCode.equals(otp) && (email == null || email.isBlank() || email.equalsIgnoreCase(sEmail));

        Json.sendOk(resp, java.util.Map.of(
                "ok", true,
                "valid", valid));
    }
}