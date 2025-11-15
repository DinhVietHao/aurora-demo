package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.ShopDAO;
import com.group01.aurora_demo.admin.model.Shop;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ShopManagementServlet", urlPatterns = {"/admin/shops", "/admin/shops/detail"})
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
     * Handle shop status update
     */
    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid shop ID");
            return;
        }

        // Get status parameter
        String status = param(req, "status");
        
        // Validate status
        if (status.isEmpty()) {
            req.setAttribute("error", "Status is required.");
            showDetail(req, resp);
            return;
        }

        // Update only the status
        dao.updateStatus(id, status, null);

        // Set success message and redirect
        req.getSession().setAttribute("message", "Cập nhật trạng thái thành công!");
        req.getSession().setAttribute("messageType", "success");
        resp.sendRedirect(req.getContextPath() + "/admin/shops/detail?id=" + id);
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

