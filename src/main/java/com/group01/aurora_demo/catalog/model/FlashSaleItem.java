package com.group01.aurora_demo.catalog.model;

import java.sql.Timestamp;

public class FlashSaleItem {
    private long flashSaleItemID;
    private long productID;
    private long flashSaleId;
    private String title;
    private double originalPrice;
    private double flashPrice;
    private int fsStock;
    private Integer perUserLimit;
    private String approvalStatus;
    private String imageUrl;
    private Product product;
    private int soldCount;
    private Timestamp startAt;
    private Timestamp endAt;

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public Timestamp getStartAt() {
        return startAt;
    }

    public void setStartAt(Timestamp startAt) {
        this.startAt = startAt;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }

    public long getFlashSaleItemID() {
        return flashSaleItemID;
    }

    public void setFlashSaleItemID(long flashSaleItemID) {
        this.flashSaleItemID = flashSaleItemID;
    }

    public long getProductID() {
        return productID;
    }

    public void setProductID(long productID) {
        this.productID = productID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getFlashPrice() {
        return flashPrice;
    }

    public void setFlashPrice(double flashPrice) {
        this.flashPrice = flashPrice;
    }

    public int getFsStock() {
        return fsStock;
    }

    public void setFsStock(int fsStock) {
        this.fsStock = fsStock;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getFlashSaleId() {
        return flashSaleId;
    }

    public void setFlashSaleId(long flashSaleId) {
        this.flashSaleId = flashSaleId;
    }

}
