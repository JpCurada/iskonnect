package com.iskonnect.models;

import java.time.LocalDateTime;

public abstract class User {
    private String strUserId;
    private String strFirstName;
    private String strLastName;
    private String strEmail;
    private String strPasswordHash;
    private LocalDateTime datetimeCreatedAt;
    private String strUserType;  // STUDENT or ADMIN

    public User(String strUserId, String strFirstName, String strLastName, String strEmail, String strUserType) {
        this.strUserId = strUserId;
        this.strFirstName = strFirstName;
        this.strLastName = strLastName;
        this.strEmail = strEmail;
        this.strUserType = strUserType;
        this.datetimeCreatedAt = LocalDateTime.now();
    }

    // Getters
    public String getUserId() { return strUserId; }
    public String getFirstName() { return strFirstName; }
    public String getLastName() { return strLastName; }
    public String getEmail() { return strEmail; }
    public String getPasswordHash() { return strPasswordHash; }
    public String getUserType() { return strUserType; }
    public LocalDateTime getdatetimeCreatedAt() { return datetimeCreatedAt; }

    // Setters
    public void setFirstName(String strFirstName) { this.strFirstName = strFirstName; }
    public void setLastName(String strLastName) { this.strLastName = strLastName; }
    public void setEmail(String strEmail) { this.strEmail = strEmail; }
    public void setPasswordHash(String strPasswordHash) { this.strPasswordHash = strPasswordHash; }

    public String getFullName() {
        return strFirstName + " " + strLastName;
    }
}