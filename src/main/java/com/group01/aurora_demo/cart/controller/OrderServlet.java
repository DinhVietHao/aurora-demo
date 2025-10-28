package com.group01.aurora_demo.cart.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.OrderItemDAO;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.dao.PaymentDAO;
import com.group01.aurora_demo.cart.dao.dto.OrderShopDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.cart.model.Payment;
import com.group01.aurora_demo.cart.service.OrderService;
import com.group01.aurora_demo.cart.service.VNPayService;
import com.group01.aurora_demo.cart.utils.ServiceResponse;
import com.group01.aurora_demo.catalog.controller.NotificationServlet;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.catalog.model.Product;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.common.service.EmailService;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.UserVoucherDAO;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order/*")
public class OrderServlet extends NotificationServlet {
    private OrderService orderService;
    private VoucherDAO voucherDAO;
    private OrderDAO orderDAO;
    private OrderShopDAO orderShopDAO;
    private PaymentDAO paymentDAO;
    private AddressDAO addressDAO;
    private CartItemDAO cartItemDAO;
    private OrderItemDAO orderItemDAO;
    private ProductDAO productDAO;
    private UserVoucherDAO userVoucherDAO;
    private EmailService emailService;

    public OrderServlet() {
        this.orderService = new OrderService();
        this.voucherDAO = new VoucherDAO();
        this.orderDAO = new OrderDAO();
        this.paymentDAO = new PaymentDAO();
        this.orderShopDAO = new OrderShopDAO();
        this.addressDAO = new AddressDAO();
        this.cartItemDAO = new CartItemDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.productDAO = new ProductDAO();
        this.userVoucherDAO = new UserVoucherDAO();
        this.emailService = new EmailService();
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
                List<Order> orders = orderDAO.getOrdersByUserId(user.getId());
                req.setAttribute("orders", orders);
                req.getRequestDispatcher("/WEB-INF/views/customer/order/order.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.equals("/shop")) {
            try {
                long orderId = Long.parseLong(req.getParameter("orderId"));

                // List<OrderShopDTO> orderShops =
                // this.orderShopDAO.getOrderShopsByOrderId(orderId);
                // if (orderShops == null || orderShops.isEmpty()) {
                // resp.sendRedirect(req.getContextPath() + "/order");
                // return;
                // }
                // Map<Long, List<OrderShopDTO>> grouped = orderShops.stream()
                // .collect(Collectors.groupingBy(orderShop -> orderShop.getOrderShopId(),
                // LinkedHashMap::new,
                // Collectors.toList()));
                // req.setAttribute("orderShops", grouped);

                String filePathCancel = getServletContext().getRealPath("/WEB-INF/config/cancel_reasons.json");
                List<Map<String, String>> cancelReasons = loadCancelReasons(filePathCancel);
                req.setAttribute("cancelReasons", cancelReasons);

                String filePathReturn = getServletContext().getRealPath("/WEB-INF/config/return_reasons.json");
                List<Map<String, String>> returnReasons = loadCancelReasons(filePathReturn);
                req.setAttribute("returnReasons", returnReasons);

                req.getRequestDispatcher("/WEB-INF/views/customer/order/order-shop.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.equals("/detail")) {
            try {
                long orderShopId = Long.parseLong(req.getParameter("orderShopId"));

                List<OrderShopDTO> orderItems = this.orderItemDAO.getOrderItemsByOrderShopId(orderShopId);
                if (orderItems == null || orderItems.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/order");
                    return;
                }
                double shopSubtotal = orderItems.stream().mapToDouble(orderItem -> orderItem.getSubtotal()).sum();
                req.setAttribute("orderItems", orderItems);
                req.setAttribute("shopSubtotal", shopSubtotal);
                req.getRequestDispatcher("/WEB-INF/views/customer/order/order-detail.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.equals("/payment")) {
            String responseCode = req.getParameter("vnp_ResponseCode");
            String txnRef = req.getParameter("vnp_TxnRef");
            String transactionNo = req.getParameter("vnp_TransactionNo");
            try {
                String groupOrderCode = txnRef;
                if ("00".equals(responseCode)) {

                    this.paymentDAO.updatePaymentStatus(groupOrderCode, "SUCCESS", transactionNo);
                    orderShopDAO.updateOrderShopStatusByGroupOrderCode(groupOrderCode, "PENDING");
                    try {
                        List<OrderShopDTO> orderShops = this.orderShopDAO.getOrderShopsByOrderShopId(groupOrderCode);
                        req.setAttribute("orderShops", orderShops);

                        String html = renderJSPToString(req, resp,
                                "/WEB-INF/views/customer/order/order_confirmation.jsp");

                        String subject = "Xác nhận đơn hàng #" + groupOrderCode + " - Aurora";
                        this.emailService.sendHtml(user.getEmail(), subject, html);

                        System.out.println("Email xác nhận đã gửi tới " + user.getEmail());
                    } catch (Exception mailEx) {
                        mailEx.printStackTrace();
                        System.err.println("Gửi email xác nhận thất bại!");
                    }

                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastMsg",
                            "Thanh toán thành công! Đơn hàng của bạn đang chờ xác nhận từ người bán.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                } else {
                    this.paymentDAO.updatePaymentStatus(groupOrderCode, "FAILED", transactionNo);
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
                        String paymentUrl = VNPayService.getPaymentUrl(req, resp, result.getGroupOrderCode(),
                                result.getFinalAmount());

                        int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                        session.setAttribute("cartCount", cartCount);
                        json.put("cartCount", cartCount);
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

                    // String paymentUrl = VNPayService.getPaymentUrl(req, resp, orderId,
                    // order.getFinalAmount());
                    // resp.sendRedirect(paymentUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Không thể khởi tạo lại thanh toán. Vui lòng thử lại sau.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                }
                break;
            }
            case "/cancel": {
                Connection conn = null;
                try {
                    // --- Bắt đầu transaction ---
                    conn = DataSourceProvider.get().getConnection();
                    conn.setAutoCommit(false);

                    Long orderShopId = Long.parseLong(req.getParameter("orderShopId"));
                    String cancelReason = req.getParameter("cancelReason");

                    OrderShop orderShop = orderShopDAO.findById(conn, orderShopId);
                    if (orderShop == null) {
                        session.setAttribute("toastType", "error");
                        session.setAttribute("toastMsg", "Không tìm thấy đơn hàng shop.");
                        resp.sendRedirect(req.getContextPath() + "/order");
                        return;
                    }

                    orderShop.setStatus("CANCELLED");
                    orderShop.setCancelReason(cancelReason);
                    orderShopDAO.update(conn, orderShop);

                    if (orderShop.getVoucherShopId() != null) {
                        userVoucherDAO.cancelUserVoucher(conn, orderShop.getVoucherShopId(), orderShop.getUserId());
                        voucherDAO.decreaseUsageCount(conn, orderShop.getVoucherShopId());
                    }

                    List<OrderItem> items = orderItemDAO.getItemsByOrderShopId(orderShop.getOrderShopId());
                    for (OrderItem item : items) {
                        productDAO.restoreStock(conn, item.getProductId(), item.getQuantity());
                    }

                    // List<OrderShop> activeShop = orderShopDAO.getActiveShopsByOrderId(conn,
                    // order.getOrderId());
                    // Payment payment = paymentDAO.getPaymentByOrderId(conn, order.getOrderId());
                    // if (activeShop.isEmpty()) {
                    // order.setOrderStatus("CANCELLED");
                    // if (order.getVoucherDiscountId() != null) {
                    // userVoucherDAO.cancelUserVoucher(conn, order.getVoucherDiscountId(),
                    // order.getUserId());
                    // voucherDAO.decreaseUsageCount(conn, order.getVoucherDiscountId());
                    // }
                    // if (order.getVoucherShipId() != null) {
                    // userVoucherDAO.cancelUserVoucher(conn, order.getVoucherShipId(),
                    // order.getUserId());
                    // voucherDAO.decreaseUsageCount(conn, order.getVoucherShipId());
                    // }
                    // if (payment != null) {
                    // paymentDAO.partialRefund(conn, order.getOrderId(),
                    // orderShop.getFinalAmount());
                    // }
                    // orderDAO.update(conn, order);
                    // } else {
                    // if (payment != null) {
                    // paymentDAO.partialRefund(conn, order.getOrderId(),
                    // orderShop.getFinalAmount());
                    // }
                    // }

                    // conn.commit();
                    // session.setAttribute("toastType", "success");
                    // session.setAttribute("toastMsg", "Đã hủy đơn hàng shop thành công.");
                    // resp.sendRedirect(req.getContextPath() + "/order/shop?orderId=" +
                    // order.getOrderId());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Có lỗi xảy ra khi huỷ đơn hàng.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                } finally {
                    if (conn != null)
                        try {
                            conn.close();
                        } catch (SQLException ignore) {
                        }
                }
                break;
            }
            case "/confirm": {
                try {
                    Long orderShopId = Long.parseLong(req.getParameter("orderShopId"));

                    boolean success = orderShopDAO.updateOrderShopStatus(orderShopId, "COMPLETED");

                    if (success) {
                        session.setAttribute("toastType", "success");
                        session.setAttribute("toastMsg", "Bạn đã xác nhận đã nhận hàng thành công.");
                    } else {
                        session.setAttribute("toastType", "error");
                        session.setAttribute("toastMsg", "Không thể xác nhận đơn hàng. Vui lòng thử lại.");
                    }

                    String referer = req.getHeader("Referer");
                    if (referer != null) {
                        resp.sendRedirect(referer);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/order");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Có lỗi xảy ra khi xác nhận đơn hàng.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                }
                break;
            }
            case "/return": {
                try {
                    Long orderShopId = Long.parseLong(req.getParameter("orderShopId"));
                    String returnReason = req.getParameter("returnReason");

                    boolean success = orderShopDAO.returnOrderShop(orderShopId, returnReason);

                    if (success) {
                        session.setAttribute("toastType", "success");
                        session.setAttribute("toastMsg", "Đã gửi yêu cầu trả hàng thành công.");
                    } else {
                        session.setAttribute("toastType", "error");
                        session.setAttribute("toastMsg", "Không thể trả hàng. Vui lòng thử lại.");
                    }

                    String referer = req.getHeader("Referer");
                    if (referer != null) {
                        resp.sendRedirect(referer);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/order");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Có lỗi xảy ra khi trả hàng.");
                    resp.sendRedirect(req.getContextPath() + "/order");
                }
                break;
            }
            case "/repurchase": {
                try {
                    Long orderShopId = Long.parseLong(req.getParameter("orderShopId"));
                    List<OrderItem> items = orderItemDAO.getItemsByOrderShopId(orderShopId);

                    List<String> errors = new ArrayList<>();

                    for (OrderItem item : items) {
                        Product product = productDAO.getBasicProductById(item.getProductId());

                        if (product == null) {
                            errors.add("Không tìm thấy sản phẩm ID: " + item.getProductId());
                            continue;
                        }

                        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                            errors.add("Sản phẩm '" + product.getTitle() + "' hiện không được bán.");
                            continue;
                        }

                        if (product.getQuantity() == null || product.getQuantity() <= 0) {
                            errors.add("Sản phẩm '" + product.getTitle() + "' đã hết hàng.");
                            continue;
                        }

                        CartItem existingItem = cartItemDAO.getCartItem(user.getId(), item.getProductId());

                        if (existingItem != null) {
                            int newQuantity = existingItem.getQuantity() + item.getQuantity();
                            if (newQuantity > product.getQuantity()) {
                                errors.add("Sản phẩm '" + product.getTitle()
                                        + "' không đủ số lượng để thêm vào giỏ hàng.");
                                continue;
                            }
                            existingItem.setQuantity(newQuantity);
                            cartItemDAO.updateQuantity(existingItem);
                        } else {
                            CartItem newItem = new CartItem();
                            newItem.setUserId(user.getId());
                            newItem.setProductId(item.getProductId());
                            newItem.setQuantity(item.getQuantity());
                            newItem.setUnitPrice(product.getSalePrice());
                            cartItemDAO.addCartItem(newItem);
                        }
                    }
                    int cartCount = cartItemDAO.getDistinctItemCount(user.getId());
                    session.setAttribute("cartCount", cartCount);

                    if (errors.isEmpty()) {
                        json.put("success", true);
                        json.put("cartCount", cartCount);
                    } else {
                        json.put("success", false);
                        json.put("type", "warning");
                        json.put("title", "Có lỗi với một số sản phẩm");
                        json.put("cartCount", cartCount);
                        json.put("messages", errors);
                    }

                    out.print(json.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("type", "error");
                    json.put("title", "Lỗi hệ thống");
                    json.put("message", "Không thể mua lại lúc này.");
                    out.print(json.toString());
                }
                break;
            }

            default:
                resp.sendRedirect(req.getContextPath() + "/checkout");
        }

    }

    public List<Map<String, String>> loadCancelReasons(String filePath) {
        List<Map<String, String>> reasons = new ArrayList<>();
        try (InputStream is = new FileInputStream(filePath)) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                map.put("code", obj.getString("code"));
                map.put("label", obj.getString("label"));
                reasons.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reasons;
    }

    public void writeJsonError(JSONObject json, PrintWriter out, String type, String title, String message) {
        try {
            json.put("success", false);
            json.put("type", type);
            json.put("title", title);
            json.put("message", message);
            out.print(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String renderJSPToString(HttpServletRequest req, HttpServletResponse resp, String jspPath)
            throws ServletException, IOException {

        StringWriter stringWriter = new StringWriter();
        RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);

        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(resp) {
            private final PrintWriter writer = new PrintWriter(stringWriter);

            @Override
            public PrintWriter getWriter() {
                return writer;
            }
        };

        dispatcher.include(req, responseWrapper);
        return stringWriter.toString();
    }

}