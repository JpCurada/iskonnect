// Path: src/main/java/com/iskonnect/controllers/MaterialDetailsDialogController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.BadgeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.net.URI;

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
    private Stage dialogStage;

    public void setMaterial(Material material) {
        this.material = material;
        updateContent();
    }

    @FXML
    private void initialize() {
        badgeService = new BadgeService();
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
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}