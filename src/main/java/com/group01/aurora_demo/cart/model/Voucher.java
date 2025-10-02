package com.group01.aurora_demo.cart.model;

public class Voucher {
    private long voucherId;
    private String code;
    private String discountType;
    private double value;
    private double maxAmount;
    private double minOrderAmount;
    private int usageLimit;
    private int perUserLimit;
    private String status;
    private int usageCount;
    private boolean isShopVoucher;
    private Long shopId;

    public long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(long voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(int perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public boolean isShopVoucher() {
        return isShopVoucher;
    }

    public void setShopVoucher(boolean isShopVoucher) {
        this.isShopVoucher = isShopVoucher;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "Voucher [voucherId=" + voucherId + ", code=" + code + ", discountType=" + discountType + ", value="
                + value + ", maxAmount=" + maxAmount + ", minOrderAmount=" + minOrderAmount + ", usageLimit="
                + usageLimit + ", perUserLimit=" + perUserLimit + ", status=" + status + ", usageCount=" + usageCount
                + ", isShopVoucher=" + isShopVoucher + ", shopId=" + shopId + "]";
    }

}
