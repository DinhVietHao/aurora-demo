package com.group01.aurora_demo.catalog.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.dao.NotificationDAO;
import com.group01.aurora_demo.catalog.model.Notification;

public abstract class BaseServlet extends HttpServlet {

    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("AUTH_USER") : null;

        if (user != null) {
            try {
                List<Notification> listNotifications = notificationDAO.getNotificationsForCustomer(user.getUserID());
                for (Notification n : listNotifications) {
                    n.setTimeAgo(formatTimeAgo(n.getCreatedAt()));
                }

                request.setAttribute("listNotifications", listNotifications);
                request.setAttribute("totalNotifications", listNotifications.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tiếp tục xử lý servlet con
        super.service(request, response);
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