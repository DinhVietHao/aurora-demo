package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.List;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ProductDAO;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("shop/product")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        try {
            switch (action) {
                case "view":
                    ShopDAO shopDAO = new ShopDAO();
                    ProductDAO productDAO = new ProductDAO();
                    long shopId = shopDAO.getShopIdByUserId(user.getId());

                    int page = 1;
                    int limit = 15; // Mỗi trang 15 sản phẩm
                    String pageParam = request.getParameter("page");

                    if (pageParam != null) {
                        try {
                            page = Integer.parseInt(pageParam);
                        } catch (NumberFormatException e) {
                            page = 1;
                        }
                    }

                    int offset = (page - 1) * limit;

                    List<Product> listProduct = productDAO.getProductsByShopId(shopId, offset, limit);
                    int totalProducts = productDAO.countProductsByShopId(shopId);
                    int totalPages = (int) Math.ceil((double) totalProducts / limit);

                    request.setAttribute("listProduct", listProduct);
                    request.setAttribute("page", page);
                    request.setAttribute("totalPages", totalPages);
                    request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        request.getRequestDispatcher("/WEB-INF/views/shop/productManage.jsp").forward(request, response);
    }
}
