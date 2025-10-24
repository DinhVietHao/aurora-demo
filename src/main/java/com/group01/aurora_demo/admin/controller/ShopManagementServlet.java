package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.ShopDAO;
import com.group01.aurora_demo.admin.model.Shop;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "ShopManagementServlet", urlPatterns = {"/admin/shops", "/admin/shops/detail"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 1024 * 1024 * 5,       // 5 MB
    maxRequestSize = 1024 * 1024 * 10    // 10 MB
)
public class ShopManagementServlet extends HttpServlet {

    private final ShopDAO dao = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Determine action from param or path
        String action = param(req, "action");
        if (action.isEmpty()) {
            String path = req.getServletPath();
            if (path != null && path.endsWith("/detail")) {
                action = "detail";
            } else {
                action = "list";
            }
        }

        switch (action) {
            case "detail":
                showDetail(req, resp);
                break;
            case "list":
            default:
                showList(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        
        try {
            if ("update".equals(action)) {
                handleUpdate(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Show list of shops with filtering and pagination
     */
    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = param(req, "q");
        String status = param(req, "status");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);

        int[] totalRows = new int[1];
        try {
            List<Shop> shops = dao.findAll(q, status, page, pageSize, totalRows);
            List<String> statuses = dao.loadStatuses();
            
            req.setAttribute("shops", shops);
            req.setAttribute("statuses", statuses);
            req.setAttribute("q", q);
            req.setAttribute("status", status);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("total", totalRows[0]);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/shops.jsp").forward(req, resp);
    }

    /**
     * Show detailed information for a single shop
     */
    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid shop ID");
            return;
        }

        try {
            Shop shop = dao.findById(id);
            if (shop == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Shop not found");
                return;
            }

            ShopDAO.PickupAddress pickupAddress = dao.getPickupAddress(id);
            List<String> statuses = dao.loadStatuses();

            req.setAttribute("shop", shop);
            req.setAttribute("pickup", pickupAddress);
            req.setAttribute("shopStatuses", statuses);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/shopInfo.jsp").forward(req, resp);
    }

    /**
     * Handle shop update
     */
    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid shop ID");
            return;
        }

        // Get parameters
        String name = param(req, "name");
        String description = param(req, "description");
        String status = param(req, "status");
        String invoiceEmail = param(req, "invoiceEmail");
        String rejectReason = param(req, "rejectReason");
        
        // Validate required fields
        StringBuilder errors = new StringBuilder();
        if (name.isEmpty()) {
            errors.append("Shop name is required. ");
        }
        if (status.isEmpty()) {
            errors.append("Status is required. ");
        }
        
        // Validate email format if provided
        if (!invoiceEmail.isEmpty() && !isValidEmail(invoiceEmail)) {
            errors.append("Invalid email format. ");
        }
        
        // If there are errors, show form with error messages
        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            
            // Keep entered values
            Shop shop = new Shop();
            shop.setShopId(id);
            shop.setName(name);
            shop.setDescription(description);
            shop.setStatus(status);
            shop.setInvoiceEmail(invoiceEmail);
            shop.setRejectReason(rejectReason);
            
            try {
                // Get original data for fields not in form
                Shop originalShop = dao.findById(id);
                if (originalShop != null) {
                    shop.setAvatarUrl(originalShop.getAvatarUrl());
                    shop.setCreatedAt(originalShop.getCreatedAt());
                }
                
                ShopDAO.PickupAddress pickupAddress = dao.getPickupAddress(id);
                List<String> statuses = dao.loadStatuses();
                
                req.setAttribute("shop", shop);
                req.setAttribute("pickup", pickupAddress);
                req.setAttribute("shopStatuses", statuses);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            
            req.getRequestDispatcher("/WEB-INF/views/admin/shopInfo.jsp").forward(req, resp);
            return;
        }

        // Update basic information
        dao.update(id, name, description, status, invoiceEmail, rejectReason);

        // Handle avatar upload if present
        Part avatarPart = req.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {
            // Validate file type and size
            String fileName = getSubmittedFileName(avatarPart);
            if (fileName != null && !fileName.isEmpty()) {
                if (!isValidImageFile(fileName)) {
                    req.setAttribute("error", "Invalid image file type. Please use PNG, JPG, JPEG, or WEBP.");
                    showDetail(req, resp);
                    return;
                }
                
                if (avatarPart.getSize() > 1024 * 1024 * 5) { // 5MB limit
                    req.setAttribute("error", "Image file is too large. Maximum size is 5MB.");
                    showDetail(req, resp);
                    return;
                }
                
                String avatarUrl = saveUploadedFile(avatarPart, req);
                if (avatarUrl != null) {
                    dao.updateAvatar(id, avatarUrl);
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/shops/detail?id=" + id);
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validate image file type
     */
    private boolean isValidImageFile(String fileName) {
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".png") || 
               fileName.endsWith(".jpg") || 
               fileName.endsWith(".jpeg") || 
               fileName.endsWith(".webp");
    }

    /**
     * Save uploaded file and return the URL/path
     */
    private String saveUploadedFile(Part filePart, HttpServletRequest req) throws IOException {
        String fileName = getSubmittedFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        // Generate unique filename
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }
        String uniqueFileName = "shop_" + UUID.randomUUID().toString() + fileExtension;

        // Get upload directory path
        String uploadPath = req.getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "images" + File.separator + "shops";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Save file
        Path filePath = Paths.get(uploadPath, uniqueFileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Return relative URL
        return req.getContextPath() + "/assets/images/shops/" + uniqueFileName;
    }

    /**
     * Extract filename from Part
     */
    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String content : contentDisposition.split(";")) {
                if (content.trim().startsWith("filename")) {
                    return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }

    // Helper methods
    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static long parseLong(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }
}

