package com.group01.aurora_demo.shop.controller;

import com.group01.aurora_demo.common.service.AvatarService;
import com.group01.aurora_demo.catalog.dao.NotificationDAO;
import com.group01.aurora_demo.catalog.model.Notification;
import com.group01.aurora_demo.profile.model.Address;
import jakarta.servlet.annotation.MultipartConfig;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.shop.model.Shop;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import java.time.temporal.ChronoUnit;
import jakarta.servlet.http.Part;
import java.time.LocalDateTime;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

@MultipartConfig
@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    private ShopDAO shopDAO = new ShopDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

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

            Shop shop = null;
            String action = request.getParameter("action");
            if (action.equalsIgnoreCase("check-status")) {
                handleCheckStatusShop(request, response, json, out, user);
            } else if (action.equalsIgnoreCase("register")) {
                request.getRequestDispatcher("/WEB-INF/views/shop/registerShop.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("dashboard")) {
                long shopId = shopDAO.getShopIdByUserId(user.getId());
                List<Notification> notifications = notificationDAO.getNotificationsForShop(shopId);
                for (Notification n : notifications) {
                    n.setTimeAgo(formatTimeAgo(n.getCreatedAt()));
                }
                request.setAttribute("notifications", notifications);
                request.getRequestDispatcher("/WEB-INF/views/shop/shopDashboard.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("shopProfile")) {
                shop = shopDAO.getShopByUserId(user.getId());
                request.setAttribute("shop", shop);
                request.getRequestDispatcher("/WEB-INF/views/shop/shop.jsp").forward(request, response);
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
            switch (action) {
                case "register":
                    handleRegisterShop(request, response, json, out, user);
                    break;

                case "uploadAvatar":
                    handleUploadShopAvatar(request, response, out, json, user);
                    break;
                case "updateProfile":
                    handleUpdateShopProfile(request, response, json, out, user);
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleCheckStatusShop(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user) {
        try {
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
        } catch (Exception e) {
            System.out.println("Error in \"handleCheckStatusShop\" function: " + e.getMessage());
            json.put("message", "Error in \"handleCheckStatusShop\" function: " + e.getMessage());
            json.put("success", false);
        } finally {
            out.print(json.toString());
        }
    }

    @SuppressWarnings("unused")
    private void handleUploadShopAvatar(HttpServletRequest request, HttpServletResponse response,
            PrintWriter out, JSONObject json, User user) throws Exception {
        try {
            Long shopID = shopDAO.getShopIdByUserId(user.getId());
            if (shopID == null) {
                json.put("success", false);
                json.put("message", "Không tìm thấy shop.");
                out.print(json.toString());
                return;
            }

            Part filePart = request.getPart("shopLogo");
            String uploadDir = request.getServletContext().getRealPath("/assets/images/shops");
            String newFilename = AvatarService.uploadAvatar(filePart, uploadDir, "shop");

            Shop shop = shopDAO.getShopByUserId(user.getUserID());
            String oldAvatar = shop != null ? shop.getAvatarUrl() : null;

            if (shopDAO.updateAvatarShop(shopID, newFilename)) {
                if (oldAvatar != null && !oldAvatar.isEmpty()) {
                    AvatarService.deleteOldAvatar(uploadDir, oldAvatar);
                }

                json.put("success", true);
                json.put("message", "Cập nhật logo shop thành công.");
                json.put("avatarUrl", request.getContextPath() + "/assets/images/shops/" + newFilename);
            } else {
                AvatarService.deleteOldAvatar(uploadDir, newFilename);
                json.put("success", false);
                json.put("message", "Không thể cập nhật logo. Vui lòng thử lại.");
            }
            out.print(json.toString());
        } catch (IllegalArgumentException e) {
            json.put("success", false);
            json.put("message", e.getMessage());
            out.print(json.toString());
        } catch (Exception e) {
            System.err.println("[ERROR] ShopServlet#handleUploadShopAvatar: " + e.getMessage());
            json.put("success", false);
            json.put("message", "Đã xảy ra lỗi. Vui lòng thử lại.");
            out.print(json.toString());
        }
    }

    private void handleRegisterShop(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user) {
        try {
            String avatarUrl = null;
            Part shopLogoPart = request.getPart("shopLogo");
            if (shopLogoPart != null && shopLogoPart.getSize() > 0) {
                try {
                    String uploadDir = request.getServletContext().getRealPath("/assets/images/shops");
                    avatarUrl = AvatarService.uploadAvatar(shopLogoPart, uploadDir, "shop");
                } catch (IllegalArgumentException e) {
                    json.put("success", false);
                    json.put("message", "Lỗi upload logo: " + e.getMessage());
                    out.print(json.toString());
                    return;
                }
            }

            String city = request.getParameter("cityName");
            String district = request.getParameter("districtName");
            String ward = request.getParameter("wardName");

            int provinceId = Integer.parseInt(request.getParameter("provinceId"));
            int districtId = Integer.parseInt(request.getParameter("districtId"));
            String wardCode = request.getParameter("wardCode");

            String phone = request.getParameter("phone");
            String shopName = request.getParameter("shopName");
            String shopDesc = request.getParameter("shopDesc");
            String invoiceEmail = request.getParameter("email");
            String recipientName = request.getParameter("fullname");
            String addressLine = request.getParameter("addressLine");

            if (shopDAO.isShopNameExists(shopName)) {
                // Xóa ảnh đã upload nếu tên shop trùng
                if (avatarUrl != null) {
                    String uploadDir = request.getServletContext().getRealPath("/assets/images/shops");
                    AvatarService.deleteOldAvatar(uploadDir, avatarUrl);
                }
                json.put("success", false);
                json.put("message", "Tên shop đã tồn tại.");
                out.print(json.toString());
                return;
            }

            Address pickupAddress = new Address();
            pickupAddress.setCity(city);
            pickupAddress.setDistrict(district);
            pickupAddress.setWard(ward);

            pickupAddress.setProvinceId(provinceId);
            pickupAddress.setDistrictId(districtId);
            pickupAddress.setWardCode(wardCode);

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

            if (shopDAO.createShop(shop, pickupAddress, user)) {
                json.put("success", true);
                json.put("message", "Đăng ký shop thành công! Chờ phê duyệt.");
            } else {
                json.put("success", false);
                json.put("message", "Đăng ký thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleRegisterShop\" function: " + e.getMessage());
            json.put("message", "Error in \"handleRegisterShop\" function: " + e.getMessage());
            json.put("success", false);
        } finally {
            out.print(json.toString());
        }
    }

    private String formatTimeAgo(Timestamp createdAt) {
        if (createdAt == null)
            return "";

        LocalDateTime created = createdAt.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(created, now);

        if (minutes < 1)
            return "vừa xong";
        if (minutes < 60)
            return minutes + " phút trước";
        if (minutes < 1440)
            return (minutes / 60) + " giờ trước";
        if (minutes < 10080)
            return (minutes / 1440) + " ngày trước";
        return (minutes / 10080) + " tuần trước";
    }

    private void handleUpdateShopProfile(HttpServletRequest request, HttpServletResponse response, JSONObject json,
            PrintWriter out, User user) {
        boolean flag = true;
        String message = "";
        try {
            Long shopId = shopDAO.getShopIdByUserId(user.getUserID());
            if (shopId == null || shopId == -1) {
                flag = false;
                message = "Không tìm thấy shop.";
            }

            if (flag) {
                String shopName = request.getParameter("shopName");
                String shopPhone = request.getParameter("shopPhone");
                String shopEmail = request.getParameter("shopEmail");
                String shopDescription = request.getParameter("shopDescription");

                String cityName = request.getParameter("cityName");
                String districtName = request.getParameter("districtName");
                String wardName = request.getParameter("wardName");

                int provinceId = Integer.parseInt(request.getParameter("provinceId"));
                int districtId = Integer.parseInt(request.getParameter("districtId"));
                String wardCode = request.getParameter("wardCode");
                String addressLine = request.getParameter("addressLine");

                if (shopName == null || shopName.trim().isEmpty()) {
                    flag = false;
                    message = "Tên shop không được để trống.";
                }

                Shop currentShop = shopDAO.getShopByUserId(user.getId());
                if (flag && !currentShop.getName().equals(shopName) && shopDAO.isShopNameExists(shopName)) {
                    flag = false;
                    message = "Tên shop đã tồn tại.";
                }

                if (flag) {
                    Shop shop = new Shop();
                    shop.setShopId(shopId);
                    shop.setName(shopName);
                    shop.setDescription(shopDescription);
                    shop.setInvoiceEmail(shopEmail);

                    Address address = new Address();
                    address.setAddressId(currentShop.getPickupAddressId());
                    address.setPhone(shopPhone);
                    address.setCity(cityName);
                    address.setDistrict(districtName);
                    address.setWard(wardName);
                    address.setProvinceId(provinceId);
                    address.setDistrictId(districtId);
                    address.setWardCode(wardCode);
                    address.setDescription(addressLine);
                    address.setRecipientName(currentShop.getPickupAddress().getRecipientName());

                    if (shopDAO.updateShopProfile(shop, address)) {
                        flag = true;
                        message = "Cập nhật thông tin shop thành công!";
                    } else {
                        flag = false;
                        message = "Không thể cập nhật thông tin. Vui lòng thử lại.";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in \"handleUpdateShopProfile\" function: " + e.getMessage());
            flag = false;
            message = "Error in \"handleUpdateShopProfile\" function: " + e.getMessage();
        } finally {
            json.put("success", flag);
            json.put("message", message);
            out.print(json.toString());
        }
    }
}