// Path: src/main/java/com/iskonnect/controllers/MaterialCardController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.models.Vote;
import com.iskonnect.services.VoteService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.awt.Desktop;
import java.net.URI;

public class MaterialCardController {
    @FXML private VBox cardContainer;
    @FXML private Label titleLabel;
    @FXML private Label subjectLabel;
    @FXML private Label collegeTag;
    @FXML private Label courseTag;
    @FXML private Label contributorLabel;
    @FXML private Button upvoteButton;
    @FXML private Label voteCountLabel;
    
    private Material material;
    private VoteService voteService;
    private boolean hasVoted = false;

    @FXML
    public void initialize() {
        voteService = new VoteService();
        
        upvoteButton.setOnAction(e -> {
            e.consume();
            handleVote();
        });
        
        cardContainer.setOnMouseClicked(e -> openMaterial());
    }

    public void setMaterial(Material material) {
        this.material = material;
        updateCard();
    }

    private void updateCard() {
        titleLabel.setText(material.getTitle());
        subjectLabel.setText(material.getSubject());
        collegeTag.setText(material.getCollege());
        courseTag.setText(material.getCourse());
        contributorLabel.setText("By " + material.getUploaderId());
        voteCountLabel.setText(String.valueOf(voteService.getVoteCount(material.getMaterialId())));
    }

    private void handleVote() {
        String voteType = hasVoted ? "DOWNVOTE" : "UPVOTE";
        if (voteService.addVote(new Vote(material.getMaterialId(), 
                                       UserSession.getInstance().getUserId(), 
                                       voteType))) {
            int currentVotes = Integer.parseInt(voteCountLabel.getText());
            voteCountLabel.setText(String.valueOf(currentVotes + (hasVoted ? -1 : 1)));
            hasVoted = !hasVoted;
            
            // Update button style
            updateVoteButtonStyle();
        }
    }
    
    private void updateVoteButtonStyle() {
        if (hasVoted) {
            upvoteButton.getStyleClass().add("voted");
        } else {
            upvoteButton.getStyleClass().remove("voted");
        }
    }
    
    private void openMaterial() {
        try {
            Desktop.getDesktop().browse(new URI(material.getFileUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            // Show error alert
            showError("Failed to open material");
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