// Path: src/main/java/com/iskonnect/controllers/MaterialCardController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.models.Vote;
import com.iskonnect.services.VoteService;
import com.iskonnect.services.BookmarkService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MaterialCardController {
    @FXML private VBox cardContainer;
    @FXML private Label titleLabel;
    @FXML private Label subjectLabel;
    @FXML private Label collegeTag;
    @FXML private Label courseTag;
    @FXML private Label contributorLabel;
    @FXML private Button upvoteButton;
    @FXML private Button bookmarkButton;
    @FXML private Label voteCountLabel;
    
    private Material material;
    private VoteService voteService;
    private BookmarkService bookmarkService;
    private boolean isBookmarked = false;

    @FXML
    public void initialize() {
        voteService = new VoteService();
        bookmarkService = new BookmarkService();
        
        upvoteButton.setOnAction(e -> {
            e.consume();
            handleVote();
        });

        bookmarkButton.setOnAction(e -> {
            e.consume();
            handleBookmark();
        });
        
        cardContainer.setOnMouseClicked(e -> openMaterial());
    }

    public void setMaterial(Material material) {
        this.material = material;
        updateCard();
        checkBookmarkState();
        updateVoteIconState(); // New method to update the vote icon based on the user's vote
    }
    
    private void updateVoteIconState() {
        String userId = UserSession.getInstance().getUserId();
        if (voteService.voteExists(material.getMaterialId(), userId)) {
            String currentVoteType = voteService.getVoteType(material.getMaterialId(), userId);
            updateVoteIcon(currentVoteType.equals("UPVOTE")); // Set icon based on current vote type
        } else {
            updateVoteIcon(false); // No vote, set to unfilled
        }
    }

    private void updateCard() {
        titleLabel.setText(material.getTitle());
        subjectLabel.setText(material.getSubject());
        collegeTag.setText(material.getCollege());
        courseTag.setText(material.getCourse());
        contributorLabel.setText("By " + material.getUploaderName());
        voteCountLabel.setText(String.valueOf(voteService.getVoteCount(material.getMaterialId())));
    }

    private void checkBookmarkState() {
        String userId = UserSession.getInstance().getUserId();
        isBookmarked = bookmarkService.isBookmarked(material.getMaterialId(), userId);
        updateBookmarkIcon();
    }

    private void handleVote() {
        String userId = UserSession.getInstance().getUserId();
        String voteType = "UPVOTE"; // Assume this is the intended action
    
        // Check if the user has already voted
        if (voteService.voteExists(material.getMaterialId(), userId)) {
            // If they have voted, check the current vote type
            String currentVoteType = voteService.getVoteType(material.getMaterialId(), userId); // You need to implement this method
            if (currentVoteType.equals("UPVOTE")) {
                // User is trying to upvote again, remove the upvote
                if (voteService.removeVote(material.getMaterialId(), userId)) {
                    int currentVotes = Integer.parseInt(voteCountLabel.getText());
                    voteCountLabel.setText(String.valueOf(currentVotes - 1)); // Decrease the vote count
                    updateVoteIcon(false); // Update the icon to indicate no vote
                }
            } else {
                // User has downvoted, change to upvote
                voteType = "UPVOTE";
                Vote vote = new Vote(material.getMaterialId(), userId, voteType);
                if (voteService.updateVote(vote)) {
                    int currentVotes = Integer.parseInt(voteCountLabel.getText());
                    voteCountLabel.setText(String.valueOf(currentVotes + 1)); // Increase the vote count
                    updateVoteIcon(true); // Update the icon to indicate an upvote
                }
            }
        } else {
            // User has not voted yet, add the vote
            Vote vote = new Vote(material.getMaterialId(), userId, voteType);
            if (voteService.addVote(vote)) {
                int currentVotes = Integer.parseInt(voteCountLabel.getText());
                voteCountLabel.setText(String.valueOf(currentVotes + 1)); // Increase the vote count
                updateVoteIcon(true); // Update the icon to indicate an upvote
            }
        }
    }

    private void handleBookmark() {
        String userId = UserSession.getInstance().getUserId();
        boolean success;
        
        if (isBookmarked) {
            success = bookmarkService.removeBookmark(material.getMaterialId(), userId);
        } else {
            success = bookmarkService.addBookmark(material.getMaterialId(), userId);
        }
        
        if (success) {
            isBookmarked = !isBookmarked;
            updateBookmarkIcon();
        }
    }
    
    private void updateVoteIcon(boolean isVoted) {
        ImageView imageView = (ImageView) upvoteButton.getGraphic();
        String iconPath = isVoted ? 
            "/images/icons/upvote.png" : 
            "/images/icons/upvote-unfilled.png";
        imageView.setImage(new Image(getClass().getResourceAsStream(iconPath)));
    }

    private void updateBookmarkIcon() {
        ImageView imageView = (ImageView) bookmarkButton.getGraphic();
        String iconPath = isBookmarked ? 
            "/images/icons/bookmark-filled.png" : 
            "/images/icons/bookmark-unfilled.png";
        imageView.setImage(new Image(getClass().getResourceAsStream(iconPath)));
    }
    
    private void openMaterial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/MaterialDetailsDialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            MaterialDetailsDialogController controller = loader.getController();
            controller.setMaterial(material);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Material Details");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening material details");
            e.printStackTrace();
        }
    }
        
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return cardContainer;
    }
}