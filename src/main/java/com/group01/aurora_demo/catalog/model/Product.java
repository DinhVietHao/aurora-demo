package com.group01.aurora_demo.catalog.model;

import com.group01.aurora_demo.shop.model.Shop;
import java.sql.Date;
import java.util.List;

public class Product {
    // Basic fields
    private Long productId;
    private Long shopId;
    private String title;
    private String description;
    private Double originalPrice;
    private Double salePrice;
    private Long soldCount;
    private Integer quantity;
    private String status;
    private Date publishedDate;
    private Double weight;
    private String rejectReason;
    private String returnReason;
    private Date createdAt;

    // Publisher relationship (N:1)
    private Long publisherId;
    private Publisher publisher;

    // Shop relationship (N:1)
    private Shop shop;

    // Images relationship (1:N)
    private String primaryImageUrl;
    private List<ProductImage> images;
    private List<String> imageUrls;

    // Categories relationship (N:N)
    private List<Category> categories;

    // Authors relationship (N:N)
    private List<Author> authors;

    // BookDetails relationship (1:1)
    private BookDetail bookDetail;

    // Computed fields (for display)
    private Double avgRating;
    private int discountPercent;

    public Product() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Long getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Long soldCount) {
        this.soldCount = soldCount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;

    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public BookDetail getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getDiscountPercent() {
        if (originalPrice != null && salePrice != null && originalPrice > salePrice) {
            this.discountPercent = (int) (((originalPrice - salePrice) / originalPrice) * 100);
        }
        return discountPercent;
    }

}