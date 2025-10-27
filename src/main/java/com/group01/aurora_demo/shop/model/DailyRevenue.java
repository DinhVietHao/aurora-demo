package com.group01.aurora_demo.shop.model;

import java.sql.Date;

public class DailyRevenue {
    private Date date;
    private double revenue;

    public DailyRevenue() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
