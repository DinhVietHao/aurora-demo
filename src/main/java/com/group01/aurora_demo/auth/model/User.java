package com.group01.aurora_demo.auth.model;

import java.time.LocalDateTime;

public class User {

    private long userID;
    private String email;
    private String fullName;
    private String authProvider;
    private String password;
    private LocalDateTime createdAt;
    private String status = "active"; // Default value
    private String phone;
    private int points;
    private String nationalID;
    private String avatarUrl;
    private String roles;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
    
    // For backward compatibility
    public long getId() {
        return userID;
    }

    public void setId(long id) {
        this.userID = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // For backward compatibility
    public String getPasswordHash() {
        return password;
    }

    public void setPasswordHash(String passwordHash) {
        this.password = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }
    
    public String getStatus() {
        if (status == null) return "active";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }
    
    // For backward compatibility
    public String getNationalId() {
        return nationalID;
    }

    public void setNationalId(String nationalId) {
        this.nationalID = nationalId;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}