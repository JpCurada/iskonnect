package com.iskonnect.services;

import com.iskonnect.models.Vote;
import com.iskonnect.utils.DatabaseConnection;
import java.sql.*;

public class VoteService {
    public boolean addVote(Vote vote) {
        String query = "INSERT INTO votes (material_id, user_id, vote_type) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, vote.getMaterialId());
            stmt.setString(2, vote.getUserId());
            stmt.setString(3, vote.getVoteType());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
}