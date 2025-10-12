package com.group01.aurora_demo.admin.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Domain model representing a row in table Shops.
 */
public class Shop {
    private long shopId;
    private String name;
    private String description;
    private BigDecimal ratingAvg;
    private String status;
    private long ownerUserId;
    private Timestamp createdAt;
    private long pickupAddressId;
    private String invoiceEmail;
    private String avatarUrl;
    
    // Additional fields for display purposes
    private String ownerName;
    private String ownerEmail;
    private int productCount;

    public Shop() {}

    public Shop(ResultSet rs) throws SQLException {
        this.shopId = rs.getLong("ShopID");
        this.name = rs.getString("Name");
        this.description = rs.getString("Description");
        this.ratingAvg = rs.getBigDecimal("RatingAvg");
        this.status = rs.getString("Status");
        this.ownerUserId = rs.getLong("OwnerUserID");
        this.createdAt = rs.getTimestamp("CreatedAt");
        this.pickupAddressId = rs.getLong("PickupAddressID");
        this.invoiceEmail = rs.getString("InvoiceEmail");
        this.avatarUrl = rs.getString("AvatarUrl");
        
        // Optional fields from JOIN queries
        try {
            this.ownerName = rs.getString("OwnerName");
        } catch (SQLException e) {
            this.ownerName = null;
        }
        try {
            this.ownerEmail = rs.getString("OwnerEmail");
        } catch (SQLException e) {
            this.ownerEmail = null;
        }
        try {
            this.productCount = rs.getInt("ProductCount");
        } catch (SQLException e) {
            this.productCount = 0;
        }
    }

    // Getters and Setters
    public long getShopId() { return shopId; }
    public void setShopId(long shopId) { this.shopId = shopId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(long ownerUserId) { this.ownerUserId = ownerUserId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public long getPickupAddressId() { return pickupAddressId; }
    public void setPickupAddressId(long pickupAddressId) { this.pickupAddressId = pickupAddressId; }

    public String getInvoiceEmail() { return invoiceEmail; }
    public void setInvoiceEmail(String invoiceEmail) { this.invoiceEmail = invoiceEmail; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
}

