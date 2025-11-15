package com.group01.aurora_demo.admin.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlashSaleItemView {
    private long flashSaleItemId;
    private long productId;
    private String title;
    private String publisherName;
    private String shopName;
    private BigDecimal flashPrice;
    private int fsStock;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private String approvalStatus;

    public FlashSaleItemView() {}

    public FlashSaleItemView(ResultSet rs) throws SQLException {
        flashSaleItemId = rs.getLong("FlashSaleItemID");
        productId = rs.getLong("ProductID");
        title = rs.getString("Title");
        publisherName = rs.getString("PublisherName");
        shopName = rs.getString("ShopName");
        flashPrice = rs.getBigDecimal("FlashPrice");
        fsStock = rs.getInt("FsStock");
        originalPrice = rs.getBigDecimal("OriginalPrice");
        salePrice = rs.getBigDecimal("SalePrice");
        approvalStatus = rs.getString("ApprovalStatus");
    }

    public long getFlashSaleItemId() { return flashSaleItemId; }
    public long getProductId() { return productId; }
    public String getTitle() { return title; }
    public String getPublisherName() { return publisherName; }
    public String getShopName() { return shopName; }
    public java.math.BigDecimal getFlashPrice() { return flashPrice; }
    public int getFsStock() { return fsStock; }
    public java.math.BigDecimal getOriginalPrice() { return originalPrice; }
    public java.math.BigDecimal getSalePrice() { return salePrice; }
    public String getApprovalStatus() { return approvalStatus; }
}


