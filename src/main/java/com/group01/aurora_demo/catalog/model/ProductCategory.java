package com.group01.aurora_demo.catalog.model;

public class ProductCategory {
    private long categoryId;
    private String name;
    private Long parentId;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "ProductCategory [categoryId=" + categoryId + ", name=" + name + ", parentId=" + parentId + "]";
    }

    

}
