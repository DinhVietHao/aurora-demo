package com.group01.aurora_demo.admin.model;

import java.math.BigDecimal;

/**
 * Model representing platform revenue statistics for admin dashboard
 * Includes monthly shop fees and tax revenue from product sales
 *
 * @author Aurora Team
 */
public class PlatformRevenueStats {
    private int activeShopCount;
    private BigDecimal monthlyFeePerShop;
    private BigDecimal totalMonthlyFees;
    private BigDecimal totalTaxRevenue;
    private BigDecimal grandTotalRevenue;

    public PlatformRevenueStats() {
        this.activeShopCount = 0;
        this.monthlyFeePerShop = BigDecimal.ZERO;
        this.totalMonthlyFees = BigDecimal.ZERO;
        this.totalTaxRevenue = BigDecimal.ZERO;
        this.grandTotalRevenue = BigDecimal.ZERO;
    }

    public PlatformRevenueStats(int activeShopCount, BigDecimal monthlyFeePerShop, 
                                BigDecimal totalMonthlyFees, BigDecimal totalTaxRevenue, 
                                BigDecimal grandTotalRevenue) {
        this.activeShopCount = activeShopCount;
        this.monthlyFeePerShop = monthlyFeePerShop;
        this.totalMonthlyFees = totalMonthlyFees;
        this.totalTaxRevenue = totalTaxRevenue;
        this.grandTotalRevenue = grandTotalRevenue;
    }

    public int getActiveShopCount() {
        return activeShopCount;
    }

    public void setActiveShopCount(int activeShopCount) {
        this.activeShopCount = activeShopCount;
    }

    public BigDecimal getMonthlyFeePerShop() {
        return monthlyFeePerShop;
    }

    public void setMonthlyFeePerShop(BigDecimal monthlyFeePerShop) {
        this.monthlyFeePerShop = monthlyFeePerShop;
    }

    public BigDecimal getTotalMonthlyFees() {
        return totalMonthlyFees;
    }

    public void setTotalMonthlyFees(BigDecimal totalMonthlyFees) {
        this.totalMonthlyFees = totalMonthlyFees;
    }

    public BigDecimal getTotalTaxRevenue() {
        return totalTaxRevenue;
    }

    public void setTotalTaxRevenue(BigDecimal totalTaxRevenue) {
        this.totalTaxRevenue = totalTaxRevenue;
    }

    public BigDecimal getGrandTotalRevenue() {
        return grandTotalRevenue;
    }

    public void setGrandTotalRevenue(BigDecimal grandTotalRevenue) {
        this.grandTotalRevenue = grandTotalRevenue;
    }

    @Override
    public String toString() {
        return "PlatformRevenueStats{" +
                "activeShopCount=" + activeShopCount +
                ", monthlyFeePerShop=" + monthlyFeePerShop +
                ", totalMonthlyFees=" + totalMonthlyFees +
                ", totalTaxRevenue=" + totalTaxRevenue +
                ", grandTotalRevenue=" + grandTotalRevenue +
                '}';
    }
}

