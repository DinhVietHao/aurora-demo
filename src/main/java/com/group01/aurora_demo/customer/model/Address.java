package com.group01.aurora_demo.customer.model;

import java.sql.Date;

public class Address {
    private String city;
    private String ward;
    private String phone;
    private long addressId;
    private Date createdAt;
    private String description;
    private String recipientName;
    private UserAddress userAddress;

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }

    @Override
    public String toString() {
        return "Address [addressId=" + addressId + ", recipientName=" + recipientName + ", phone=" + phone + ", city="
                + city + ", ward=" + ward + ", description=" + description + ", createdAt=" + createdAt
                + ", userAddress=" + userAddress + "]";
    }

}
