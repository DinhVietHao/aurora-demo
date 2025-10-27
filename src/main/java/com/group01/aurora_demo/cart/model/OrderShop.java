package com.group01.aurora_demo.cart.model;

import java.util.Date;
import java.util.List;

import com.group01.aurora_demo.auth.model.User;

public class OrderShop {
    private long orderShopId;
    private String groupOrderCode;
    private long userId;
    private long shopId;
    private String address;
    private Long voucherShopId;
    private Long voucherDiscountId;
    private Long voucherShipId;
    private double subtotal;
    private double shopDiscount;
    private double systemDiscount;
    private double shippingFee;
    private double systemShippingDiscount;
    private double finalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String cancelReason;
    private String returnReason;

    private List<OrderItem> items;
    private User user;

    public long getOrderShopId() {
        return orderShopId;
    }

    public void setOrderShopId(long orderShopId) {
        this.orderShopId = orderShopId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getVoucherShopId() {
        return voucherShopId;
    }

    public void setVoucherShopId(Long voucherShopId) {
        this.voucherShopId = voucherShopId;
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

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGroupOrderCode() {
        return groupOrderCode;
    }

    public void setGroupOrderCode(String groupOrderCode) {
        this.groupOrderCode = groupOrderCode;
    }

}