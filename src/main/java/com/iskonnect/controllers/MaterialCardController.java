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
        String voteType = "UPVOTE";
        if (voteService.addVote(new Vote(material.getMaterialId(), 
                                       UserSession.getInstance().getUserId(), 
                                       voteType))) {
            int currentVotes = Integer.parseInt(voteCountLabel.getText());
            voteCountLabel.setText(String.valueOf(currentVotes + 1));
            updateVoteIcon(true);
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