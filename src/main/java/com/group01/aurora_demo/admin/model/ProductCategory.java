package com.group01.aurora_demo.admin.model;

public class ProductCategory {
    private long categoryId;
    private String name;
    private String vatCode;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    @Override
    public String toString() {
        return "ProductCategory [categoryId=" + categoryId + ", name=" + name + ", vatCode=" + vatCode + "]";
    }

    

}
