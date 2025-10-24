package com.group01.aurora_demo.cart.dao.task;

import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderAutoCancelTask {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderShopDAO orderShopDAO = new OrderShopDAO();

    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                int cancelledCount = orderDAO.cancelExpiredOrders();
                if (cancelledCount > 0) {
                    System.out.println("🕒 Đã tự động hủy " + cancelledCount + " đơn hàng quá hạn (3 ngày).");
                }
                // Hoàn tất đơn giao thành công sau 7 ngày mà không xác nhận
                int completedCount = orderShopDAO.autoCompleteConfirmOrders();
                if (completedCount > 0) {
                    System.out
                            .println("Đã tự động hoàn tất " + completedCount + " đơn hàng không xác nhận sau 7 ngày.");
                }

                // Hủy đơn quá hạn thanh toán (chờ thanh toán quá 60 phút)
                int pendingCancelled = orderShopDAO.autoCancelPendingPaymentOrders();
                if (pendingCancelled > 0) {
                    System.out.println("Đã tự động hủy " + pendingCancelled + " đơn CHỜ THANH TOÁN quá 60 phút.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}