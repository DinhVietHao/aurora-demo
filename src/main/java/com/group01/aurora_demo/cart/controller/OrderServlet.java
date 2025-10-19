package com.group01.aurora_demo.cart.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.dao.PaymentDAO;
import com.group01.aurora_demo.cart.dao.dto.OrderDTO;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.service.OrderService;
import com.group01.aurora_demo.cart.service.VNPayService;
import com.group01.aurora_demo.cart.utils.ServiceResponse;
import com.group01.aurora_demo.catalog.dao.CategoryDAO;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    private OrderService orderService;
    private VoucherDAO voucherDAO;
    private OrderDAO orderDAO;
    private OrderShopDAO orderShopDAO;
    private CategoryDAO categoryDAO;
    private PaymentDAO paymentDAO;
    private AddressDAO addressDAO;

    public OrderServlet() {
        this.orderService = new OrderService();
        this.voucherDAO = new VoucherDAO();
        this.orderDAO = new OrderDAO();
        this.categoryDAO = new CategoryDAO();
        this.paymentDAO = new PaymentDAO();
        this.orderShopDAO = new OrderShopDAO();
        this.addressDAO = new AddressDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("user", user);
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            try {
                String status = req.getParameter("status");
                List<OrderDTO> orders = this.orderDAO.getOrdersByStatus(user.getId(), status);
                Map<Long, List<OrderDTO>> grouped = orders.stream()
                        .collect(Collectors.groupingBy(order -> order.getOrderId(),
                                LinkedHashMap::new,
                                Collectors.toList()));
                req.setAttribute("orders", grouped);
                req.getRequestDispatcher("/WEB-INF/views/customer/order/order.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.equals("/payment")) {
            String responseCode = req.getParameter("vnp_ResponseCode");
            String txnRef = req.getParameter("vnp_TxnRef");
            String transactionNo = req.getParameter("vnp_TransactionNo");

            try {
                long orderId = Long.parseLong(txnRef);
                if ("00".equals(responseCode)) {
                    this.orderDAO.updateOrderStatus(orderId, "PENDING");
                    this.orderShopDAO.updateOrderShopStatusByOrderId(orderId, "PENDING");
                    this.paymentDAO.updatePaymentStatus(orderId, "SUCCESS", transactionNo);
                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastMsg",
                            "Thanh toán thành công! Đơn hàng của bạn đang chờ xác nhận từ người bán.");
                    resp.sendRedirect(req.getContextPath() + "/order?status=pending");
                } else {
                    this.paymentDAO.updatePaymentStatus(orderId, "FAILED", transactionNo);
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Thanh toán không thành công. Vui lòng thử lại.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/checkout");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("user", user);
        String path = req.getPathInfo();
        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        switch (path) {
            case "/create": {
                try {
                    Long addressId = Long.parseLong(req.getParameter("addressId"));

                    String systemVoucherDiscountCode = req.getParameter("systemVoucherDiscount");
                    String systemVoucherShipCode = req.getParameter("systemVoucherShip");

                    Voucher systemVoucherDiscount = null;
                    Voucher systemVoucherShip = null;

                    Address address = null;
                    if (addressId != null) {
                        address = this.addressDAO.getAddressById(user.getId(), addressId);
                    }

                    if (address == null || address.getDistrictId() == 0 || address.getWardCode() == null) {
                        json.put("success", false);
                        json.put("type", "error");
                        json.put("title", "Thiếu địa chỉ giao hàng");
                        json.put("message", "Vui lòng chọn địa chỉ giao hàng hợp lệ trước khi đặt hàng!");
                        out.print(json.toString());
                        return;
                    }
                    if (systemVoucherDiscountCode != null && !systemVoucherDiscountCode.isEmpty()) {
                        systemVoucherDiscount = this.voucherDAO.getVoucherByCode(null, systemVoucherDiscountCode,
                                false);
                    }
                    if (systemVoucherShipCode != null && !systemVoucherShipCode.isEmpty()) {
                        systemVoucherShip = this.voucherDAO.getVoucherByCode(null, systemVoucherShipCode, false);
                    }

                    Map<Long, String> shopVouchers = new HashMap<>();
                    req.getParameterMap().forEach((key, values) -> {
                        if (key.startsWith("shopVoucher_")) {
                            Long shopId = Long.parseLong(key.substring("shopVoucher_".length()));
                            shopVouchers.put(shopId, values[0]);
                        }
                    });

                    ServiceResponse result = this.orderService.createOrder(user, address, systemVoucherDiscount,
                            systemVoucherShip,
                            shopVouchers);
                    if ("success".equalsIgnoreCase(result.getType())) {
                        System.out.println("Check FinalAmount= " + result.getFinalAmount());
                        String paymentUrl = VNPayService.getPaymentUrl(req, resp, result.getOrderId(),
                                result.getFinalAmount());
                        json.put("url", paymentUrl);
                        json.put("success", true);
                        json.put("type", result.getType());
                        json.put("title", result.getTitle());
                        json.put("message", result.getMessage());
                    } else {
                        json.put("success", false);
                        json.put("type", result.getType());
                        json.put("title", result.getTitle());
                        json.put("message", result.getMessage());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("message", "lỗi hệ thống!");
                }
                out.print(json.toString());
                break;

            }
            case "/repayment": {
                try {
                    long orderId = Long.parseLong(req.getParameter("orderId"));
                    Order order = orderDAO.getOrderById(orderId);

                    if (order == null) {
                        session.setAttribute("toastType", "error");
                        session.setAttribute("toastMsg", "Không tìm thấy đơn hàng cần thanh toán lại.");
                        resp.sendRedirect(req.getContextPath() + "/order");
                        break;
                    }

                    if (!"PENDING_PAYMENT".equals(order.getOrderStatus())) {
                        session.setAttribute("toastType", "warning");
                        session.setAttribute("toastMsg", "Đơn hàng này không thể thanh toán lại.");
                        resp.sendRedirect(req.getContextPath() + "/order");
                        break;
                    }

                    String paymentUrl = VNPayService.getPaymentUrl(req, resp, orderId, order.getFinalAmount());
                    resp.sendRedirect(paymentUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Không thể khởi tạo lại thanh toán. Vui lòng thử lại sau.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                }
                break;
            }
            default:
                resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
}
