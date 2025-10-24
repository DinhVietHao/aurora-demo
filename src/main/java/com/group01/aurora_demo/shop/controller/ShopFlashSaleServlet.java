package com.group01.aurora_demo.shop.controller;

import java.io.IOException;

import com.group01.aurora_demo.auth.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/flashSale")
public class ShopFlashSaleServlet extends HttpServlet {

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
            action = "viewListFlashsale";
        switch (action) {
            case "viewListFlashsale":
                request.getRequestDispatcher("/WEB-INF/views/shop/flashSale.jsp").forward(request, response);
                break;

            default:
                request.setAttribute("errorMessage", "lỗi tải list Flashsale");
                break;
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
