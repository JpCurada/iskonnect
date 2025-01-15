package com.iskonnect.utils;

public class UserSession {
    private static UserSession instance;
    private String strUserId;  // Changed to String for student number
    private String strUserType;
    private String strFirstName;
    private String strLastName;
    private String strEmail;

    // Private constructor to enforce singleton pattern
    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void startSession(String strUserId, String strUserType, String strFirstName, String strLastName, String strEmail) {
        this.strUserId = strUserId;
        this.strUserType = strUserType;
        this.strFirstName = strFirstName;
        this.strLastName = strLastName;
        this.strEmail = strEmail;
    }

    public void endSession() {
        this.strUserId = null;
        this.strUserType = null;
        this.strFirstName = null;
        this.strLastName = null;
        this.strEmail = null;
    }

    // Getters
    public String getUserId() {
        return strUserId;
    }

    public String getUserType() {
        return strUserType;
    }

    public String getFirstName() {
        return strFirstName;
    }

    public String getLastName() {
        return strLastName;
    }

    public String getEmail() {
        return strEmail;
    }

    public boolean isLoggedIn() {
        return strUserId != null && strUserType != null;
    }

    public String getFullName() {
        return strFirstName + " " + strLastName;
    }
}