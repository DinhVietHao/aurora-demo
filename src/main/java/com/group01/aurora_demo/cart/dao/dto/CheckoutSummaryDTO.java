package com.group01.aurora_demo.cart.dao.dto;

public class CheckoutSummaryDTO {
    private double totalProduct;
    private double ShopDiscount;
    private double systemDiscount;
    private double totalShippingFee;
    private double systemShippingDiscount;
    private double finalAmount;

    public CheckoutSummaryDTO(double totalProduct, double shopDiscount, double systemDiscount, double totalShippingFee,
            double systemShippingDiscount, double finalAmount) {
        this.totalProduct = totalProduct;
        ShopDiscount = shopDiscount;
        this.systemDiscount = systemDiscount;
        this.totalShippingFee = totalShippingFee;
        this.systemShippingDiscount = systemShippingDiscount;
        this.finalAmount = finalAmount;
    }

    public double getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(double totalProduct) {
        this.totalProduct = totalProduct;
    }

    public double getShopDiscount() {
        return ShopDiscount;
    }

    public void setShopDiscount(double shopDiscount) {
        ShopDiscount = shopDiscount;
    }

    public double getSystemDiscount() {
        return systemDiscount;
    }

    public void setSystemDiscount(double systemDiscount) {
        this.systemDiscount = systemDiscount;
    }

    public double getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(double totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public double getSystemShippingDiscount() {
        return systemShippingDiscount;
    }

    public void setSystemShippingDiscount(double systemShippingDiscount) {
        this.systemShippingDiscount = systemShippingDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

}
