package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.AdminProductDAO;
import com.group01.aurora_demo.admin.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ProductApprovalServlet", urlPatterns = {"/admin/products/approval"})
public class ProductApprovalServlet extends HttpServlet {

    private final AdminProductDAO productDAO = new AdminProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = param(req, "status", "PENDING");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);

        try {
            int[] totalRows = new int[1];
            List<Product> products = productDAO.findProductsByStatus(status, page, pageSize, totalRows);
            List<String> statuses = productDAO.getProductStatuses();

            req.setAttribute("products", products);
            req.setAttribute("statuses", statuses);
            req.setAttribute("status", status);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("total", totalRows[0]);

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

        req.getRequestDispatcher("/WEB-INF/views/admin/product_approval.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action", "");
        
        try {
            if ("approve".equals(action)) {
                handleApprove(req, resp);
            } else if ("reject".equals(action)) {
                handleReject(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long productId = parseLong(req.getParameter("id"), -1);
        
        if (productId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing product id");
            return;
        }
        
        int result = productDAO.updateProductStatus(productId, "ACTIVE", null);
        
        if (result > 0) {
            req.getSession().setAttribute("successMessage", "Sản phẩm đã được duyệt thành công!");
        } else {
            req.getSession().setAttribute("errorMessage", "Không thể duyệt sản phẩm. Vui lòng thử lại.");
        }
        
        // Redirect back to approval page with current filter
        redirectBack(req, resp);
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long productId = parseLong(req.getParameter("id"), -1);
        String rejectReason = param(req, "rejectReason", "");
        
        if (productId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing product id");
            return;
        }

        if (rejectReason.isEmpty()) {
            req.getSession().setAttribute("errorMessage", "Vui lòng cung cấp lý do từ chối.");
            redirectBack(req, resp);
            return;
        }
        
        int result = productDAO.updateProductStatus(productId, "REJECTED", rejectReason);
        
        if (result > 0) {
            req.getSession().setAttribute("successMessage", "Đã từ chối sản phẩm thành công!");
        } else {
            req.getSession().setAttribute("errorMessage", "Không thể từ chối sản phẩm. Vui lòng thử lại.");
        }
        
        redirectBack(req, resp);
    }
    
    private void redirectBack(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Redirect back to approval page with current filter
        String redirectUrl = req.getContextPath() + "/admin/products/approval";
        String status = param(req, "status", "PENDING");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);
        
        redirectUrl += "?status=" + java.net.URLEncoder.encode(status, "UTF-8");
        redirectUrl += "&page=" + page;
        redirectUrl += "&pageSize=" + pageSize;
        
        resp.sendRedirect(redirectUrl);
    }

    private static String param(HttpServletRequest req, String name, String def) {
        String v = req.getParameter(name);
        return v == null ? def : v.trim();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static long parseLong(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }
}
