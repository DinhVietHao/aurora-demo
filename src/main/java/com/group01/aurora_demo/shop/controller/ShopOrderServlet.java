package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.*;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
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
        String action = request.getParameter("action");

        if (action != null) {
            try {
                switch (action) {
                    case "detail":
                        Long orderShopId = Long.parseLong(request.getParameter("orderShopId"));
                        OrderShop orderShop = orderDAO.getOrderShopDetail(orderShopId);
                        if (orderShop == null) {
                            request.setAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng này!");
                        }
                        System.out.println("------------------------" + orderShop.getFinalAmount());
                        request.setAttribute("orderShop", orderShop);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request, response);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi tải đơn hàng: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
            }

        }

        try {
            List<OrderShop> orderShops = new ArrayList<>();
            Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
            switch (status.toUpperCase()) {
                case "ALL":
                    orderShops = orderDAO.getOrdersByShopId(shopId);
                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    request.setAttribute("status", status);
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "PENDING":
                    orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "PENDING");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("status", status);
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "SHIPPING":
                    orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "SHIPPING");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("status", status);
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "WAITING_SHIP":
                    orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "WAITING_SHIP");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("status", status);
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "COMPLETED":
                    orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "COMPLETED");
                    request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận");
                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("status", status);
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "CANCELLED":
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "RETURNED":
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
                case "detail":
                    request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request, response);
                    break;
                default:
                    request.setAttribute("pageTitle", "Tất cả đơn hàng");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            response.sendRedirect("/home");
            return;
        }
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "update-status":
                    long orderShopId = Long.parseLong(request.getParameter("orderShopId"));
                    String newStatus = request.getParameter("newStatus");

                    boolean updated = orderDAO.updateOrderShopStatus(orderShopId, newStatus);

                    if (updated) {
                        session.setAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Không thể cập nhật trạng thái đơn hàng!");
                    }
                    Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
                    List<OrderShop> orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, newStatus);

                    request.setAttribute("orderShops", orderShops);
                    request.setAttribute("status", newStatus);
                    request.setAttribute("pageTitle", "Đơn hàng trạng thái: " + newStatus);

                    request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request, response);
        }

    }
}
