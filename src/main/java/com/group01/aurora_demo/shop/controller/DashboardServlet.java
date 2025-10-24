package com.group01.aurora_demo.shop.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

<<<<<<< HEAD
@WebServlet("/dashboard")
=======
@WebServlet("/shop/dashboard")
>>>>>>> 6a13786814f123593cf52f52fe60d13c593aa470
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/shop/shopDashboard.jsp").forward(request, response);
    }
}
