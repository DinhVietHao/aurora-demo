package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.FlashSaleDAO;
import com.group01.aurora_demo.admin.model.FlashSale;
import com.group01.aurora_demo.admin.model.FlashSaleItemView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "FlashSalesManagementServlet", urlPatterns = {"/admin/flash-sales", "/admin/flash-sales/detail", "/admin/flash-sales/create", "/admin/flash-sales/edit"})
public class FlashSalesManagementServlet extends HttpServlet {

    private final FlashSaleDAO dao = new FlashSaleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Determine action from param or path
        String action = param(req, "action");
        if (action.isEmpty()) {
            String path = req.getServletPath();
            if (path != null && path.endsWith("/detail")) action = "detail";
            else if (path != null && path.endsWith("/create")) action = "create_form";
            else if (path != null && path.endsWith("/edit")) action = "edit_form";
            else action = "list";
        }

        switch (action) {
            case "detail":
                showDetail(req, resp);
                break;
            case "create_form":
                showCreateForm(req, resp);
                break;
            case "edit_form":
                showEditForm(req, resp);
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
        String path = req.getServletPath();
        if (action.isEmpty()) {
            if (path != null && path.endsWith("/create")) action = "create";
            else if (path != null && path.endsWith("/edit")) action = "update";
        }

        try {
            if ("create".equals(action)) {
                handleCreate(req, resp);
            } else if ("update".equals(action)) {
                handleUpdate(req, resp);
            } else if ("remove_product".equals(action)) {
                handleRemoveProduct(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = param(req, "q");
        String status = param(req, "status");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);

        int[] totalRows = new int[1];
        try {
            List<FlashSale> items = dao.findAll(q, status, page, pageSize, totalRows);
            req.setAttribute("items", items);
            req.setAttribute("statuses", dao.loadStatuses());
            req.setAttribute("q", q);
            req.setAttribute("status", status);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("total", totalRows[0]);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/flash_sales.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id");
            return;
        }
        String name = param(req, "name");
        String publisher = param(req, "publisher");
        String price = param(req, "price");
        String sort = param(req, "sort");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 12);

        try {
            FlashSale fs = dao.findById(id);
            if (fs == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            int[] totalRows = new int[1];
            List<FlashSaleItemView> items = dao.findItems(id, name, publisher, price, sort, page, pageSize, totalRows);

            req.setAttribute("fs", fs);
            req.setAttribute("items", items);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("total", totalRows[0]);
            req.setAttribute("name", name);
            req.setAttribute("publisher", publisher);
            req.setAttribute("price", price);
            req.setAttribute("sort", sort);
            
            // Handle session messages
            String successMessage = (String) req.getSession().getAttribute("successMessage");
            if (successMessage != null) {
                req.setAttribute("successMessage", successMessage);
                req.getSession().removeAttribute("successMessage");
            }
            
            String errorMessage = (String) req.getSession().getAttribute("errorMessage");
            if (errorMessage != null) {
                req.setAttribute("errorMessage", errorMessage);
                req.getSession().removeAttribute("errorMessage");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_detail.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("statuses", dao.loadStatuses());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
        try {
            com.group01.aurora_demo.admin.model.FlashSale fs = dao.findById(id);
            req.setAttribute("fs", fs);
            if (fs != null) {
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                if (fs.getStartAt() != null) {
                    String startLocal = fs.getStartAt().toLocalDateTime().format(fmt);
                    req.setAttribute("startAtLocal", startLocal);
                }
                if (fs.getEndAt() != null) {
                    String endLocal = fs.getEndAt().toLocalDateTime().format(fmt);
                    req.setAttribute("endAtLocal", endLocal);
                }
            }
            req.setAttribute("statuses", dao.loadStatuses());
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_form.jsp").forward(req, resp);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // Get parameters
        String name = param(req, "name");
        String status = param(req, "status");
        String startAtParam = param(req, "startAt");
        String endAtParam = param(req, "endAt");
        
        // Validate required fields
        StringBuilder errors = new StringBuilder();
        if (name.isEmpty()) {
            errors.append("Name is required. ");
        }
        if (status.isEmpty()) {
            errors.append("Status is required. ");
        }
        if (startAtParam.isEmpty()) {
            errors.append("Start date is required. ");
        }
        if (endAtParam.isEmpty()) {
            errors.append("End date is required. ");
        }
        
        // Parse timestamps if provided
        java.sql.Timestamp startAt = null;
        java.sql.Timestamp endAt = null;
        
        if (!startAtParam.isEmpty()) {
            try {
                startAt = parseTimestamp(startAtParam);
            } catch (Exception e) {
                errors.append("Invalid start date format. ");
            }
        }
        
        if (!endAtParam.isEmpty()) {
            try {
                endAt = parseTimestamp(endAtParam);
            } catch (Exception e) {
                errors.append("Invalid end date format. ");
            }
        }
        
        // Validate business rules
        if (startAt != null && endAt != null && endAt.before(startAt)) {
            errors.append("End date must be after start date. ");
        }
        
        // If there are errors, show form with error messages
        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            req.setAttribute("name", name);
            req.setAttribute("status", status);
            req.setAttribute("startAt", startAtParam);
            req.setAttribute("endAt", endAtParam);
            
            try {
                req.setAttribute("statuses", dao.loadStatuses());
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            
            req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_form.jsp").forward(req, resp);
            return;
        }
        
        // All validations passed, proceed with creation
        long id = dao.insert(name, startAt, endAt, status);
        resp.sendRedirect(req.getContextPath() + "/admin/flash-sales/detail?id=" + id);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = parseLong(req.getParameter("id"), -1);
        
        // Validate ID
        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid flash sale ID");
            return;
        }
        
        // Get parameters
        String name = param(req, "name");
        String status = param(req, "status");
        String startAtParam = param(req, "startAt");
        String endAtParam = param(req, "endAt");
        
        // Validate required fields
        StringBuilder errors = new StringBuilder();
        if (name.isEmpty()) {
            errors.append("Name is required. ");
        }
        if (status.isEmpty()) {
            errors.append("Status is required. ");
        }
        if (startAtParam.isEmpty()) {
            errors.append("Start date is required. ");
        }
        if (endAtParam.isEmpty()) {
            errors.append("End date is required. ");
        }
        
        // Parse timestamps if provided
        java.sql.Timestamp startAt = null;
        java.sql.Timestamp endAt = null;
        
        if (!startAtParam.isEmpty()) {
            try {
                startAt = parseTimestamp(startAtParam);
            } catch (Exception e) {
                errors.append("Invalid start date format. ");
            }
        }
        
        if (!endAtParam.isEmpty()) {
            try {
                endAt = parseTimestamp(endAtParam);
            } catch (Exception e) {
                errors.append("Invalid end date format. ");
            }
        }
        
        // Validate business rules
        if (startAt != null && endAt != null && endAt.before(startAt)) {
            errors.append("End date must be after start date. ");
        }
        
        // If there are errors, show form with error messages
        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            req.setAttribute("name", name);
            req.setAttribute("status", status);
            req.setAttribute("startAt", startAtParam);
            req.setAttribute("endAt", endAtParam);
            req.setAttribute("id", id);
            
            try {
                com.group01.aurora_demo.admin.model.FlashSale fs = dao.findById(id);
                req.setAttribute("fs", fs);
                req.setAttribute("statuses", dao.loadStatuses());
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            
            req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_form.jsp").forward(req, resp);
            return;
        }
        
        // All validations passed, proceed with update
        dao.update(id, name, startAt, endAt, status);
        resp.sendRedirect(req.getContextPath() + "/admin/flash-sales/detail?id=" + id);
    }

    private void handleRemoveProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long flashSaleId = parseLong(req.getParameter("flashSaleId"), -1);
        long productId = parseLong(req.getParameter("productId"), -1);
        long flashSaleItemId = parseLong(req.getParameter("flashSaleItemId"), -1);
        
        if (flashSaleId <= 0 || productId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing flashSaleId or productId");
            return;
        }

        // Check if product is in flash sale
        if (!dao.isProductInFlashSale(flashSaleId, productId)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found in flash sale");
            return;
        }
        
        // Check if the item can be deleted (not in ACTIVE status and no related orders)
        if (flashSaleItemId > 0) {
            if (!dao.canDelete(flashSaleItemId)) {
                req.getSession().setAttribute("errorMessage", "Không thể xóa sản phẩm này vì Flash Sale đã bắt đầu hoặc đã có đơn hàng!");
                
                // Redirect back to flash sale detail page
                String redirectUrl = req.getContextPath() + "/admin/flash-sales/detail?id=" + flashSaleId;
                redirectBackWithParams(req, resp, redirectUrl);
                return;
            }
        }

        // Remove product from flash sale
        int result = dao.removeProductFromFlashSale(flashSaleId, productId);
        
        if (result > 0) {
            // Set success message in session
            req.getSession().setAttribute("successMessage", "Đã xóa sản phẩm khỏi flash sale thành công!");
            
            // Redirect back to flash sale detail page
            String redirectUrl = req.getContextPath() + "/admin/flash-sales/detail?id=" + flashSaleId;
            redirectBackWithParams(req, resp, redirectUrl);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to remove product from flash sale");
        }
    }
    
    /**
     * Helper method to redirect back with all filter parameters
     */
    private void redirectBackWithParams(HttpServletRequest req, HttpServletResponse resp, String redirectUrl) 
            throws IOException {
        String name = param(req, "name");
        String publisher = param(req, "publisher");
        String price = param(req, "price");
        String sort = param(req, "sort");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 12);
        
        if (!name.isEmpty() || !publisher.isEmpty() || !price.isEmpty() || !sort.isEmpty()) {
            redirectUrl += "&name=" + java.net.URLEncoder.encode(name, "UTF-8");
            redirectUrl += "&publisher=" + java.net.URLEncoder.encode(publisher, "UTF-8");
            redirectUrl += "&price=" + java.net.URLEncoder.encode(price, "UTF-8");
            redirectUrl += "&sort=" + java.net.URLEncoder.encode(sort, "UTF-8");
            redirectUrl += "&page=" + page;
            redirectUrl += "&pageSize=" + pageSize;
        }
        
        resp.sendRedirect(redirectUrl);
    }

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

    private static java.sql.Timestamp parseTimestamp(String input) {
        if (input == null || input.isEmpty()) return null;
        String norm = input.replace('T', ' ');
        // add seconds if missing
        if (norm.length() == 16) norm += ":00"; // yyyy-MM-dd HH:mm -> add :ss
        return java.sql.Timestamp.valueOf(norm);
    }
}
