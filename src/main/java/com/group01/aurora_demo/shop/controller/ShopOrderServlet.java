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
            request.setAttribute("orderCountReturned", orderCounts.getOrDefault("RETURNED_GROUP", 0));

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

                    boolean updated = false;
                    if ("RETURNED".equals(newStatus)) {
                        updated = orderDAO.updateOrderShopStatusByBR(orderShopId, newStatus);
                    } else {
                        updated = orderShopDAO.updateOrderShopStatus(orderShopId, newStatus);
                    }

                    if (updated) {

                        Set<String> notifiableStatuses = Set.of(
                                "CONFIRM", "SHIPPING", "COMPLETED",
                                "CANCELLED", "RETURNED", "RETURNED_REJECTED", "WAITING_SHIP");

                        boolean shouldSendEmail = notifiableStatuses
                                .contains(newStatus != null ? newStatus.toUpperCase() : "");

                        if (shouldSendEmail) {
                            try {
                                EmailService emailService = new EmailService();
                                OrderShop orderShop = orderDAO.getOrderShopDetail(orderShopId);

                                String customerEmail = orderShop.getUser().getEmail();
                                String customerName = orderShop.getCustomerName();

                                String subject = "Cập nhật đơn hàng #" + orderShopId + " - Aurora";
                                String html = renderOrderStatusEmail(customerName, orderShopId, newStatus);
                                emailService.sendHtml(customerEmail, subject, html);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                request.setAttribute("errorMessage",
                                        "⚠️ Không thể gửi email xác nhận đơn hàng: " + ex.getMessage());
                            }
                        }

                        request.setAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
                    } else {
                        request.setAttribute("errorMessage", "Không thể cập nhật trạng thái đơn hàng!");
                    }
                    if (newStatus.equals("RETURNED_REJECTED")) {
                        newStatus = "RETURNED";
                    }
                    request.setAttribute("status", newStatus);
                    response.sendRedirect(request.getContextPath() + "/shop/orders?status=" + newStatus);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý yêu cầu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request, response);
        }
    }

    private String renderOrderStatusEmail(String name, long orderId, String status) {
        String statusLabel;
        String message;

        switch (status.toUpperCase()) {
            case "CONFIRM" -> {
                statusLabel = "Đơn hàng đang chờ xác nhận của bạn";
                message = "Đơn hàng của bạn đã được người bán xác nhận. Chúng tôi đang đợi bạn xác nhận đơn hàng được giao thành công.";
            }
            case "SHIPPING" -> {
                statusLabel = "Đơn hàng đã giao cho đơn vị vận chuyển";
                message = "Đơn hàng của bạn đã giao cho đơn vị vận chuyển, chúng tôi sẽ giao đơn hàng cho bạn sớm nhất có thể!";
            }
            case "WAITING_SHIP" -> {
                statusLabel = "Đơn hàng đang được giao";
                message = "Đơn hàng của bạn đang trên đường đến địa chỉ nhận. Hãy chuẩn bị để nhận hàng nhé!";
            }
            case "COMPLETED" -> {
                statusLabel = "Đơn hàng đã hoàn tất";
                message = "Cảm ơn bạn đã tin tưởng Aurora! Rất mong sớm được phục vụ bạn trong những lần mua sắm tiếp theo.";
            }
            case "CANCELLED" -> {
                statusLabel = "Đơn hàng đã bị hủy";
                message = "Rất tiếc, đơn hàng của bạn đã bị hủy. Nếu đây là sự nhầm lẫn, bạn có thể đặt lại bất cứ lúc nào.";
            }
            case "RETURNED" -> {
                statusLabel = "Xác nhận trả hàng thành công";
                message = "Chúng tôi đã xác nhận yêu cầu trả hàng của bạn. Sản phẩm sẽ được xử lý hoàn trả theo chính sách của Aurora.";
            }
            case "RETURNED_REJECTED" -> {
                statusLabel = "Yêu cầu trả hàng bị từ chối";
                message = "Rất tiếc, yêu cầu trả hàng của bạn không được chấp nhận. Vui lòng liên hệ chủ shop để biết thêm chi tiết.";
            }
            default -> {
                statusLabel = "Đơn hàng đang được xử lý";
                message = "Đơn hàng của bạn hiện đang được xử lý. Chúng tôi sẽ thông báo cho bạn ngay khi có cập nhật mới.";
            }
        }

        return """
                    <div style="font-family:Arial,sans-serif; color:#333; line-height:1.6;">
                        <h2>Xin chào %s,</h2>
                        <p>Đơn hàng <b>#%d</b> của bạn đã được cập nhật trạng thái:</p>
                        <h3 style="color:#007bff;">%s</h3>
                        <p>%s</p>
                        <p style="margin-top:20px;">Cảm ơn bạn đã mua sắm tại <b>Aurora</b>.</p>
                        <p>Trân trọng,<br/>Đội ngũ Aurora</p>
                    </div>
                """.formatted(name, orderId, statusLabel, message);
    }

}
