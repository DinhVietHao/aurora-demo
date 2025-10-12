package com.group01.aurora_demo.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.group01.aurora_demo.admin.dao.DashboardDAO;
import com.group01.aurora_demo.admin.dao.OrderDAO;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.catalog.dao.ProductDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet{
    
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final UserDAO userDAO = new UserDAO();
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Fetch basic statistics
        int totalOrders = orderDAO.getOrderCount();
        int pendingOrders = orderDAO.getOrderCountByStatus("pending");
        int newOrdersToday = orderDAO.getOrderCountByStatus("pending");
        int totalProducts = productDAO.countProducts();
        int lowStockProducts = dashboardDAO.getLowStockProductCount(5);
        
        // Revenue statistics
        BigDecimal totalRevenue = orderDAO.getTotalRevenue();
        BigDecimal revenue7Days = orderDAO.getRevenueLastDays(7);
        BigDecimal revenue30Days = orderDAO.getRevenueLastDays(30);
        
        // Percentage change calculation (if previous period data is available)
        double revenueChangePercent = 0;
        if (revenue30Days.compareTo(BigDecimal.ZERO) > 0) {
            double currentPeriod = revenue7Days.doubleValue();
            double previousPeriod = revenue30Days.subtract(revenue7Days).doubleValue() / 3; // Average of previous 3 weeks
            revenueChangePercent = (previousPeriod > 0) ? 
                                   ((currentPeriod - previousPeriod) / previousPeriod) * 100 : 0;
        }
        
        // Order change percentage (simplified)
        double orderChangePercent = 8.2; // In a real system, calculate this
        
        // Product change percentage
        double productChangePercent = -2.1; // In a real system, calculate this
        
        // Get average rating
        double avgRating = dashboardDAO.getAverageProductRating();
        
        // Get daily revenue for chart
        Map<String, BigDecimal> dailyRevenue = orderDAO.getDailyRevenueLast7Days();
        
        // Get recent activities
        List<Map<String, Object>> recentActivities = dashboardDAO.getRecentActivities(5);
        
        // Set attributes for the JSP
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("pendingOrders", pendingOrders);
        req.setAttribute("newOrdersToday", newOrdersToday);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("lowStockProducts", lowStockProducts);
        
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("revenue7Days", revenue7Days);
        req.setAttribute("revenue30Days", revenue30Days);
        
        req.setAttribute("revenueChangePercent", revenueChangePercent);
        req.setAttribute("orderChangePercent", orderChangePercent);
        req.setAttribute("productChangePercent", productChangePercent);
        
        req.setAttribute("avgRating", avgRating);
        req.setAttribute("dailyRevenue", dailyRevenue);
        req.setAttribute("recentActivities", recentActivities);
        
        // Forward to JSP
        req.getRequestDispatcher("/WEB-INF/views/admin/adminDashboard.jsp").forward(req, resp);
    }
}
