package com.group01.aurora_demo.cart.dao.dto;

public class CheckoutSummaryDTO {
    private double totalProduct;
    private double totalDiscount;
    private double totalShippingFee;
    private double shippingDiscount;
    private double finalAmount;

    public CheckoutSummaryDTO(double totalProduct, double totalDiscount, double totalShippingFee,
            double shippingDiscount, double finalAmount) {
        this.totalProduct = totalProduct;
        this.totalDiscount = totalDiscount;
        this.totalShippingFee = totalShippingFee;
        this.shippingDiscount = shippingDiscount;
        this.finalAmount = finalAmount;
    }

    public double getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(double totalProduct) {
        this.totalProduct = totalProduct;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(double totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public double getShippingDiscount() {
        return shippingDiscount;
    }

    public void setShippingDiscount(double shippingDiscount) {
        this.shippingDiscount = shippingDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

}
