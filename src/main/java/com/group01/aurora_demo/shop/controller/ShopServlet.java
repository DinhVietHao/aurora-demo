package com.group01.aurora_demo.shop.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;

import com.group01.aurora_demo.shop.model.Address;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import java.io.PrintWriter;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.model.Shop;

@WebServlet("/shop")
@MultipartConfig
public class ShopServlet extends HttpServlet {

    private ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject json = new JSONObject();

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("AUTH_USER");

            if (user == null) {
                json.put("status", "NOT_LOGGED_IN");
                json.put("message", "Vui lòng đăng nhập.");
                out.print(json.toString());
                return;
            }

            String action = request.getParameter("action");
            if (action.equalsIgnoreCase("check-status")) {
                Shop shop = shopDAO.getShopByUserId(user.getId());
                if (shop != null) {
                    switch (shop.getStatus()) {
                        case "PENDING":
                            json.put("status", "PENDING");
                            json.put("message", "Đang duyệt… sắp bán được rồi ✨");
                            break;
                        case "REJECTED":
                            json.put("status", "REJECTED");
                            json.put("message", "Shop bị từ chối ❌ - Lý do: " + shop.getRejectReason());
                            break;
                        case "ACTIVE":
                            json.put("status", "ACTIVE");
                            json.put("redirect", request.getContextPath() + "/shop?action=dashboard");
                            break;
                    }
                } else {
                    json.put("status", "NONE");
                    json.put("message", "Chia sẻ đam mê sách, tạo thu nhập dễ dàng. Hãy mở shop sách online của bạn!");
                    json.put("redirect", request.getContextPath() + "/shop?action=register");
                }
                out.print(json.toString());
            } else if (action.equalsIgnoreCase("register")) {
                request.getRequestDispatcher("/WEB-INF/views/shop/registerShop.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("dashboard")) {
                // ... Lấy dữ liệu chuyển sang dashboard thống kê
                request.getRequestDispatcher("/WEB-INF/views/shop/shopDashboard.jsp").forward(request, response);
            } else {
                json.put("status", "ERROR");
                json.put("message", "Action không hợp lệ.");
                out.print(json.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject json = new JSONObject();

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("AUTH_USER");
            if (user == null) {
                json.put("success", false);
                json.put("message", "Phiên đăng nhập đã hết hạn.");
                out.print(json.toString());
                return;
            }

            String action = request.getParameter("action");
            if (action.equalsIgnoreCase("register")) {
                String avatarUrl = null; // Need additional
                String city = request.getParameter("city");
                String ward = request.getParameter("ward");
                String phone = request.getParameter("phone");
                String shopName = request.getParameter("shopName");
                String shopDesc = request.getParameter("shopDesc");
                String invoiceEmail = request.getParameter("email");
                String recipientName = request.getParameter("fullname");
                String addressLine = request.getParameter("addressLine");

                if (shopDAO.isShopNameExists(shopName)) {
                    json.put("success", false);
                    json.put("message", "Tên shop đã tồn tại.");
                    out.print(json.toString());
                    return;
                }

                Address pickupAddress = new Address();
                pickupAddress.setCity(city);
                pickupAddress.setWard(ward);
                pickupAddress.setPhone(phone);
                pickupAddress.setDescription(addressLine);
                pickupAddress.setRecipientName(recipientName);

                Shop shop = new Shop();
                shop.setName(shopName);
                shop.setAvatarUrl(avatarUrl);
                shop.setDescription(shopDesc);
                shop.setStatus("PENDING");
                shop.setOwnerUserId(user.getId());
                shop.setInvoiceEmail(invoiceEmail);

                if (shopDAO.createShop(shop, pickupAddress)) {
                    json.put("success", true);
                    json.put("message", "Đăng ký shop thành công! Chờ phê duyệt.");
                } else {
                    json.put("success", false);
                    json.put("message", "Đăng ký thất bại.");
                }

                out.print(json.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
