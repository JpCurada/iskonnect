package com.iskonnect.models;

public class Badge {
    private int intBadgeId;
    private String strName;
    private String strDescription;
    private String strImageUrl; // Add this field
    private int intRequirementPoints;

    // Updated constructor to include imageUrl
    public Badge(String strName, String strDescription, String strImageUrl, int intRequirementPoints) {
        this.strName = strName;
        this.strDescription = strDescription;
        this.strImageUrl = strImageUrl; // Initialize this field
        this.intRequirementPoints = intRequirementPoints;
    }

    // Getters
    public int getBadgeId() { return intBadgeId; }
    public String getName() { return strName; }
    public String getDescription() { return strDescription; }
    public String getImageUrl() { return strImageUrl; } // Add this getter
    public int getRequirementPoints() { return intRequirementPoints; }

    // Setters
    public void setImageUrl(String strImageUrl) { this.strImageUrl = strImageUrl; }
}