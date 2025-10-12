package com.group01.aurora_demo.catalog.model;

import java.time.LocalDate;
import java.util.List;

public class Product {

    private Long productId;
    private Long shopId;
    private String title;
    private String description;
    private Double originalPrice;
    private Double salePrice;
    private Long soldCount;
    private Integer stock;
    private Boolean isBundle;
    private Long categoryId;
    private LocalDate publishedDate;
    private String primaryImageUrl;
    private List<ProductImages> images;
    private List<Author> authors;
    private Publisher publisher; 
    private String publisherString; 
    private BookDetail bookDetail;

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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getIsBundle() {
        return isBundle;
    }

    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getPublisherString() {
        return publisherString;
    }

    public void setPublisherString(String publisherString) {
        this.publisherString = publisherString;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public List<ProductImages> getImages() {
        return images;
    }

    public void setImages(List<ProductImages> images) {
        this.images = images;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public BookDetail getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", shopId=" + shopId + ", title=" + title + ", description="
                + description + ", originalPrice=" + originalPrice + ", salePrice=" + salePrice + ", soldCount="
                + soldCount + ", stock=" + stock + ", isBundle=" + isBundle + ", categoryId=" + categoryId
                + ", publishedDate=" + publishedDate + ", primaryImageUrl=" + primaryImageUrl + ", images=" + images
                + ", authors=" + authors + ", publisher=" + publisher + ", bookDetail=" + bookDetail + "]";
    }

    
}
