// Path: src/main/java/com/iskonnect/models/Bookmark.java

package com.iskonnect.models;

import java.time.LocalDateTime;

public class Bookmark {
    private int bookmarkId;
    private int materialId;
    private String userId;
    private LocalDateTime createdAt;

    public Bookmark(int materialId, String userId) {
        this.materialId = materialId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public int getBookmarkId() { return bookmarkId; }
    public int getMaterialId() { return materialId; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setBookmarkId(int bookmarkId) { this.bookmarkId = bookmarkId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}