package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.*;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.model.Order;
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

            // ✅ Xử lý theo trạng thái đơn hàng
            switch (status.toUpperCase()) {
                case "ALL":
                    // orders = orderDAO.getOrdersByShop(shopId);
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    break;
                case "PENDING":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "PENDING");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    break;
                case "SHIPPING":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "CONFIRMED");
                    request.setAttribute("pageTitle", "Đơn hàng đang vận chuyển");
                    break;
                case "WAITING_SHIP":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "SHIPPING");
                    request.setAttribute("pageTitle", "Đơn hàng chờ giao hàng");
                    break;
                case "COMPLETED":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "DELIVERED");
                    request.setAttribute("pageTitle", "Đơn hàng hoàn thành");
                    break;
                case "CANCELLED":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "CANCELLED");
                    request.setAttribute("pageTitle", "Đơn hàng bị hủy / hoàn tiền");
                    break;
                case "RETURNED":
                    orders = orderDAO.getOrdersWithItemsByShopAndStatus(shopId, "CANCELLED");
                    request.setAttribute("pageTitle", "Đơn hàng bị hủy / hoàn tiền");
                    break;
                default:
                    // orders = orderDAO.getOrdersByShop(shopId);
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    break;
            }

            // ✅ Gửi dữ liệu sang JSP
            request.setAttribute("orders", orders);
            request.setAttribute("status", status);
            request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
        }
    }
}
