package com.iskonnect.models;

public class UserDetails {
    private String userId;
    private String firstName;
    private String lastName;
    private int points;
    private String email;
    private int reportCount;
    private int upvoteCount;

    public UserDetails(String userId, String firstName, String lastName, 
                      int points, String email, int reportCount, int upvoteCount) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.points = points;
        this.email = email;
        this.reportCount = reportCount;
        this.upvoteCount = upvoteCount;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getPoints() { return points; }
    public String getEmail() { return email; }
    public int getReportCount() { return reportCount; }
    public int getUpvoteCount() { return upvoteCount; }
}