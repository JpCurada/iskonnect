
// Path: src/main/java/com/iskonnect/models/ReportDetails.java
package com.iskonnect.models;

public class ReportDetails {
    private String materialTitle;
    private String description;
    private String uploaderId;
    private String reporterId;
    private String reason;
    private int reportCount;
    private String fileUrl;

    public ReportDetails(String materialTitle, String description, String uploaderId,
                        String reporterId, String reason, int reportCount, String fileUrl) {
        this.materialTitle = materialTitle;
        this.description = description;
        this.uploaderId = uploaderId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.reportCount = reportCount;
        this.fileUrl = fileUrl;
    }

    // Getters
    public String getMaterialTitle() { return materialTitle; }
    public String getDescription() { return description; }
    public String getUploaderId() { return uploaderId; }
    public String getReporterId() { return reporterId; }
    public String getReason() { return reason; }
    public int getReportCount() { return reportCount; }
    public String getFileUrl() { return fileUrl; }
}