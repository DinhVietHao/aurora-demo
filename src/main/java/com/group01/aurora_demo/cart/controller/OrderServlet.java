package com.group01.aurora_demo.cart.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.dto.OrderDTO;
import com.group01.aurora_demo.cart.service.OrderService;
import com.group01.aurora_demo.cart.service.VNPayService;
import com.group01.aurora_demo.cart.utils.ServiceResponse;
import com.group01.aurora_demo.catalog.dao.CategoryDAO;
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
    private CategoryDAO categoryDAO;

    public OrderServlet() {
        this.orderService = new OrderService();
        this.voucherDAO = new VoucherDAO();
        this.orderDAO = new OrderDAO();
        this.categoryDAO = new CategoryDAO();
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
                        .collect(Collectors.groupingBy(order -> order.getShopId()));
                req.setAttribute("orders", grouped);
                req.getRequestDispatcher("/WEB-INF/views/customer/order/order.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.equals("/payment")) {
            if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                String vnp_TransactionNo = req.getParameter("vnp_TransactionNo");
                String vnp_Amount = req.getParameter("vnp_Amount");

            } else {

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
                    Address address = new Address();
                    address.setAddressId(addressId);
                    String discountCode = req.getParameter("discountVoucher");
                    String shipCode = req.getParameter("shippingVoucher");

                    Voucher discountVoucher = null;
                    Voucher shipVoucher = null;

                    if (discountCode != null && !discountCode.isEmpty()) {
                        discountVoucher = this.voucherDAO.getVoucherByCode(null, discountCode, false);
                    }
                    if (shipCode != null && !shipCode.isEmpty()) {
                        shipVoucher = this.voucherDAO.getVoucherByCode(null, shipCode, false);
                    }

                    Map<Long, String> shopVouchers = new HashMap<>();
                    req.getParameterMap().forEach((key, values) -> {
                        if (key.startsWith("shopVoucher_")) {
                            Long shopId = Long.parseLong(key.substring("shopVoucher_".length()));
                            shopVouchers.put(shopId, values[0]);
                        }
                    });
                    String paymentUrl = VNPayService.getPaymentUrl(req, resp, 100000);
                    ServiceResponse result = this.orderService.createOrder(user, address, discountVoucher, shipVoucher,
                            shopVouchers);

                    json.put("success", "success".equals(result.getType()));
                    json.put("url", paymentUrl);
                    json.put("type", result.getType());
                    json.put("title", result.getTitle());
                    json.put("message", result.getMessage());

                } catch (Exception e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("message", "lỗi hệ thống!");
                }
                out.print(json.toString());
                break;

            }
            default:
                resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
}
