package com.iskonnect.services;

import com.iskonnect.models.Vote;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;

public class VoteService {
    
    // Check if a vote already exists for the user and material
    public boolean voteExists(int materialId, String userId) {
        String query = "SELECT COUNT(*) FROM votes WHERE material_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if a vote exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add a vote only if it doesn't already exist
    public boolean addVote(Vote vote) {
        if (voteExists(vote.getMaterialId(), vote.getUserId())) {
            // If a vote exists, return false to indicate failure
            return false;
        } else {
            // If no vote exists, insert a new one
            String insertQuery = "INSERT INTO votes (material_id, user_id, vote_type) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                
                stmt.setInt(1, vote.getMaterialId());
                stmt.setString(2, vote.getUserId());
                stmt.setString(3, vote.getVoteType());
                
                return stmt.executeUpdate() > 0; // Return true if the insert was successful
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean updateVote(Vote vote) {
        String updateQuery = "UPDATE votes SET vote_type = ? WHERE material_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setString(1, vote.getVoteType());
            stmt.setInt(2, vote.getMaterialId());
            stmt.setString(3, vote.getUserId());
            
            return stmt.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getVoteType(int materialId, String userId) {
        String query = "SELECT vote_type FROM votes WHERE material_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("vote_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no vote exists
    }

    public int getVoteCount(int materialId) {
        String query = """
            SELECT 
                COUNT(CASE WHEN vote_type = 'UPVOTE' THEN 1 END) -
                COUNT(CASE WHEN vote_type = 'DOWNVOTE' THEN 1 END) as vote_count
            FROM votes 
            WHERE material_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("vote_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean removeVote(int materialId, String userId) {
        String deleteQuery = "DELETE FROM votes WHERE material_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            
            stmt.setInt(1, materialId);
            stmt.setString(2, userId);
            
            return stmt.executeUpdate() > 0; // Return true if the delete was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}