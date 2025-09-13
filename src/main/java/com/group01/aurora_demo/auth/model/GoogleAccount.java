package com.group01.aurora_demo.auth.model;

public class GoogleAccount {

    private String id;
    private String name;
    private String email;
    private String picture;
    private String givenName;
    private String familyName;
    private boolean emailVerified;

    public GoogleAccount() {
    }

    public GoogleAccount(String id, String email, boolean emailVerified,
            String name, String givenName, String familyName, String picture) {
        this.id = id;
        this.email = email;
        this.emailVerified = emailVerified;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "GoogleAccount{"
                + "id='" + id + '\''
                + ", email='" + email + '\''
                + ", emailVerified=" + emailVerified
                + ", name='" + name + '\''
                + ", givenName='" + givenName + '\''
                + ", familyName='" + familyName + '\''
                + ", picture='" + picture + '\''
                + '}';
    }
}