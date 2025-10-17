package com.group01.aurora_demo.cart.dao.task;

import com.group01.aurora_demo.cart.dao.OrderDAO;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderAutoCancelTask {

    private final OrderDAO orderDAO = new OrderDAO();

    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                int cancelledCount = orderDAO.cancelExpiredOrders();
                if (cancelledCount > 0) {
                    System.out.println("🕒 Đã tự động hủy " + cancelledCount + " đơn hàng quá hạn (3 ngày).");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}