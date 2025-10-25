package com.group01.aurora_demo.admin.model;

/**
 * Model representing VAT (Value Added Tax) information
 *
 * @author Aurora Team
 */
public class VAT {
    private String vatCode;
    private double vatRate;
    private String description;

    public VAT() {
    }

    public VAT(String vatCode, double vatRate, String description) {
        this.vatCode = vatCode;
        this.vatRate = vatRate;
        this.description = description;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VAT{" +
                "vatCode='" + vatCode + '\'' +
                ", vatRate=" + vatRate +
                ", description='" + description + '\'' +
                '}';
    }
}
