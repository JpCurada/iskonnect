package com.iskonnect.models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private int intPoints;
    private List<Badge> badges;
    private List<Material> uploads;

    public Student(String strStudentId, String strFirstName, String strLastName, String strEmail) {
        super(strStudentId, strFirstName, strLastName, strEmail, "STUDENT");
        this.intPoints = 0;
        this.badges = new ArrayList<>();
        this.uploads = new ArrayList<>();
    }

    // Getters
    public int getPoints() { return intPoints; }
    public List<Badge> getBadges() { return badges; }
    public List<Material> getUploads() { return uploads; }

    // Setters
    public void setPoints(int intPoints) { this.intPoints = intPoints; }
}