package com.group01.aurora_demo.cart.model;

import java.util.Date;
import java.util.List;

public class Order {
    private long orderId;
    private long userId;
    private String address;
    private Long voucherDiscountId;
    private Long voucherShipId;
    private double totalAmount;
    private double discountAmount;
    private double totalShippingFee;
    private double shippingDiscount;
    private double finalAmount;
    private String orderStatus;
    private Date createdAt;
    private Date deliveredAt;
    private String cancelReason;
    private Date cancelledAt;
    private String customerName;

    private List<OrderShop> orderShop;

    public List<OrderShop> getOrderShop() {
        return orderShop;
    }

    public void setOrderShop(List<OrderShop> orderShop) {
        this.orderShop = orderShop;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getVoucherDiscountId() {
        return voucherDiscountId;
    }

    public void setVoucherDiscountId(Long voucherDiscountId) {
        this.voucherDiscountId = voucherDiscountId;
    }

    public Long getVoucherShipId() {
        return voucherShipId;
    }

    public void setVoucherShipId(Long voucherShipId) {
        this.voucherShipId = voucherShipId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getShippingDiscount() {
        return shippingDiscount;
    }

    public void setShippingDiscount(double shippingDiscount) {
        this.shippingDiscount = shippingDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public double getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(double totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}