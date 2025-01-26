package com.iskonnect.models;

public class Admin extends User {
    private int reportCount;
    private int upvoteCount;

    public Admin(String strAdminId, String strFirstName, String strLastName, String strEmail) {
        super(strAdminId, strFirstName, strLastName, strEmail, "ADMIN");
    }

    // Additional properties for admin management views
    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }
    
    public int getUpvoteCount() { return upvoteCount; }
    public void setUpvoteCount(int upvoteCount) { this.upvoteCount = upvoteCount; }
}