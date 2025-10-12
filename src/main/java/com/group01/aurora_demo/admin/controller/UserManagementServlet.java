package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * UserManagementServlet handles admin operations for user accounts
 * - List all users with pagination and filtering
 * - Lock/unlock user accounts
 * - Search users by name or email
 * - Filter users by status (active/locked) and role (customer/shop owner)
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/admin/users"})
public class UserManagementServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        
        if ("toggle-status".equals(action)) {
            toggleUserStatus(req, resp);
            return;
        }
        
        // Default action: list users
        showUserList(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        
        if ("toggle-status".equals(action)) {
            toggleUserStatus(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    /**
     * Display list of users with filtering and search
     */
    private void showUserList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get filter parameters
        String searchKeyword = param(req, "q");
        String status = param(req, "status");
        String role = param(req, "role");
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 10);
        
        // Fetch users based on filters
        List<User> users;
        int totalUsers;
        
        if (searchKeyword.isEmpty() && status.isEmpty() && role.isEmpty()) {
            // No filters - get all users
            users = userDAO.getAllUsers(page, pageSize);
            totalUsers = userDAO.countUsers();
        } else {
            // Apply filters
            users = userDAO.searchUsers(searchKeyword, status, role, page, pageSize);
            totalUsers = userDAO.countSearchResults(searchKeyword, status, role);
        }
        
        // Set attributes for JSP
        req.setAttribute("users", users);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("total", totalUsers);
        req.setAttribute("q", searchKeyword);
        req.setAttribute("status", status);
        req.setAttribute("role", role);
        
        // Forward to the view
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }
    
    /**
     * Toggle user account status (lock/unlock)
     */
    private void toggleUserStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userID = parseLong(req.getParameter("id"), -1);
        if (userID <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid user ID");
            return;
        }
        
        boolean success = userDAO.toggleUserStatus(userID);
        if (!success) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update user status");
            return;
        }
        
        // Redirect back to the user list
        resp.sendRedirect(req.getContextPath() + "/admin/users");
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
