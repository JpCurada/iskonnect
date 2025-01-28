package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.BadgeService;
import com.iskonnect.services.VoteService; // Import VoteService
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MaterialDetailsDialogController {
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label subjectLabel;
    @FXML private Label uploaderLabel;
    @FXML private Label badgeLabel;
    @FXML private Label votesLabel;
    @FXML private Button reportButton;
    @FXML private Button downloadButton;

    private Material material;
    private BadgeService badgeService;
    private VoteService voteService; // Add VoteService
    private Stage dialogStage;
    private ScheduledExecutorService voteUpdateService; // Scheduled service for vote updates

    public void setMaterial(Material material) {
        this.material = material;
        updateContent();
    }

    @FXML
    private void initialize() {
        badgeService = new BadgeService();
        voteService = new VoteService(); // Initialize VoteService
    }

    private void updateContent() {
        titleLabel.setText(material.getTitle());
        descriptionLabel.setText(material.getDescription());
        subjectLabel.setText(material.getSubject());
        uploaderLabel.setText(material.getUploaderId());
        votesLabel.setText(String.valueOf(material.getTotalVotes()));
        
        // Get highest badge of uploader
        String highestBadge = badgeService.getHighestBadge(material.getUploaderId());
        badgeLabel.setText(highestBadge != null ? highestBadge : "No badges yet");
    }

    @FXML
    private void handleReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ReportsDialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            ReportDialogController controller = loader.getController();
            controller.setMaterial(material);
            
            Stage reportStage = new Stage();
            reportStage.setTitle("Report Material");
            reportStage.initModality(Modality.WINDOW_MODAL);
            reportStage.initOwner(dialogStage);
            reportStage.setScene(scene);
            reportStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening report dialog");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDownload() {
        try {
            Desktop.getDesktop().browse(new URI(material.getFileUrl()));
        } catch (Exception e) {
            showError("Error downloading material");
            e.printStackTrace();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setOnCloseRequest(event -> stopVoteUpdateService()); // Stop service on close
        startVoteUpdateService(); // Start service when dialog is set
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Start the vote update service
    public void startVoteUpdateService() {
        voteUpdateService = Executors.newScheduledThreadPool(1);
        voteUpdateService.scheduleAtFixedRate(() -> {
            int updatedVoteCount = voteService.getVoteCount(material.getMaterialId());
            Platform.runLater(() -> votesLabel.setText(String.valueOf(updatedVoteCount)));
        }, 0, 5, TimeUnit.SECONDS); // Update every 5 seconds
    }

    // Stop the vote update service
    public void stopVoteUpdateService() {
        if (voteUpdateService != null) {
            voteUpdateService.shutdownNow();
        }
    }
}