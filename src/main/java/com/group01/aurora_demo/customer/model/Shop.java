package com.group01.aurora_demo.customer.model;

public class Shop {
    private long shopId;
    private String name;
    private String description;
    private double ratingAvg;
    private String status;
    private long ownerUserId;
    private long pickupAddressId;
    private String invoiceEmail;
    private String avatarUrl;
    private String rejectReason;
    private Address address;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public long getPickupAddressId() {
        return pickupAddressId;
    }

    public void setPickupAddressId(long pickupAddressId) {
        this.pickupAddressId = pickupAddressId;
    }

    public String getInvoiceEmail() {
        return invoiceEmail;
    }

    public void setInvoiceEmail(String invoiceEmail) {
        this.invoiceEmail = invoiceEmail;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Shop [shopId=" + shopId + ", name=" + name + ", description=" + description + ", ratingAvg=" + ratingAvg
                + ", status=" + status + ", ownerUserId=" + ownerUserId + ", pickupAddressId=" + pickupAddressId
                + ", invoiceEmail=" + invoiceEmail + ", avatarUrl=" + avatarUrl + ", rejectReason=" + rejectReason
                + "]";
    }

}
