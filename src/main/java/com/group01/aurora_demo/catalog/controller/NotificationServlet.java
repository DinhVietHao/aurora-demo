package com.group01.aurora_demo.catalog.controller;

import java.util.List;
import jakarta.servlet.http.*;
import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.model.Notification;
import com.group01.aurora_demo.catalog.dao.NotificationDAO;

public class NotificationServlet extends HttpServlet {
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;
            if (user != null) {
                List<Notification> listNotifications = notificationDAO.getNotificationsForCustomer(user.getUserID());
                for (Notification n : listNotifications) {
                    n.setTimeAgo(formatTimeAgo(n.getCreatedAt()));
                }
                request.setAttribute("listNotifications", listNotifications);
                request.setAttribute("totalNotifications", listNotifications.size());
            }

            // Tiếp tục xử lý servlet con
            super.service(request, response);
        } catch (Exception e) {
            System.out.println("[ERROR] catalog/controller/NotificationServlet: " + e.getMessage());
        }
    }

    private String formatTimeAgo(java.sql.Timestamp createdAt) {
        long diffMillis = System.currentTimeMillis() - createdAt.getTime();
        long diffMinutes = diffMillis / (60 * 1000);
        if (diffMinutes < 1)
            return "Vừa xong";
        if (diffMinutes < 60)
            return diffMinutes + " phút trước";
        long diffHours = diffMinutes / 60;
        if (diffHours < 24)
            return diffHours + " giờ trước";
        long diffDays = diffHours / 24;
        return diffDays + " ngày trước";
    }
}
