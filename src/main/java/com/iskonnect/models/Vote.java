// Path: src/main/java/com/iskonnect/models/Vote.java
package com.iskonnect.models;

public class Vote {
    private int voteId;
    private int materialId;
    private String userId;
    private String voteType;  // UPVOTE or DOWNVOTE

    public Vote(int materialId, String userId, String voteType) {
        this.materialId = materialId;
        this.userId = userId;
        this.voteType = voteType;
    }

    // Getters
    public int getVoteId() { return voteId; }
    public int getMaterialId() { return materialId; }
    public String getUserId() { return userId; }
    public String getVoteType() { return voteType; }
}