package com.group01.aurora_demo.admin.model;

public class ProductImages {

    private long imageId;
    private long productId;
    private String url;
    private boolean isPrimary;

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;

    }

    @Override
    public String toString() {
        return "ProductImages{" + "imageId=" + imageId
                + ", productId=" + productId
                + ", url=" + url + '}';

    }
}