package com.group01.aurora_demo.cart.dao.dto;

import java.util.Date;

public class OrderShopDTO {
    private long orderShopId;
    private long ShopId;
    private String shopName;
    private double shopDiscount;
    private double shopShippingFee;
    private double shopFinalAmount;
    private String shopStatus;
    private double systemDiscount;
    private double systemShippingDiscount;
    private Date updatedAt;

    // ----------- Sản phẩm trong shop ----------
    private long productId;
    private String productName;
    private String imageUrl;

    // ----------- OrderItem ------------
    private int quantity;
    private double originalPrice;
    private double salePrice;
    private double subtotal;

    private boolean canReturn;

    public long getOrderShopId() {
        return orderShopId;
    }

    public void setOrderShopId(long orderShopId) {
        this.orderShopId = orderShopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getShopDiscount() {
        return shopDiscount;
    }

    public void setShopDiscount(double shopDiscount) {
        this.shopDiscount = shopDiscount;
    }

    public double getShopShippingFee() {
        return shopShippingFee;
    }

    public void setShopShippingFee(double shopShippingFee) {
        this.shopShippingFee = shopShippingFee;
    }

    public double getShopFinalAmount() {
        return shopFinalAmount;
    }

    public void setShopFinalAmount(double shopFinalAmount) {
        this.shopFinalAmount = shopFinalAmount;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public double getSystemShippingDiscount() {
        return systemShippingDiscount;
    }

    public void setSystemShippingDiscount(double systemShippingDiscount) {
        this.systemShippingDiscount = systemShippingDiscount;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getVietnameseStatus() {
        switch (shopStatus) {
            case "PENDING":
                return "Chờ xác nhận";
            case "PENDING_PAYMENT":
                return "Chờ thanh toán";
            case "WAITING_SHIP":
                return "Chờ giao hàng";
            case "SHIPPING":
                return "Đang vận chuyển";
            case "CONFIRM":
                return "Đang giao hàng";
            case "COMPLETED":
                return "Hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            case "RETURNED_REQUESTED":
                return "Yêu cầu trả hàng";
            case "RETURNED":
                return "Hoàn thành trả hàng";
            case "RETURNED_REJECTED":
                return "Từ chối trả hàng";

            default:
                return "Không xác định";
        }
    }

    public boolean isCanReturn() {
        return canReturn;
    }

    public void setCanReturn(boolean canReturn) {
        this.canReturn = canReturn;
    }

    public long getShopId() {
        return ShopId;
    }

    public void setShopId(long shopId) {
        ShopId = shopId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getSystemDiscount() {
        return systemDiscount;
    }

    public void setSystemDiscount(double systemDiscount) {
        this.systemDiscount = systemDiscount;
    }
}
