package com.group01.aurora_demo.common.config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.concurrent.ScheduledExecutorService;
import com.group01.aurora_demo.chatbot.service.DocumentSyncTask;

public class SchedulerConfig implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                new DocumentSyncTask(),
                0, 30, TimeUnit.MINUTES);
        System.out.println("✅ Scheduler started: Document sync runs every 30 minutes.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("🛑 Scheduler stopped.");
        }
    }
}
