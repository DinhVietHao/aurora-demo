package com.group01.aurora_demo.cart.dao.task;

import com.group01.aurora_demo.cart.dao.OrderShopDAO;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderAutoCancelTask {
    private final OrderShopDAO orderShopDAO = new OrderShopDAO();

    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {

                // Hoàn tất đơn giao thành công sau 7 ngày mà không xác nhận
                int completedCount = orderShopDAO.autoCompleteConfirmOrders();
                if (completedCount > 0) {
                    System.out
                            .println("Đã tự động hoàn tất " + completedCount + " đơn hàng không xác nhận sau 7 ngày.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}