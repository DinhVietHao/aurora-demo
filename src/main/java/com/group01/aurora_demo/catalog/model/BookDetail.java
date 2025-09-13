package com.group01.aurora_demo.catalog.model;

public class BookDetail {

    private long productId;
    private String author;
    private String translator;
    private String version;
    private String coverType;
    private int pages;
    private String language;
    private String size;
    private String ISBN;

    public BookDetail() {
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    @Override
    public String toString() {
        return "BookDetail{" + "productId=" + productId + ", author=" + author + ", translator=" + translator
                + ", version=" + version + ", coverType=" + coverType + ", pages=" + pages + ", language=" + language
                + ", size=" + size + ", ISBN=" + ISBN + '}';
    }

}