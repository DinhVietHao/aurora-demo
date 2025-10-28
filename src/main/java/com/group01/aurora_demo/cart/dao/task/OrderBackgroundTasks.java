package com.group01.aurora_demo.cart.dao.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;

public class OrderBackgroundTasks {
    private final OrderShopDAO orderShopDAO = new OrderShopDAO();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int cancelled = orderShopDAO.cancelExpiredOrders();
                if (cancelled > 0) {
                    System.out.println("🕒 Đã tự động hủy " + cancelled + " đơn hàng quá hạn (3 ngày).");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                int returned = orderShopDAO.autoApproveReturnRequests();
                if (returned > 0) {
                    System.out.println("♻️ Đã tự động chuyển " + returned
                            + " đơn hàng RETURNED_REQUESTED sang RETURNED (3 ngày).");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}
