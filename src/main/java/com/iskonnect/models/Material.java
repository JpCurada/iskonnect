// Path: src/main/java/com/iskonnect/models/Material.java
package com.iskonnect.models;

import java.time.LocalDateTime;

public class Material {
    private int materialId;
    private String title;
    private String description;
    private String subject;
    private String college;
    private String course;
    private String fileUrl;
    private String fileName;
    private String uploaderId;
    private String uploaderName;
    private String contributorName;
    private LocalDateTime uploadDate;
    private int upvotes;
    private int downvotes;

    public Material(String title, String description, String subject, String college, 
                   String course, String uploaderId) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.college = college;
        this.course = course;
        this.uploaderId = uploaderId;
        this.uploadDate = LocalDateTime.now();
    }

    // Getters
    public int getMaterialId() { return materialId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getCollege() { return college; }
    public String getCourse() { return course; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public String getUploaderId() { return uploaderId; }
    public String getUploaderName() { return uploaderName; }
    public String getContributorName() { return contributorName; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }
    public int getTotalVotes() { return upvotes - downvotes; }

    // Setters
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }
    public void setContributorName(String contributorName) { this.contributorName = contributorName; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public void setDownvotes(int downvotes) { this.downvotes = downvotes; }
}