package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
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
        String voucherCode = request.getParameter("voucherCode");

        if ("delete_success".equals(message)) {
            request.setAttribute("successMessage", "Voucher đã được xóa thành công!");
        }
        if ("delete_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể xóa voucher này vì (đã dùng, hết hạn hoặc gắn với đơn hàng).");
        }
        if ("create_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Đã thêm voucher thành công.");
        }
        if ("create_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Thêm voucher thất bại");
        }
        if ("message_update".equals(error)) {
            request.setAttribute("errorMessage",
                    "Voucher đang hoạt động và đã có người sử dụng. Không thể cập nhật.");
        }
        if ("update_success".equals(message)) {
            request.setAttribute("successMessage",
                    "Voucher " + voucherCode + " đã được cập nhật thành công!");
        }
        if ("update_failed".equals(error)) {
            request.setAttribute("errorMessage",
                    "Không thể cập nhật voucher " + voucherCode + " !");
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
                    try {
                        long voucherId = Long.parseLong(request.getParameter("voucherID"));
                        Voucher voucher = voucherDAO.getVoucherByVoucherID(voucherId);

                        if (voucher == null) {
                            request.setAttribute("errorMessage", "Không tìm thấy voucher.");
                            request.getRequestDispatcher("/WEB-INF/views/shop/voucherList.jsp")
                                    .forward(request, response);
                            return;
                        }

                        request.setAttribute("voucher", voucher);
                        request.getRequestDispatcher("/WEB-INF/views/shop/voucherDetail.jsp")
                                .forward(request, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Lỗi hệ thống khi tải voucher.");
                        request.getRequestDispatcher("/WEB-INF/views/shop/voucherList.jsp")
                                .forward(request, response);
                    }
                    break;
                case "create":
                    request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request, response);
                    break;
                case "update":
                    try {
                        Long voucherId = Long.parseLong(request.getParameter("voucherID"));
                        Voucher voucher = voucherDAO.getVoucherByVoucherID(voucherId);

                        if (voucher == null) {
                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&error=not_found");
                            return;
                        }

                        int usageCount = voucherDAO.getUsageCount(voucherId);
                        boolean disableAll = false;
                        boolean restrictToDescription = false;
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime startAt = voucher.getStartAt().toLocalDateTime();
                        LocalDateTime endAt = voucher.getEndAt().toLocalDateTime();
                        String status;
                        if (now.isBefore(startAt)) {
                            status = "UPCOMING";
                        } else if (now.isAfter(endAt)) {
                            status = "EXPIRED";
                        } else {
                            status = "ACTIVE";
                        }
                        voucher.setStatus(status);
                        switch (status) {
                            case "UPCOMING":
                                break;

                            case "ACTIVE":
                                if (usageCount > 0) {
                                    disableAll = true;
                                    request.setAttribute("errorMessage",
                                            "Voucher đang hoạt động và đã có người sử dụng. Không thể cập nhật.");
                                }
                                break;

                            case "EXPIRED":
                                restrictToDescription = true;
                                request.setAttribute("errorMessage",
                                        "Voucher hết hạng chỉ được cập nhật \"mô tả\" và \"ngày kết thúc\"");
                                break;
                            default:
                                disableAll = true;
                                break;
                        }

                        request.setAttribute("voucher", voucher);
                        request.setAttribute("usageCount", usageCount);
                        request.setAttribute("restrictToDescription", restrictToDescription);
                        request.setAttribute("disableAll", disableAll);

                        request.getRequestDispatcher("/WEB-INF/views/shop/updateVoucher.jsp").forward(request,
                                response);

                    } catch (Exception e) {
                        e.printStackTrace();
                        response.sendRedirect(
                                request.getContextPath() + "/shop/voucher?action=view&error=update_failed");
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
                        voucher.setStatus("UPCOMING");
                        voucher.setShopVoucher(true);
                        voucher.setShopID(shopId);
                        if (voucherDAO.insertVoucher(voucher)) {

                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&message=create_success");
                        } else {
                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&error=create_failed");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request,
                                response);
                    }

                    break;
                case "delete":
                    try {
                        shopId = shopDAO.getShopIdByUserId(user.getId());

                        String voucherCODE = request.getParameter("voucherCode");

                        Long voucherID = voucherDAO.getVoucherIDByVoucherCodeAndShopID(voucherCODE, shopId);

                        boolean deleted = voucherDAO.deleteVoucherByBusinessRule(voucherID);

                        if (deleted) {
                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&message=delete_success");
                        } else {
                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&error=delete_failed");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/shop/createVoucher.jsp").forward(request,
                                response);
                    }
                    break;
                case "update":
                    try {
                        Long voucherId = Long.parseLong(request.getParameter("voucherID"));
                        Voucher voucher = voucherDAO.getVoucherByVoucherID(voucherId);

                        if (voucher == null) {
                            response.sendRedirect(
                                    request.getContextPath() + "/shop/voucher?action=view&error=not_found");
                            return;
                        }

                        int usageCount = voucherDAO.getUsageCount(voucherId);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        LocalDateTime endAt = LocalDateTime.parse(request.getParameter("endDate"), formatter);
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime originalEndAt = voucher.getEndAt().toLocalDateTime();

                        String status;
                        if (now.isBefore(voucher.getStartAt().toLocalDateTime())) {
                            status = "UPCOMING";
                        } else if (now.isAfter(originalEndAt)) {
                            status = "EXPIRED";
                        } else {
                            status = "ACTIVE";
                        }
                        voucher.setStatus(status);

                        boolean allowFullUpdate = false;
                        boolean allowPartialUpdate = false;

                        switch (status) {
                            case "UPCOMING":
                                allowFullUpdate = true;
                                break;
                            case "ACTIVE":
                                if (usageCount == 0) {
                                    allowFullUpdate = true;
                                } else {
                                    response.sendRedirect(
                                            request.getContextPath()
                                                    + "/shop/voucher?action=view&error=message_update");
                                    return;
                                }
                                break;
                            case "EXPIRED":
                                allowPartialUpdate = true;
                                break;
                        }
                        if (allowFullUpdate) {
                            LocalDateTime startAt = LocalDateTime.parse(request.getParameter("startDate"), formatter);
                            voucher.setCode(request.getParameter("voucherCode"));
                            voucher.setDescription(request.getParameter("voucherDescription"));
                            voucher.setDiscountType(request.getParameter("discountType"));
                            voucher.setValue(Double.parseDouble(request.getParameter("discountValue")));

                            String maxDiscountStr = request.getParameter("maxDiscount");
                            voucher.setMaxAmount((maxDiscountStr != null && !maxDiscountStr.isEmpty())
                                    ? Double.parseDouble(maxDiscountStr)
                                    : null);

                            String minOrderStr = request.getParameter("minOrderValue");
                            voucher.setMinOrderAmount((minOrderStr != null && !minOrderStr.isEmpty())
                                    ? Double.parseDouble(minOrderStr)
                                    : null);

                            String usageLimitStr = request.getParameter("usageLimit");
                            voucher.setUsageLimit((usageLimitStr != null && !usageLimitStr.isEmpty())
                                    ? Integer.parseInt(usageLimitStr)
                                    : null);
                            voucher.setStartAt(Timestamp.valueOf(startAt));
                            voucher.setEndAt(Timestamp.valueOf(endAt));
                        } else if (allowPartialUpdate) {
                            voucher.setDescription(request.getParameter("voucherDescription"));
                            voucher.setEndAt(Timestamp.valueOf(endAt));
                            voucher.setStartAt(voucher.getStartAt());
                        }
                        boolean updated;
                        if (status.equals("EXPIRED")) {
                            updated = voucherDAO.updateVoucherExpired(voucher);
                        } else {
                            updated = voucherDAO.updateVoucher(voucher);
                        }

                        if (updated) {
                            response.sendRedirect(request.getContextPath()
                                    + "/shop/voucher?action=view"
                                    + "&message=update_success"
                                    + "&voucherCode=" + URLEncoder.encode(voucher.getCode(), "UTF-8"));
                        } else {
                            response.sendRedirect(request.getContextPath()
                                    + "/shop/voucher?action=view"
                                    + "&error=update_failed"
                                    + "&voucherCode=" + URLEncoder.encode(voucher.getCode(), "UTF-8"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        response.sendRedirect(request.getContextPath()
                                + "/shop/voucher?action=view&error=update_failed");
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
