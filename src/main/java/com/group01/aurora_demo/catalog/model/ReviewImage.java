package com.group01.aurora_demo.catalog.model;

public class ReviewImage {
    private Long reviewImageId;
    private Long reviewId;
    private String url;
    private String caption;
    private boolean isPrimary;

    public ReviewImage() {
    }

    public Long getReviewImageId() {
        return reviewImageId;
    }

    public void setReviewImageId(Long reviewImageId) {
        this.reviewImageId = reviewImageId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
