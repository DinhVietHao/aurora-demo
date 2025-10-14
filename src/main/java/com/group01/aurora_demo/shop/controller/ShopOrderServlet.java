package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.*;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/orders")
public class ShopOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String status = Optional.ofNullable(request.getParameter("status")).orElse("PENDING");

        try {
            Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
            List<Order> orders = new ArrayList<>();
            List<OrderShop> orderShops = new ArrayList<>();

            // ✅ Xử lý theo trạng thái đơn hàng
            switch (status.toUpperCase()) {
                case "ALL":
                    // orders = orderDAO.getOrdersByShop(shopId);
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    break;
                case "PENDING":
                    orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "PENDING");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    break;
                case "SHIPPING":

                    break;
                case "WAITING_SHIP":

                    break;
                case "COMPLETED":

                    break;
                case "CANCELLED":

                    break;
                case "RETURNED":

                    break;
                default:
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    break;
            }

            request.setAttribute("orderShops", orderShops);
            request.setAttribute("status", status);
            request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
        }
    }
}
