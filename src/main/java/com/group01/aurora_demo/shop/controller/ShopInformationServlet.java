package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.model.Shop;

@WebServlet("/shop/information")
public class ShopInformationServlet extends HttpServlet {

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
            action = "update";

        try {
            switch (action) {
                case "update":
                    try {
                        ShopDAO shopDAO = new ShopDAO();
                        Shop shop = shopDAO.getShopByOwnerUserId(user.getId());

                        if (shop == null) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                    "Shop not found for userId=" + user.getId());
                            return;
                        }
                        request.setAttribute("shop", shop);
                        request.getRequestDispatcher("/WEB-INF/views/shop/shop.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
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
