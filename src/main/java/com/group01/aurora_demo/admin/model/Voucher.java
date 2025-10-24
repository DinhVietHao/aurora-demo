package com.group01.aurora_demo.admin.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Voucher {
    private long voucherId;
    private String code;
    private String discountType;
    private BigDecimal value;
    private BigDecimal maxAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer usageLimit;
    private Integer perUserLimit;
    private String status;
    private int usageCount;
    private boolean isShopVoucher;
    private Long shopId;
    private String description;
    private java.sql.Timestamp createdAt;

    public Voucher() {
    }

    public Voucher(long voucherId, String code, String discountType, BigDecimal value, BigDecimal maxAmount,
                  BigDecimal minOrderAmount, LocalDateTime startAt, LocalDateTime endAt, Integer usageLimit,
                  Integer perUserLimit, String status, int usageCount, boolean isShopVoucher, Long shopId,
                  String description, java.sql.Timestamp createdAt) {
        this.voucherId = voucherId;
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.maxAmount = maxAmount;
        this.minOrderAmount = minOrderAmount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usageLimit = usageLimit;
        this.perUserLimit = perUserLimit;
        this.status = status;
        this.usageCount = usageCount;
        this.isShopVoucher = isShopVoucher;
        this.shopId = shopId;
        this.description = description;
        this.createdAt = createdAt;
    }

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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endAt);
    }
    
    public boolean isPending() {
        return LocalDateTime.now().isBefore(startAt);
    }
    
    // Convert LocalDateTime to Date for JSP formatting
    public Date getStartAtDate() {
        return Date.from(startAt.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public Date getEndAtDate() {
        return Date.from(endAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String getFormattedValue() {
        if ("percentage".equalsIgnoreCase(discountType)) {
            return value + "%";
        } else {
            return String.format("%,.0f VNĐ", value);
        }
    }
    
    public String getFormattedMaxAmount() {
        if (maxAmount == null) {
            return "Không giới hạn";
        }
        return String.format("%,.0f VNĐ", maxAmount);
    }
    
    public String getFormattedMinOrderAmount() {
        return String.format("%,.0f VNĐ", minOrderAmount);
    }
    
    public String getDisplayStatus() {
        if (isPending()) {
            return "Chờ";
        } else if (isExpired()) {
            return "Hết hạn";
        } else if (isActive()) {
            return "Hoạt động";
        } else {
            return "Ngừng hoạt động";
        }
    }
}
