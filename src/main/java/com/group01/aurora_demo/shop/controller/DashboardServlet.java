package com.group01.aurora_demo.shop.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.NotificationDAO;
import com.group01.aurora_demo.catalog.model.Notification;
import com.group01.aurora_demo.shop.dao.ShopDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/shop/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("AUTH_USER");
        if (user == null) {
            response.sendRedirect("/home");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        ShopDAO shopDAO = new ShopDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        switch (action) {
            case "view":
                try {
                    long shopId = shopDAO.getShopIdByUserId(user.getId());
                    List<Notification> notifications = notificationDAO.getNotificationsForShop(shopId);
                    for (Notification n : notifications) {
                        n.setTimeAgo(formatTimeAgo(n.getCreatedAt()));
                    }
                    System.out.println("=================================>" + shopId);
                    request.setAttribute("notifications", notifications);
                    request.getRequestDispatcher("/WEB-INF/views/shop/shopDashboard.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ServletException("Lỗi khi lấy danh sách thông báo.", e);
                }
                break;
            default:
                break;
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
}
