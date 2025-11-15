package com.group01.aurora_demo.admin.model;

/**
 * Model representing a Category in the admin module
 *
 * @author Aurora Team
 */
public class Category {
    private Long categoryId;
    private String name;
    private String vatCode;

    public Category() {
    }

    public Category(Long categoryId, String name, String vatCode) {
        this.categoryId = categoryId;
        this.name = name;
        this.vatCode = vatCode;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
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
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", vatCode='" + vatCode + '\'' +
                '}';
    }
}
