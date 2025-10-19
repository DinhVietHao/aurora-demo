package com.group01.aurora_demo.cart.dao.dto;

import java.util.Date;
import java.util.List;

import com.group01.aurora_demo.catalog.model.Category;

public class OrderDTO {
    private long orderId;
    private long orderShopId;
    private long productId;
    private String shopName;
    private String productName;
    private String imageUrl;
    private int quantity;
    private Double originalPrice;
    private Double salePrice;
    private double finalAmount;
    private String shopStatus;
    private boolean canReturn;
    private Date updateAt;

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    List<Category> categories;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public long getOrderShopId() {
        return orderShopId;
    }

    public void setOrderShopId(long orderShopId) {
        this.orderShopId = orderShopId;
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
            case "RETURN_REQUESTED":
                return "Yêu cầu trả hàng";
            case "RETURNED":
                return "Hoàn thành trả hàng";
            case "RETURN_REJECTED":
                return "Từ chối trả hàng";

            default:
                return "Không xác định";
        }
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public boolean isCanReturn() {
        return canReturn;
    }

    public void setCanReturn(boolean canReturn) {
        this.canReturn = canReturn;
    }

}
