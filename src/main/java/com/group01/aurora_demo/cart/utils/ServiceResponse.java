package com.group01.aurora_demo.cart.utils;

public class ServiceResponse {
    private String type;
    private String title;
    private String message;
    private long orderId;
    private double finalAmount;

    public ServiceResponse(String type, String title, String message, long orderId, double finalAmount) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.orderId = orderId;
        this.finalAmount = finalAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

}
