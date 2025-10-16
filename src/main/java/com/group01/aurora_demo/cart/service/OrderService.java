package com.group01.aurora_demo.cart.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
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
import com.group01.aurora_demo.cart.utils.VoucherValidator;
import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.profile.model.Address;
import com.group01.aurora_demo.shop.dao.VoucherDAO;
import com.group01.aurora_demo.shop.model.Voucher;
import com.group01.aurora_demo.admin.dao.VATDAO;

public class OrderService {
    private OrderDAO orderDAO;
    private OrderShopDAO orderShopDAO;
    private OrderItemDAO orderItemDAO;
    private PaymentDAO paymentDAO;
    private VoucherDAO voucherDAO;
    private CheckoutService checkoutService;
    private CartItemDAO cartItemDAO;
    private VoucherValidator voucherValidator;
    private VATDAO vatDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderShopDAO = new OrderShopDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.paymentDAO = new PaymentDAO();
        this.checkoutService = new CheckoutService();
        this.cartItemDAO = new CartItemDAO();
        this.voucherDAO = new VoucherDAO();
        this.voucherValidator = new VoucherValidator();
        this.vatDAO = new VATDAO();
    }

    public boolean createOrder(User user, Address address, Voucher voucherDiscount,
            Voucher voucherShip, Map<Long, String> shopVouchers) {
        try (Connection conn = DataSourceProvider.get().getConnection()) {
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
            order.setOrderStatus("PENDING");

            if (!orderDAO.createOrder(conn, order)) {
                conn.rollback();
                return false;
            }

            long orderId = order.getOrderId();
            List<CartItem> cartItems = this.cartItemDAO.getCheckedCartItems(user.getId());
            Map<Long, List<CartItem>> groupByShop = cartItems.stream()
                    .collect(Collectors.groupingBy(ci -> ci.getProduct().getShop().getShopId()));

            for (Map.Entry<Long, List<CartItem>> entry : groupByShop.entrySet()) {
                long shopId = entry.getKey();
                List<CartItem> items = entry.getValue();
                double shopSubtotal = items.stream()
                        .mapToDouble(ci -> ci.getProduct().getSalePrice() * ci.getQuantity()).sum();

                double shopDiscount = 0;
                Long voucherId = null;
                if (shopVouchers != null && shopVouchers.containsKey(shopId)) {
                    String shopVoucherCode = shopVouchers.get(shopId);
                    if (shopVoucherCode != null && !shopVoucherCode.isEmpty()) {
                        Voucher shopVoucher = this.voucherDAO.getVoucherByCode(shopVoucherCode, true);
                        if (shopVoucher != null) {
                            shopDiscount = this.voucherValidator.calculateDiscount(shopVoucher, shopSubtotal);
                            voucherId = shopVoucher.getVoucherID();
                        }
                    }
                }
                Double shopShippingFees = this.checkoutService.calculateShippingFee(cartItems, address);
                OrderShop orderShop = new OrderShop();
                orderShop.setOrderId(orderId);
                orderShop.setShopId(shopId);
                orderShop.setVoucherId(voucherId);
                orderShop.setSubtotal(shopSubtotal);
                orderShop.setDiscount(shopDiscount);
                orderShop.setShippingFee(shopShippingFees);
                orderShop.setFinalAmount(shopSubtotal - shopDiscount);
                orderShop.setStatus("PENDING");

                if (!orderShopDAO.createOrderShop(conn, orderShop)) {
                    conn.rollback();
                    return false;
                }

                long orderShopId = orderShop.getOrderShopId();

                for (CartItem item : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderShopId(orderShopId);
                    orderItem.setProductId(item.getProduct().getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(item.getProduct().getSalePrice());
                    orderItem.setSubtotal(item.getQuantity() * item.getProduct().getSalePrice());

                    // Calculate VAT rate based on product category
                    double vatRate = vatDAO.getVATRateByProductId(item.getProduct().getProductId());
                    orderItem.setVatRate(vatRate);

                    if (!orderItemDAO.createOrderItem(conn, orderItem)) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(summary.getFinalAmount());
            payment.setTransactionRef("SYS-" + System.currentTimeMillis());

            if (!paymentDAO.createPayment(conn, payment)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
