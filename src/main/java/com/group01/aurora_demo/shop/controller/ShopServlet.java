package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.group01.aurora_demo.auth.model.User;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        try {
            switch (action) {
                case "view":
                    request.getRequestDispatcher("/WEB-INF/views/shop/shopDashboard.jsp").forward(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
