package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.util.*;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.common.service.EmailService;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/orders")
public class ShopOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final OrderShopDAO orderShopDAO = new OrderShopDAO();

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
        String status = Optional.ofNullable(request.getParameter("status")).orElse("ALL");
        String action = request.getParameter("action");
        try {
            Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
            Map<String, Integer> orderCounts = orderDAO.getOrderCountsByShopId(shopId);
            request.setAttribute("orderCountAll", orderDAO.countOrdersByShop(shopId));
            request.setAttribute("orderCountPending", orderCounts.getOrDefault("PENDING", 0));
            request.setAttribute("orderCountShipping", orderCounts.getOrDefault("SHIPPING", 0));
            request.setAttribute("orderCountWaiting", orderCounts.getOrDefault("WAITING_SHIP", 0));
            request.setAttribute("orderCountConfirm", orderCounts.getOrDefault("CONFIRM", 0));
            request.setAttribute("orderCountCompleted", orderCounts.getOrDefault("COMPLETED", 0));
            request.setAttribute("orderCountCancelled", orderCounts.getOrDefault("CANCELLED", 0));
            request.setAttribute("orderCountReturned", orderCounts.getOrDefault("RETURNED", 0));

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
                            request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request,
                                    response);
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
                        request.setAttribute("pageTitle", "chờ xác nhận");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "SHIPPING":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "SHIPPING");
                        request.setAttribute("pageTitle", "giao cho đơn vận chuyển");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "WAITING_SHIP":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "WAITING_SHIP");
                        request.setAttribute("pageTitle", "Đơn hàng đang giao");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "CONFIRM":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "CONFIRM");
                        request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận của khách hàng");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "COMPLETED":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "COMPLETED");
                        request.setAttribute("pageTitle", "hoàn thành");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "CANCELLED":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "CANCELLED");
                        request.setAttribute("pageTitle", "đã hủy");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "RETURNED":
                        orderShops = orderDAO.getOrdersByShopIdAndStatus(shopId, "RETURNED");
                        request.setAttribute("pageTitle", "hoàn đơn hàng");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
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

                    boolean updated = orderShopDAO.updateOrderShopStatus(orderShopId, newStatus);

                    if (updated) {
                        EmailService emailService = new EmailService();
                        OrderShop orderShop = orderDAO.getOrderShopDetail(orderShopId);

                        String customerEmail = orderShop.getUser().getEmail();
                        String customerName = orderShop.getCustomerName();

                        String subject = "Cập nhật đơn hàng #" + orderShopId + " - Aurora";
                        String html = renderOrderStatusEmail(customerName, orderShopId, newStatus);
                        try {
                            emailService.sendHtml(customerEmail, subject, html);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            request.setAttribute("errorMessage",
                                    "⚠️ Không thể gửi email xác nhận đơn hàng:" + ex.getMessage());
                        }
                        request.setAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
                    } else {
                        request.setAttribute("errorMessage", "Không thể cập nhật trạng thái đơn hàng!");
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

    private String renderOrderStatusEmail(String name, long orderId, String status) {
        String statusLabel;
        switch (status.toUpperCase()) {
            case "CONFIRMED" -> statusLabel = "Đã xác nhận";
            case "SHIPPING" -> statusLabel = "Đang giao hàng";
            case "COMPLETED" -> statusLabel = "Hoàn tất";
            case "CANCELLED" -> statusLabel = "Đã hủy";
            default -> statusLabel = "Đang xử lý";
        }

        return """
                    <div style="font-family:Arial,sans-serif; color:#333;">
                        <h2>Xin chào %s,</h2>
                        <p>Đơn hàng <b>#%d</b> của bạn đã được cập nhật trạng thái:</p>
                        <h3 style="color:#007bff;">%s</h3>
                        <p>Cảm ơn bạn đã mua sắm tại Aurora.</p>
                        <p>Trân trọng,<br/>Đội ngũ Aurora</p>
                    </div>
                """.formatted(name, orderId, statusLabel);
    }
}
