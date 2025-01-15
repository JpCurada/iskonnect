package com.iskonnect.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Material {
    private int intMaterialId;
    private String strTitle;
    private String strDescription;
    private String strSubject;
    private String strCollege;
    private String strCourse;
    private String strFileUrl;
    private String strUploaderId;
    private LocalDateTime datetimeUploadDate;
    private List<Vote> votes;

    public Material(String strTitle, String strDescription, String strSubject, String strCollege, String strCourse, String strUploaderId) {
        this.strTitle = strTitle;
        this.strDescription = strDescription;
        this.strSubject = strSubject;
        this.strCollege = strCollege;
        this.strCourse = strCourse;
        this.strUploaderId = strUploaderId;
        this.datetimeUploadDate = LocalDateTime.now();
        this.votes = new ArrayList<>();
    }

    // Getters
    public int getMaterialId() { return intMaterialId; }
    public String getTitle() { return strTitle; }
    public String getDescription() { return strDescription; }
    public String getSubject() { return strSubject; }
    public String getCollege() { return strCollege; }
    public String getCourse() { return strCourse; }
    public String getFileUrl() { return strFileUrl; }
    public String getUploaderId() { return strUploaderId; }
    public LocalDateTime getUploadDate() { return datetimeUploadDate; }
    public List<Vote> getVotes() { return votes; }

    // Setters
    public void setTitle(String strTitle) { this.strTitle = strTitle; }
    public void setDescription(String strDescription) { this.strDescription = strDescription; }
    public void setFileUrl(String strFileUrl) { this.strFileUrl = strFileUrl; }
}