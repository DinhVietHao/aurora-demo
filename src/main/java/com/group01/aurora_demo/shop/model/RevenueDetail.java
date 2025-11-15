package com.group01.aurora_demo.shop.model;

import java.sql.Timestamp;

public class RevenueDetail {
    private Long orderShopId;
    private String orderCode;
    private Timestamp completedAt;
    private double subtotal;
    private double shopDiscount;
    private double systemDiscount;
    private double shippingFee;
    private double systemShippingDiscount;
    private double totalVAT;
    private double finalAmount;
    private double shopRevenue;
    private String customerName;
    private int itemCount;
    private double platformFee;

    public Long getOrderShopId() {
        return orderShopId;
    }

    public void setOrderShopId(Long orderShopId) {
        this.orderShopId = orderShopId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShopDiscount() {
        return shopDiscount;
    }

    public void setShopDiscount(double shopDiscount) {
        this.shopDiscount = shopDiscount;
    }

    public double getSystemDiscount() {
        return systemDiscount;
    }

    public void setSystemDiscount(double systemDiscount) {
        this.systemDiscount = systemDiscount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getSystemShippingDiscount() {
        return systemShippingDiscount;
    }

    public void setSystemShippingDiscount(double systemShippingDiscount) {
        this.systemShippingDiscount = systemShippingDiscount;
    }

    public double getTotalVAT() {
        return totalVAT;
    }

    public void setTotalVAT(double totalVAT) {
        this.totalVAT = totalVAT;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public double getShopRevenue() {
        return shopRevenue;
    }

    public void setShopRevenue(double shopRevenue) {
        this.shopRevenue = shopRevenue;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public double getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(double platformFee) {
        this.platformFee = platformFee;
    }
}