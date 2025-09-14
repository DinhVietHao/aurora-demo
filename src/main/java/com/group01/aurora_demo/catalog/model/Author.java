package com.group01.aurora_demo.catalog.model;

public class Author {
    private Long authorId;
    private String authorName;

    // Constructors
    public Author() {}
    public Author(Long authorId, String authorName) {
        this.authorId = authorId;
        this.authorName = authorName;
    }

    // Getters & Setters
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "Author{id=" + authorId + ", name='" + authorName + "'}";
    }
}

