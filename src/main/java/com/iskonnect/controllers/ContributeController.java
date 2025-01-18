package com.iskonnect.controllers;

import com.iskonnect.services.MaterialService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContributeController {
    @FXML private TextField materialNameField;
    @FXML private TextArea descriptionField;  // Changed from TextField to TextArea
    @FXML private TextField subjectField;
    @FXML private TextField collegeField;
    @FXML private TextField courseField;
    @FXML private Button selectFileButton;
    @FXML private Button uploadButton;
    @FXML private Label statusLabel;
    @FXML private Label pointsLabel;
    @FXML private Label materialsCountLabel;
    @FXML private Label fileNameLabel;

    private File selectedFile;
    private final MaterialService materialService;

    public ContributeController() {
        this.materialService = new MaterialService();
    }

    @FXML
    private void initialize() {

        loadUserStats();
    }

    private void loadUserStats() {
        try {
            MaterialService.UserStats stats = materialService.getUserStats(UserSession.getInstance().getUserId());
            pointsLabel.setText(String.valueOf(stats.getPoints()));
            materialsCountLabel.setText(String.valueOf(stats.getMaterialsCount()));
        } catch (Exception e) {
            showError("Failed to load user statistics");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Document Files", "*.pdf", "*.doc", "*.docx", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    
        selectedFile = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            statusLabel.setText("");
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleUpload() {
        if (!validateFields()) {
            return;
        }
    
        // Check file size (e.g., 10MB limit)
        long maxFileSize = 10 * 1024 * 1024; // 10MB in bytes
        if (selectedFile != null && selectedFile.length() > maxFileSize) {
            showError("File is too large. Maximum size is 10MB.");
            return;
        }
    
        // Check file type
        if (selectedFile != null && !isValidFileType(selectedFile)) {
            showError("Invalid file type. Only PDF, DOC, DOCX, TXT, and common image formats are allowed.");
            return;
        }
    
        try {
            disableUploadDuringProcess(true);
            statusLabel.setText("Uploading...");
    
            MaterialService.MaterialUploadRequest request = new MaterialService.MaterialUploadRequest(
                materialNameField.getText().trim(),
                descriptionField.getText().trim(),
                subjectField.getText().trim(),
                collegeField.getText().trim(),
                courseField.getText().trim(),
                selectedFile
            );
    
            materialService.uploadMaterial(UserSession.getInstance().getUserId(), request);
            
            showSuccess("Material uploaded successfully!");
            clearFields();
            loadUserStats();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("response code: 400")) {
                showError("Upload failed: The file may be too large or in an unsupported format.");
            } else if (errorMessage.contains("response code: 401")) {
                showError("Upload failed: Authentication error. Please try logging in again.");
            } else if (errorMessage.contains("response code: 403")) {
                showError("Upload failed: You don't have permission to upload files.");
            } else {
                showError("Upload failed: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            disableUploadDuringProcess(false);
        }
    }
    
    private boolean isValidFileType(File file) {
        String filename = file.getName().toLowerCase();
        return filename.endsWith(".pdf") || 
               filename.endsWith(".doc") || 
               filename.endsWith(".docx") || 
               filename.endsWith(".txt") || 
               filename.endsWith(".png") || 
               filename.endsWith(".jpg") || 
               filename.endsWith(".jpeg") || 
               filename.endsWith(".gif");
    }

    private boolean validateFields() {
        if (materialNameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
            subjectField.getText().isEmpty() || collegeField.getText().isEmpty() ||
            courseField.getText().isEmpty() || selectedFile == null) {
            
            showError("Please fill in all fields and select a file.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #FF0000;"); // Red color for errors
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #008000;"); // Green color for success
    }

    private void clearFields() {
        materialNameField.clear();
        descriptionField.clear();
        subjectField.clear();
        collegeField.clear();
        courseField.clear();
        selectedFile = null;
        statusLabel.setText("");
    }

    private void setupFieldValidation() {
        // Add text change listeners for real-time validation
        materialNameField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.length() > 100) {
                materialNameField.setText(old);
                showError("Material name cannot exceed 100 characters");
            }
        });

        descriptionField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.length() > 500) {
                descriptionField.setText(old);
                showError("Description cannot exceed 500 characters");
            }
        });

        // Add focus listeners for field validation
        materialNameField.focusedProperty().addListener((obs, old, newValue) -> {
            if (!newValue && materialNameField.getText().isEmpty()) {
                showError("Material name is required");
            }
        });

        subjectField.focusedProperty().addListener((obs, old, newValue) -> {
            if (!newValue && subjectField.getText().isEmpty()) {
                showError("Subject is required");
            }
        });

        collegeField.focusedProperty().addListener((obs, old, newValue) -> {
            if (!newValue && collegeField.getText().isEmpty()) {
                showError("College is required");
            }
        });

        courseField.focusedProperty().addListener((obs, old, newValue) -> {
            if (!newValue && courseField.getText().isEmpty()) {
                showError("Course is required");
            }
        });
    }

    private void disableUploadDuringProcess(boolean disable) {
        uploadButton.setDisable(disable);
        selectFileButton.setDisable(disable);
        materialNameField.setDisable(disable);
        descriptionField.setDisable(disable);
        subjectField.setDisable(disable);
        collegeField.setDisable(disable);
        courseField.setDisable(disable);
    }
}