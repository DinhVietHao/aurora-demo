package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.catalog.model.OrderItemVATInfo;
import com.group01.aurora_demo.common.service.EmailService;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/shop/orders")
public class ShopOrderServlet extends HttpServlet {

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
            Map<String, Integer> orderCounts = orderShopDAO.getOrderShopCountsByShopId(shopId);
            request.setAttribute("orderCountAll", orderShopDAO.countOrderShopByShop(shopId));
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
                            OrderShop orderShop = orderShopDAO.getOrderShopDetail(orderShopId);
                            if (orderShop == null) {
                                request.setAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng này!");
                                request.getRequestDispatcher("/WEB-INF/views/shop/orderDetail.jsp").forward(request,
                                        response);
                                break;
                            }

                            request.setAttribute("phone", orderShop.getAddress().split("-")[0].trim());
                            request.setAttribute("address", orderShop.getAddress().split("-")[1].trim());
                            request.setAttribute("orderShop", orderShop);

                            if ("COMPLETED".equalsIgnoreCase(orderShop.getStatus())) {
                                Date completedDate = orderShop.getUpdatedAt();
                                if (completedDate != null) {
                                    LocalDateTime completedAt = completedDate.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime();

                                    LocalDateTime now = LocalDateTime.now();
                                    long hoursPassed = ChronoUnit.HOURS.between(completedAt, now);
                                    long remainHours = 168 - hoursPassed;

                                    double totalPrice = orderShop.getSubtotal();
                                    double AmoutShopreceived = orderShop.getSubtotal() + orderShop.getShippingFee()
                                            - orderShop.getShopDiscount();
                                    double shipFee = orderShop.getShippingFee();
                                    double voucherShop = orderShop.getShopDiscount();
                                    double platformFee = 3000;
                                    double totalVAT = orderShopDAO.getTotalVATByOrderShopId(orderShopId);

                                    if (hoursPassed >= 168) {
                                        double receivedAmount = totalPrice - shipFee - voucherShop - platformFee
                                                - totalVAT + shipFee;
                                        if (receivedAmount < 0)
                                            receivedAmount = 0;

                                        request.setAttribute("receivedAmount", receivedAmount);
                                        request.setAttribute("isReceived", true);
                                    } else {
                                        long remainDays = remainHours / 24;
                                        long remainH = remainHours % 24;

                                        request.setAttribute("remainDays", remainDays);
                                        request.setAttribute("remainHours", remainH);
                                        request.setAttribute("isReceived", false);
                                    }
                                    request.setAttribute("AmoutShopreceived", AmoutShopreceived);
                                    request.setAttribute("totalPrice", totalPrice);
                                    request.setAttribute("shipFee", shipFee);
                                    request.setAttribute("voucherShop", voucherShop);
                                    request.setAttribute("platformFee", platformFee);
                                    request.setAttribute("totalVAT", totalVAT);
                                    LocalDateTime expectedReceiveAt = completedAt.plusDays(7);
                                    request.setAttribute("completedAt", completedAt);
                                    request.setAttribute("expectedReceiveAt", expectedReceiveAt);

                                } else {
                                    request.setAttribute("isReceived", false);
                                }
                            } else {
                                request.setAttribute("isReceived", false);
                            }

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
                        orderShops = orderShopDAO.getOrderShopByShopId(shopId);
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("pageTitle", "Tất cả đơn hàng");
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "PENDING":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "PENDING");
                        request.setAttribute("pageTitle", "chờ xác nhận");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "SHIPPING":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "SHIPPING");
                        request.setAttribute("pageTitle", "giao cho đơn vận chuyển");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "WAITING_SHIP":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "WAITING_SHIP");
                        request.setAttribute("pageTitle", "Đơn hàng đang giao");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "CONFIRM":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "CONFIRM");
                        request.setAttribute("pageTitle", "Đơn hàng chờ xác nhận của khách hàng");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "COMPLETED":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "COMPLETED");
                        request.setAttribute("pageTitle", "hoàn thành");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "CANCELLED":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "CANCELLED");
                        request.setAttribute("pageTitle", "đã hủy");
                        request.setAttribute("orderShops", orderShops);
                        request.setAttribute("status", status);
                        request.getRequestDispatcher("/WEB-INF/views/shop/orderManage.jsp").forward(request, response);
                        break;
                    case "RETURNED":
                        orderShops = orderShopDAO.getOrderShopByShopIdAndStatus(shopId, "RETURNED");
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
                        updated = orderShopDAO.updateOrderShopStatusByBR(orderShopId, newStatus);
                    } else {
                        updated = orderShopDAO.updateOrderShopStatus(orderShopId, newStatus);
                    }

                    if (updated) {

                        Set<String> notifiableStatuses = Set.of(
                                "CONFIRM", "CANCELLED", "RETURNED", "RETURNED_REJECTED");

                        boolean shouldSendEmail = notifiableStatuses
                                .contains(newStatus != null ? newStatus.toUpperCase() : "");

                        if (shouldSendEmail) {
                            try {
                                EmailService emailService = new EmailService();
                                OrderShop orderShop = orderShopDAO.getOrderShopDetail(orderShopId);

                                String customerEmail = orderShop.getUser().getEmail();
                                String customerName = orderShop.getUser().getFullName();

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
