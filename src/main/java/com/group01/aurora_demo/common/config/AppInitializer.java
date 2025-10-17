package com.group01.aurora_demo.common.config;

import com.group01.aurora_demo.cart.dao.task.OrderAutoCancelTask;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new OrderAutoCancelTask().start();
        System.out.println("🚀 Tác vụ tự động kiểm tra đơn hàng quá hạn đã khởi động.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Ứng dụng dừng, hủy tác vụ kiểm tra đơn hàng.");
    }
}
