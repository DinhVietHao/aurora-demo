package com.group01.aurora_demo.common.config;

import com.group01.aurora_demo.cart.dao.task.OrderBackgroundTasks;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitialize implements ServletContextListener {

    private OrderBackgroundTasks backgroundTasks;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        backgroundTasks = new OrderBackgroundTasks();
        backgroundTasks.start();
        System.out.println("🚀 Các tác vụ nền (tự động hủy và duyệt trả hàng) đã được khởi động.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (backgroundTasks != null) {
            backgroundTasks.stop();
            System.out.println("🛑 Ứng dụng dừng, tất cả tác vụ nền đã được hủy.");
        }
    }
}
