package com.group01.aurora_demo.shop.controller;

import com.group01.aurora_demo.profile.model.Address;
import jakarta.servlet.annotation.MultipartConfig;
import com.group01.aurora_demo.shop.dao.ShopDAO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.ImageDAO;
import com.group01.aurora_demo.shop.model.Shop;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.PrintWriter;
import org.json.JSONObject;

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
            ShopDAO shopDAO = new ShopDAO();
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
            } else if (action.equalsIgnoreCase("uploadAvatar")) {
                Long shopID = shopDAO.getShopIdByUserId(user.getId());
                Part filePart = request.getPart("shopLogo");
                if (filePart == null || filePart.getSize() == 0) {
                    json.put("success", false);
                    json.put("message", "Ảnh không tồn tại hoặc chưa chọn ảnh.");
                    out.print(json.toString());
                    return;
                }
                if (filePart.getSize() > 5 * 1024 * 1024) {
                    json.put("success", false);
                    json.put("message", "Ảnh vượt quá dung lượng cho phép (5MB).");
                    out.print(json.toString());
                    return;
                }

                String contentType = filePart.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    json.put("success", false);
                    json.put("message", "Tệp tải lên không phải hình ảnh hợp lệ.");
                    out.print(json.toString());
                    return;
                }
                String uploadDir = request.getServletContext().getRealPath("/assets/images/catalog/avatars");
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();

                }
                ImageDAO imageDAO = new ImageDAO();
                String fileName = imageDAO.uploadAvatar(filePart, uploadDir);

                if (shopDAO.updateAvatarShop(shopID, fileName)) {
                    json.put("success", true);
                    json.put("message", "Upload avatar thành công.");
                } else {
                    json.put("success", false);
                    json.put("message", "Upload avatar thất bại.");
                }
                out.print(json.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
