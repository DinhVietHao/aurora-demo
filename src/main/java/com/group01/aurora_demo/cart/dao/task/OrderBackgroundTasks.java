// package com.group01.aurora_demo.cart.dao.task;

// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;
// import com.group01.aurora_demo.cart.dao.OrderDAO;

// public class OrderBackgroundTasks {
//     private final OrderDAO orderDAO = new OrderDAO();
//     private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

//     public void start() {
//         scheduler.scheduleAtFixedRate(() -> {
//             try {
//                 int cancelled = orderDAO.cancelExpiredOrders();
//                 if (cancelled > 0) {
//                     System.out.println("🕒 Đã tự động hủy " + cancelled + " đơn hàng quá hạn (3 ngày).");
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }, 0, 1, TimeUnit.HOURS);

//         scheduler.scheduleAtFixedRate(() -> {
//             try {
//                 int returned = orderDAO.autoApproveReturnRequests();
//                 if (returned > 0) {
//                     System.out.println("♻️ Đã tự động chuyển " + returned
//                             + " đơn hàng RETURNED_REQUESTED sang RETURNED (3 ngày).");
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }, 0, 1, TimeUnit.HOURS);
//     }

//     public void stop() {
//         scheduler.shutdown();
//     }
// }
