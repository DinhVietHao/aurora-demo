package com.group01.aurora_demo.catalog.model;

import java.sql.Timestamp;

public class FlashSale {
    private long flashSaleID;
    private String name;
    private Timestamp startAt;
    private Timestamp endAt;
    private String status;

    public long getFlashSaleID() {
        return flashSaleID;
    }

    public void setFlashSaleID(long flashSaleID) {
        this.flashSaleID = flashSaleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

}
