package com.group01.aurora_demo.cart.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.cart.dao.CartItemDAO;
import com.group01.aurora_demo.cart.dao.OrderDAO;
import com.group01.aurora_demo.cart.dao.OrderItemDAO;
import com.group01.aurora_demo.cart.dao.OrderShopDAO;
import com.group01.aurora_demo.cart.dao.PaymentDAO;
import com.group01.aurora_demo.cart.dao.dto.CheckoutSummaryDTO;
import com.group01.aurora_demo.cart.model.CartItem;
import com.group01.aurora_demo.cart.model.Order;
import com.group01.aurora_demo.cart.model.OrderItem;
import com.group01.aurora_demo.cart.model.OrderShop;
import com.group01.aurora_demo.cart.model.Payment;
import com.group01.aurora_demo.cart.utils.ServiceResponse;
import com.group01.aurora_demo.cart.utils.VoucherValidator;
import com.group01.aurora_demo.catalog.dao.ProductDAO;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.UserVoucherDAO;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;

public class OrderService {
    private OrderDAO orderDAO;
    private OrderShopDAO orderShopDAO;
    private OrderItemDAO orderItemDAO;
    private PaymentDAO paymentDAO;
    private VoucherDAO voucherDAO;
    private CheckoutService checkoutService;
    private CartItemDAO cartItemDAO;
    private VoucherValidator voucherValidator;
    private UserVoucherDAO userVoucherDAO;
    private ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderShopDAO = new OrderShopDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.paymentDAO = new PaymentDAO();
        this.checkoutService = new CheckoutService();
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.voucherValidator = new VoucherValidator();
        this.userVoucherDAO = new UserVoucherDAO();
        this.productDAO = new ProductDAO();
    }

    public ServiceResponse createOrder(User user, Address address, Voucher voucherDiscount,
            Voucher voucherShip, Map<Long, String> shopVouchers) {
        Connection conn = null;
        try {
            conn = DataSourceProvider.get().getConnection();
            conn.setAutoCommit(false);

            Order order = new Order();
            CheckoutSummaryDTO summary = this.checkoutService.calculateCheckoutSummary(
                    user.getId(), address.getAddressId(), voucherDiscount != null ? voucherDiscount.getCode() : null,
                    voucherShip != null ? voucherShip.getCode() : null,
                    shopVouchers);

            order.setUserId(user.getId());
            order.setAddressId(address.getAddressId());
            order.setVoucherDiscountId(voucherDiscount != null ? voucherDiscount.getVoucherID() : null);
            order.setVoucherShipId(voucherShip != null ? voucherShip.getVoucherID() : null);
            order.setTotalAmount(summary.getTotalProduct());
            order.setDiscountAmount(summary.getTotalDiscount());
            order.setTotalShippingFee(summary.getTotalShippingFee());
            order.setShippingDiscount(summary.getShippingDiscount());
            order.setFinalAmount(summary.getFinalAmount());
            order.setOrderStatus("PENDING_PAYMENT");

            long orderId = orderDAO.createOrder(conn, order);
            if (orderId == -1) {
                conn.rollback();
                return new ServiceResponse("error", "Lỗi tạo đơn hàng",
                        "Không thể tạo đơn hàng. Vui lòng thử lại sau.", 0, 0.0);
            }
            List<CartItem> cartItems = this.cartItemDAO.getCheckedCartItems(user.getId());
            Map<Long, List<CartItem>> groupByShop = cartItems.stream()
                    .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

            Map<Long, Double> shopShippingFees = this.checkoutService.calculateShippingFeePerShop(cartItems, address);
            Map<Long, Voucher> shopVoucherCache = new HashMap<>();

            for (Map.Entry<Long, List<CartItem>> entry : groupByShop.entrySet()) {
                long shopId = entry.getKey();
                List<CartItem> items = entry.getValue();
                double shopSubtotal = items.stream()
                        .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity()).sum();

                double shopDiscount = 0;
                Long shopVoucherId = null;
                if (shopVouchers != null && shopVouchers.containsKey(shopId)) {
                    String shopVoucherCode = shopVouchers.get(shopId);
                    if (shopVoucherCode != null && !shopVoucherCode.isEmpty()) {
                        Voucher shopVoucher = this.voucherDAO.getVoucherByCode(shopId, shopVoucherCode, true);
                        if (shopVoucher == null) {
                            conn.rollback();
                            return new ServiceResponse("warning", "Mã giảm giá không hợp lệ",
                                    "Mã shop #" + shopVoucherCode + " đã hết hạn hoặc không tồn tại.", 0, 0.0);
                        }
                        if (shopVoucher != null) {
                            shopVoucherCache.put(shopId, shopVoucher);
                        }

                        String validation = voucherValidator.validate(shopVoucher, shopSubtotal, shopId);
                        if (validation != null) {
                            conn.rollback();
                            return new ServiceResponse("warning", "Không thể áp dụng mã giảm giá", validation, 0, 0.0);
                        }

                        if (shopVoucher.getPerUserLimit() > 0) {
                            int usedCount = this.userVoucherDAO.getUserVoucherUsageCount(user.getId(),
                                    shopVoucher.getVoucherID());
                            if (usedCount >= shopVoucher.getPerUserLimit()) {
                                conn.rollback();
                                return new ServiceResponse("warning", "Vượt giới hạn sử dụng mã",
                                        "Bạn đã sử dụng mã giảm giá này quá số lần cho phép.", 0, 0.0);
                            }
                        }
                        shopDiscount = voucherValidator.calculateDiscount(shopVoucher, shopSubtotal);
                        shopVoucherId = shopVoucher.getVoucherID();
                    }
                }

                double shopShippingFee = shopShippingFees.getOrDefault(shopId, 0.0);
                OrderShop orderShop = new OrderShop();
                orderShop.setOrderId(orderId);
                orderShop.setShopId(shopId);
                orderShop.setVoucherId(shopVoucherId);
                orderShop.setSubtotal(shopSubtotal);
                orderShop.setDiscount(shopDiscount);
                orderShop.setShippingFee(shopShippingFee);
                orderShop.setFinalAmount(shopSubtotal - shopDiscount + shopShippingFee);
                orderShop.setStatus("PENDING_PAYMENT");
                long orderShopId = this.orderShopDAO.createOrderShop(conn, orderShop);

                if (orderShopId == -1) {
                    conn.rollback();
                    return new ServiceResponse("error", "Lỗi hệ thống",
                            "Không thể tạo đơn hàng cho shop #" + shopId + ".", 0, 0.0);
                }

                for (CartItem item : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderShopId(orderShopId);
                    orderItem.setProductId(item.getProduct().getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setOriginalPrice(item.getProduct().getOriginalPrice());
                    orderItem.setSalePrice(item.getProduct().getSalePrice());
                    orderItem.setSubtotal(item.getQuantity() * item.getProduct().getSalePrice());
                    orderItem.setVatRate(0);

                    if (orderItemDAO.createOrderItem(conn, orderItem) == -1) {
                        conn.rollback();
                        return new ServiceResponse("error", "Lỗi hệ thống",
                                "Đã xảy ra sự cố. Vui lòng thử lại sau.", 0, 0.0);
                    }

                    if (!productDAO.updateStock(conn, item.getProduct().getProductId(), item.getQuantity())) {
                        conn.rollback();
                        return new ServiceResponse("warning", "Sản phẩm hết hàng",
                                "Sản phẩm '" + item.getProduct().getTitle() + "' không đủ số lượng.", 0, 0.0);
                    }
                }
            }

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(summary.getFinalAmount());
            payment.setTransactionRef("SYS-" + System.currentTimeMillis());
            payment.setStatus("FAILED");
            if (paymentDAO.createPayment(conn, payment) == -1) {
                conn.rollback();
                return new ServiceResponse("error", "Lỗi thanh toán",
                        "Không thể khởi tạo giao dịch thanh toán. Vui lòng thử lại.", 0, 0.0);
            }
            if (!cartItemDAO.deleteCheckout(conn, user.getId())) {
                conn.rollback();
                return new ServiceResponse("error", "Lỗi hệ thống",
                        "Đã xảy ra sự cố. Vui lòng thử lại sau.", 0, 0.0);
            }

            if (voucherDiscount != null) {
                if (!voucherDAO.incrementUsage(conn, voucherDiscount.getVoucherID()) ||
                        !userVoucherDAO.insertUserVoucher(conn, user.getId(), voucherDiscount.getVoucherID())) {
                    conn.rollback();
                    return new ServiceResponse("warning", "Mã giảm giá hết lượt",
                            "Mã giảm giá đơn hàng đã hết lượt sử dụng.", 0, 0.0);
                }
            }
            if (voucherShip != null) {
                if (!voucherDAO.incrementUsage(conn, voucherShip.getVoucherID()) ||
                        !userVoucherDAO.insertUserVoucher(conn, user.getId(), voucherShip.getVoucherID())) {
                    conn.rollback();
                    return new ServiceResponse("warning", "Mã vận chuyển hết lượt",
                            "Mã giảm giá phí vận chuyển đã hết lượt sử dụng.", 0, 0.0);
                }

            }
            if (shopVouchers != null && !shopVoucherCache.isEmpty()) {
                for (Voucher shopVoucher : shopVoucherCache.values()) {
                    if (!voucherDAO.incrementUsage(conn, shopVoucher.getVoucherID()) ||
                            !userVoucherDAO.insertUserVoucher(conn, user.getId(), shopVoucher.getVoucherID())) {
                        conn.rollback();
                        return new ServiceResponse("warning", "Mã giảm giá shop hết lượt",
                                "Một trong các mã shop đã vượt giới hạn sử dụng.", 0, 0.0);
                    }
                }
            }
            conn.commit();
            return new ServiceResponse(
                    "success",
                    "Đặt hàng thành công",
                    "Đơn hàng của bạn đã được tạo thành công!",
                    orderId,
                    summary.getFinalAmount());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cause: " + e.getCause());
            System.err.println("Error message: " + e.getMessage());
            System.out.println("insertUserVoucher ERROR: " + e.getMessage());
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception rbEx) {
                rbEx.printStackTrace();
            }
            return new ServiceResponse("error", "Lỗi hệ thống", "Đã xảy ra sự cố. Vui lòng thử lại sau.", 0, 0.0);
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
                if (conn != null)
                    conn.close();
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

}
