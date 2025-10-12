package com.group01.aurora_demo.shipping.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Hiển thị trang nhập địa chỉ giao hàng. Sau này có thể load sẵn địa chỉ mặc
 * định của user.
 */
@WebServlet(name = "ShippingAddressServlet", urlPatterns = { "/shipping-address" })
public class ShippingAddressServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // req.setAttribute("fullName", ...); req.setAttribute("phone", ...); ...

        req.getRequestDispatcher("/WEB-INF/views/shipping_address.jsp").forward(req, resp);
    }
}