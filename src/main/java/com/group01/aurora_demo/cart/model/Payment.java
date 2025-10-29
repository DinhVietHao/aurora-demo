package com.group01.aurora_demo.cart.model;

import java.util.Date;

public class Payment {
    private long paymentId;
    private long orderShopId;
    private String groupOrderCode;
    private double amount;
    private double refundedAmount;
    private String transactionRef;
    private String status;
    private Date createdAt;

    public long getPaymentId() {
        return paymentId;
    }

    public String getGroupOrderCode() {
        return groupOrderCode;
    }

    public void setGroupOrderCode(String groupOrderCode) {
        this.groupOrderCode = groupOrderCode;
    }

    public double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getOrderShopId() {
        return orderShopId;
    }

    public void setOrderShopId(long orderShopId) {
        this.orderShopId = orderShopId;
    }

}
