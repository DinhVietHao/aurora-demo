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

@WebServlet(name = "ShopApprovalServlet", urlPatterns = {"/admin/shops/approval"})
public class ShopApprovalServlet extends HttpServlet {

    private final ShopDAO dao = new ShopDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        long shopId = parseLong(req.getParameter("id"), -1);
        
        if (shopId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid shop ID");
            return;
        }
        
        try {
            Shop shop = dao.findById(shopId);
            if (shop == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Shop not found");
                return;
            }
            
            switch (action) {
                case "approve":
                    handleApprove(shopId, req, resp);
                    break;
                case "reject":
                    handleReject(shopId, req, resp);
                    break;
                case "suspend":
                    handleSuspend(shopId, req, resp);
                    break;
                case "unsuspend":
                    handleUnsuspend(shopId, req, resp);
                    break;
                case "ban":
                    handleBan(shopId, req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    private void handleApprove(long shopId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        // Update shop status to APPROVED
        String nullReason = null;
        dao.updateStatus(shopId, "APPROVED", nullReason);
        addSuccessMessage(req, "Đã duyệt cửa hàng thành công");
        redirectToDetail(shopId, resp, req);
    }
    
    private void handleReject(long shopId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        String rejectReason = param(req, "rejectReason");
        
        if (rejectReason.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập lý do từ chối");
            showShopDetail(shopId, req, resp);
            return;
        }
        
        // Update shop status to REJECTED with reason
        dao.updateStatus(shopId, "REJECTED", rejectReason);
        addSuccessMessage(req, "Đã từ chối cửa hàng");
        redirectToDetail(shopId, resp, req);
    }
    
    private void handleSuspend(long shopId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        String suspendReason = param(req, "suspendReason");
        
        if (suspendReason.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập lý do tạm ngưng");
            showShopDetail(shopId, req, resp);
            return;
        }
        
        // Update shop status to SUSPENDED with reason
        dao.updateStatus(shopId, "SUSPENDED", suspendReason);
        addSuccessMessage(req, "Đã tạm ngưng hoạt động cửa hàng");
        redirectToDetail(shopId, resp, req);
    }
    
    private void handleUnsuspend(long shopId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        // Update shop status back to APPROVED
        String nullReason = null;
        dao.updateStatus(shopId, "APPROVED", nullReason);
        addSuccessMessage(req, "Đã kích hoạt lại cửa hàng");
        redirectToDetail(shopId, resp, req);
    }
    
    private void handleBan(long shopId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        String banReason = param(req, "banReason");
        
        if (banReason.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập lý do cấm vĩnh viễn");
            showShopDetail(shopId, req, resp);
            return;
        }
        
        // Update shop status to BANNED with reason
        dao.updateStatus(shopId, "BANNED", banReason);
        addSuccessMessage(req, "Đã cấm vĩnh viễn cửa hàng");
        redirectToDetail(shopId, resp, req);
    }
    
    private void showShopDetail(long shopId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/admin/shops/detail?id=" + shopId).forward(req, resp);
    }
    
    private void redirectToDetail(long shopId, HttpServletResponse resp, HttpServletRequest req) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/shops/detail?id=" + shopId);
    }
    
    private void addSuccessMessage(HttpServletRequest req, String message) {
        req.getSession().setAttribute("message", message);
        req.getSession().setAttribute("messageType", "success");
    }
    
    // Helper methods
    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
    
    private static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }
}
