package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.FlashSaleDAO;
import com.group01.aurora_demo.admin.model.FlashSaleItemView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "FlashSaleApprovalServlet", urlPatterns = {"/admin/flash-sales/approval"})
public class FlashSaleApprovalServlet extends HttpServlet {

    private final FlashSaleDAO dao = new FlashSaleDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = param(req, "status");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);

        try {
            int[] totalRows = new int[1];
            List<FlashSaleItemView> items = dao.findApprovalItems(status, page, pageSize, totalRows);
            List<String> approvalStatuses = dao.loadApprovalStatuses();

            req.setAttribute("items", items);
            req.setAttribute("approvalStatuses", approvalStatuses);
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

        req.getRequestDispatcher("/WEB-INF/views/admin/flash_sale_approval.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        
        try {
            if ("approve".equals(action)) {
                handleApprove(req, resp);
            } else if ("reject".equals(action)) {
                handleReject(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long flashSaleItemId = parseLong(req.getParameter("id"), -1);
        
        if (flashSaleItemId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing flash sale item id");
            return;
        }
        
        // Verify this item is not a duplicate product
        boolean isAlreadyApproved = dao.checkIfProductAlreadyApproved(flashSaleItemId);
        
        if (isAlreadyApproved) {
            req.getSession().setAttribute("errorMessage", "Sản phẩm này đã được duyệt trong đợt Flash Sale khác.");
        } else {
            int result = dao.updateApprovalStatus(flashSaleItemId, "APPROVED");
            
            if (result > 0) {
                req.getSession().setAttribute("successMessage", "Đã duyệt sản phẩm thành công!");
            } else {
                req.getSession().setAttribute("errorMessage", "Không thể duyệt sản phẩm. Vui lòng thử lại.");
            }
        }
        
        // Redirect back to approval page with current filter
        String redirectUrl = req.getContextPath() + "/admin/flash-sales/approval";
        String status = param(req, "status");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);
        
        if (!status.isEmpty()) {
            redirectUrl += "?status=" + java.net.URLEncoder.encode(status, "UTF-8");
            redirectUrl += "&page=" + page;
            redirectUrl += "&pageSize=" + pageSize;
        }
        
        resp.sendRedirect(redirectUrl);
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long flashSaleItemId = parseLong(req.getParameter("id"), -1);
        
        if (flashSaleItemId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing flash sale item id");
            return;
        }

        int result = dao.updateApprovalStatus(flashSaleItemId, "REJECTED");
        
        if (result > 0) {
            req.getSession().setAttribute("successMessage", "Đã từ chối sản phẩm thành công!");
        } else {
            req.getSession().setAttribute("errorMessage", "Không thể từ chối sản phẩm. Vui lòng thử lại.");
        }
        
        // Redirect back to approval page with current filter
        String redirectUrl = req.getContextPath() + "/admin/flash-sales/approval";
        String status = param(req, "status");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);
        
        if (!status.isEmpty()) {
            redirectUrl += "?status=" + java.net.URLEncoder.encode(status, "UTF-8");
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
}
