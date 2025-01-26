// Path: src/main/java/com/iskonnect/controllers/ReportDialogController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.ReportService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

public class ReportDialogController {
    @FXML private ComboBox<String> reasonComboBox;
    @FXML private TextArea additionalInfoArea;
    
    private Material material;
    private ReportService reportService;

    @FXML
    private void initialize() {
        reportService = new ReportService();
        
        // Initialize report reasons
        reasonComboBox.setItems(FXCollections.observableArrayList(
            "Violent or inappropriate content",
            "Inaccurate information",
            "Irrelevant to subject",
            "Copyright violation",
            "Spam or misleading",
            "Poor quality or unreadable",
            "Duplicate content",
            "Other"
        ));
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @FXML
    private void handleSubmit() {
        if (reasonComboBox.getValue() == null) {
            showError("Please select a reason for reporting");
            return;
        }

        try {
            reportService.submitReport(
                material.getMaterialId(),
                UserSession.getInstance().getUserId(),
                reasonComboBox.getValue(),
                additionalInfoArea.getText()
            );

            showSuccess("Report submitted successfully");
            closeDialog();
        } catch (Exception e) {
            showError("Failed to submit report");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) reasonComboBox.getScene().getWindow()).close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}