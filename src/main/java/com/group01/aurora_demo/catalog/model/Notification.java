package com.group01.aurora_demo.catalog.model;


import java.sql.Timestamp;

public class Notification {
    private long notificationID;
    private String recipientType;
    private long recipientID;
    private String type;
    private String title;
    private String message;
    private String referenceType;
    private Long referenceID;
    private Timestamp createdAt;
    private String link;
    private String timeAgo;

    // --- Getters & Setters ---
    public long getNotificationID() {
        return notificationID;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setNotificationID(long notificationID) {
        this.notificationID = notificationID;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public long getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(long recipientID) {
        this.recipientID = recipientID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(Long referenceID) {
        this.referenceID = referenceID;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
