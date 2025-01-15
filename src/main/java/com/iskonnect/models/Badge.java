package com.iskonnect.models;

public class Badge {
    private int intBadgeId;
    private String strName;
    private String strDescription;
    private String strImageUrl;
    private int intRequirementPoints;

    public Badge(String strName, String strDescription, int intRequirementPoints) {
        this.strName = strName;
        this.strDescription = strDescription;
        this.intRequirementPoints = intRequirementPoints;
    }

    // Getters
    public int getBadgeId() { return intBadgeId; }
    public String getName() { return strName; }
    public String getDescription() { return strDescription; }
    public String getImageUrl() { return strImageUrl; }
    public int getRequirementPoints() { return intRequirementPoints; }

    // Setters
    public void setImageUrl(String strImageUrl) { this.strImageUrl = strImageUrl; }
}