package com.group01.aurora_demo.auth.controller;

import com.group01.aurora_demo.common.util.Json;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Servlet dùng để vô hiệu hóa (invalidate) OTP hiện tại trong phiên đăng ký.
 * Client sẽ gọi endpoint này khi đóng modal/huỷ flow để dọn sạch các key OTP
 * trong session.
 */
@WebServlet(name = "InvalidateOtpServlet", urlPatterns = { "/auth/invalidate-otp" })
public class InvalidateOtpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Trả JSON UTF-8
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        // Lấy session hiện có, KHÔNG tạo mới (nếu không có thì bỏ qua)
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Xoá toàn bộ thông tin liên quan đến OTP đã phát sinh trong flow đăng ký
            session.removeAttribute("REGISTER_OTP_EMAIL");
            session.removeAttribute("REGISTER_OTP_CODE");
            session.removeAttribute("REGISTER_OTP_EXPIRES_AT");
            session.removeAttribute("REGISTER_OTP_LAST_SENT_AT");
        }

        // Phản hồi ok đơn giản để client biết thao tác dọn dẹp đã hoàn tất
        Json.sendOk(resp, java.util.Map.of("ok", true));
    }
}