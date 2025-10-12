package com.group01.aurora_demo.admin.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Domain model representing a row in table FlashSales.
 */
public class FlashSale {
    private long flashSaleId;
    private String name;
    private Timestamp startAt;
    private Timestamp endAt;
    private String status;
    private Timestamp createdAt;

    public FlashSale() {}

    public FlashSale(ResultSet rs) throws SQLException {
        this.flashSaleId = rs.getLong("FlashSaleID");
        this.name = rs.getString("Name");
        this.startAt = rs.getTimestamp("StartAt");
        this.endAt = rs.getTimestamp("EndAt");
        this.status = rs.getString("Status");
        this.createdAt = rs.getTimestamp("CreatedAt");
    }

    public long getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(long flashSaleId) { this.flashSaleId = flashSaleId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Timestamp getStartAt() { return startAt; }
    public void setStartAt(Timestamp startAt) { this.startAt = startAt; }

    public Timestamp getEndAt() { return endAt; }
    public void setEndAt(Timestamp endAt) { this.endAt = endAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
