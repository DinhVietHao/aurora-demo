package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

@WebServlet("/shop/voucher")
public class VoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";
            
        String message = request.getParameter("message");
        String error = request.getParameter("error");

        if ("delete_success".equals(message)) {
            request.setAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        }
        if ("delete_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể xóa sản phẩm vì đang trong Flash Sale hoặc đang được giao hàng.");
        }
        if ("create_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Đã thêm sản phẩm thành công.");
        }
        try {
            VoucherDAO voucherDAO = new VoucherDAO();
            ShopDAO shopDAO = new ShopDAO();
            switch (action) {
                case "view":
                    long shopId = shopDAO.getShopIdByUserId(user.getId());

                    Map<String, Integer> stats = voucherDAO.getVoucherStatsByShop(shopId);
                    List<Voucher> listVoucher = voucherDAO.getAllVouchersByShopId(shopId);

                    request.setAttribute("stats", stats);
                    request.setAttribute("listVoucher", listVoucher);
                    request.getRequestDispatcher("/WEB-INF/views/shop/voucherManage.jsp").forward(request, response);
                    break;
                case "detail":
                    String voucherCode = request.getParameter("voucherCode");
                    Voucher voucher = voucherDAO.getVoucherByVoucherCode(voucherCode);
                    request.setAttribute("voucher", voucher);
                    request.getRequestDispatcher("/WEB-INF/views/shop/voucherDetail.jsp").forward(request, response);
                    break;
                case "create":
                    request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request, response);
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
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";
        ShopDAO shopDAO = new ShopDAO();
        VoucherDAO voucherDAO = new VoucherDAO();
        Long shopId = (long) 0;
        try {
            switch (action) {
                case "checkVoucherCode":
                    shopId = shopDAO.getShopIdByUserId(user.getId());
                    String voucherCode = request.getParameter("voucherCode");
                    boolean isDuplicate = voucherDAO.checkVoucherCode(voucherCode, shopId);
                    json.put("success", !isDuplicate);
                    out.print(json.toString());
                    break;
                case "create":
                    try {
                        shopId = shopDAO.getShopIdByUserId(user.getId());

                        Voucher voucher = new Voucher();
                        voucher.setCode(request.getParameter("voucherCode"));
                        voucher.setDescription(request.getParameter("voucherDescription"));
                        voucher.setDiscountType(request.getParameter("discountType"));
                        voucher.setValue(Double.parseDouble(request.getParameter("discountValue")));
                        String maxDiscountStr = request.getParameter("maxDiscount");
                        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
                            voucher.setMaxAmount(Double.parseDouble(maxDiscountStr));
                        }
                        voucher.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderValue")));
                        voucher.setUsageLimit(Integer.parseInt(request.getParameter("usageLimit")));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        LocalDateTime startAt = LocalDateTime.parse(request.getParameter("startDate"), formatter);
                        LocalDateTime endAt = LocalDateTime.parse(request.getParameter("endDate"), formatter);
                        voucher.setStartAt(Timestamp.valueOf(startAt));
                        voucher.setEndAt(Timestamp.valueOf(endAt));
                        voucher.setStatus("ACTIVE");
                        voucher.setShopVoucher(true);
                        voucher.setShopID(shopId);
                        if (voucherDAO.insertVoucher(voucher)) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request,
                                response);
                    }

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
}
