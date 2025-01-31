package com.iskonnect.controllers;

import com.iskonnect.services.MaterialService;
import com.iskonnect.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.input.DragEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributeController {
    @FXML private TextField materialNameField;
    @FXML private TextArea descriptionField; // Changed from TextField to TextArea
    @FXML private TextField subjectField;
    @FXML private ComboBox<String> collegeComboBox; // Changed to ComboBox
    @FXML private ComboBox<String> courseComboBox;  // Changed to ComboBox
    @FXML private Button selectFileButton;
    @FXML private Button uploadButton;
    @FXML private Label statusLabel;
    @FXML private Label pointsLabel;
    @FXML private Label materialsCountLabel;
    @FXML private Label fileNameLabel;
    @FXML private VBox dropArea;

    private File selectedFile;
    private final MaterialService materialService;

    // Map to store colleges and their corresponding courses
    private final Map<String, List<String>> collegeToCoursesMap = new HashMap<>();

    public ContributeController() {
        this.materialService = new MaterialService();
    }

    @FXML
    private void initialize() {
        // Load user statistics (points and materials shared)
        loadUserStats();

        // Populate colleges and courses
        populateCollegeToCoursesMap();

        // Add placeholder text for collegeComboBox and courseComboBox
        initializeComboBoxes();

        // Add listener to dynamically update courses based on selected college
        collegeComboBox.setOnAction(event -> updateCourseComboBox());

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
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            // Handle the first file (you can modify this to handle multiple files if needed)
            File file = dragboard.getFiles().get(0);
            // Update the file name label and handle the file selection
            fileNameLabel.setText(file.getName());
            selectedFile = file;
            // You can also set the file to a variable for further processing
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private void handleDragExited(DragEvent event) {
        // Optional: You can add visual feedback when the drag exits the drop area
        event.consume();
    }


    private void populateCollegeToCoursesMap() {
        collegeToCoursesMap.put("OUS", List.of(
                "BSENTREP", "BABR", "BSBAHRM", "BSBAMM", "BSOA", "BSTM", "BPA"));
        collegeToCoursesMap.put("CAF", List.of("BSA", "BSMA", "BSBAFM"));
        collegeToCoursesMap.put("CADBE", List.of("BS-ARCH", "BSID", "BSEP"));
        collegeToCoursesMap.put("CAL", List.of("ABE", "ABF", "ABPHILO", "ABTA"));
        collegeToCoursesMap.put("CBA", List.of("BSBA-MM", "BSBA-HRDM", "BSEntrep", "BSOA"));
        collegeToCoursesMap.put("COC", List.of("BAPR", "ABJ", "BA Broadcasting", "ABCR"));
        collegeToCoursesMap.put("CCIS", List.of("BSCS", "BSIT"));
        collegeToCoursesMap.put("COED", List.of(
                "BTLEd", "BLIS", "BSEd", "BEEd", "BECEd"));
        collegeToCoursesMap.put("CE", List.of(
                "BSCE", "BSCpE", "BSEE", "BSECE", "BSIE", "BSME", "BSRE"));
        collegeToCoursesMap.put("CHK", List.of("BPE"));
        collegeToCoursesMap.put("CPSPA", List.of("BAPS", "BAPE", "BAIS", "BPA"));
        collegeToCoursesMap.put("CSSD", List.of("BC", "ABH", "BSE", "BSPSY", "BSS"));
        collegeToCoursesMap.put("CS", List.of(
                "BAS", "BSAM", "BSBIO", "BSCHEM", "BSFT", "BSM", "BSND", "BSP"));
        collegeToCoursesMap.put("CTHTM", List.of("BSTM", "BTM", "BSHM"));
    }

    private void initializeComboBoxes() {
        // Populate the collegeComboBox with colleges
        collegeComboBox.setItems(FXCollections.observableArrayList(collegeToCoursesMap.keySet()));

        // Add placeholder text to the collegeComboBox
        collegeComboBox.setPromptText("Select a College");

        // Add placeholder text to the courseComboBox
        courseComboBox.setPromptText("Select a Course");

        // Disable the courseComboBox until a college is selected
        courseComboBox.setDisable(true);
    }

    private void updateCourseComboBox() {
        // Get the selected college
        String selectedCollege = collegeComboBox.getValue();

        if (selectedCollege != null) {
            // Fetch the courses for the selected college
            List<String> courses = collegeToCoursesMap.get(selectedCollege);

            if (courses != null) {
                // Update the courseComboBox with the relevant courses
                courseComboBox.setItems(FXCollections.observableArrayList(courses));
                courseComboBox.setDisable(false); // Enable the combo box
            }
        } else {
            // If no college is selected, clear and disable the courseComboBox
            courseComboBox.setItems(FXCollections.observableArrayList());
            courseComboBox.setDisable(true);
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

        try {
            disableUploadDuringProcess(true);
            statusLabel.setText("Uploading...");

            MaterialService.MaterialUploadRequest request = new MaterialService.MaterialUploadRequest(
                    materialNameField.getText().trim(),
                    descriptionField.getText().trim(),
                    subjectField.getText().trim(),
                    collegeComboBox.getValue(), // Use ComboBox value for college
                    courseComboBox.getValue(),  // Use ComboBox value for course
                    selectedFile
            );

            materialService.uploadMaterial(UserSession.getInstance().getUserId(), request);

            showSuccess("Material uploaded successfully!");
            clearFields();
            loadUserStats();
        } catch (Exception e) {
            showError("Upload failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disableUploadDuringProcess(false);
        }
    }

    private boolean validateFields() {
        if (materialNameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                subjectField.getText().isEmpty() || collegeComboBox.getValue() == null ||
                courseComboBox.getValue() == null || selectedFile == null) {

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
        collegeComboBox.getSelectionModel().clearSelection();
        courseComboBox.getSelectionModel().clearSelection();
        courseComboBox.setDisable(true);
        selectedFile = null;
        fileNameLabel.setText("No file selected");
        statusLabel.setText("");
    }

    private void disableUploadDuringProcess(boolean disable) {
        uploadButton.setDisable(disable);
        selectFileButton.setDisable(disable);
        materialNameField.setDisable(disable);
        descriptionField.setDisable(disable);
        subjectField.setDisable(disable);
        collegeComboBox.setDisable(disable);
        courseComboBox.setDisable(disable);
    }
}