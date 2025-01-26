// Path: src/main/java/com/iskonnect/controllers/AdminReportsController.java
package com.iskonnect.controllers;

import com.iskonnect.services.AdminReportService;
import com.iskonnect.services.AdminReportService.ReportEntry;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.sql.SQLException;
import java.awt.Desktop;
import java.net.URI;

public class AdminReportsController {
    @FXML private TableView<ReportEntry> reportsTable;
    @FXML private TableColumn<ReportEntry, String> titleColumn;
    @FXML private TableColumn<ReportEntry, String> descriptionColumn;
    @FXML private TableColumn<ReportEntry, String> uploaderColumn;
    @FXML private TableColumn<ReportEntry, String> reporterColumn;
    @FXML private TableColumn<ReportEntry, String> reasonColumn;
    @FXML private TableColumn<ReportEntry, Integer> countColumn;
    @FXML private TextField searchField;
    @FXML private ProgressIndicator loadingIndicator;

    private final AdminReportService reportService;
    private ObservableList<ReportEntry> reportsList;
    private FilteredList<ReportEntry> filteredReports;


    public AdminReportsController() {
        this.reportService = new AdminReportService();
    }

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        loadReports();
    }

    private void setupColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        uploaderColumn.setCellValueFactory(new PropertyValueFactory<>("uploaderId"));
        reporterColumn.setCellValueFactory(new PropertyValueFactory<>("reporterId"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("reportCount"));

        // Make title clickable
        titleColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText(item);
                    link.setOnAction(e -> openMaterial(getTableView().getItems().get(getIndex())));
                    setGraphic(link);
                }
            }
        });

        // Add delete button column
        TableColumn<ReportEntry, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                deleteBtn.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        reportsTable.getColumns().add(actionCol);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredReports != null) {
                filteredReports.setPredicate(report -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return report.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                           report.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                           report.getUploaderId().toLowerCase().contains(lowerCaseFilter) ||
                           report.getReason().toLowerCase().contains(lowerCaseFilter);
                });
            }
        });
    }

    private void loadReports() {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            try {
                reportsList = reportService.getReportedMaterials();
                filteredReports = new FilteredList<>(reportsList, p -> true);
                
                javafx.application.Platform.runLater(() -> {
                    reportsTable.setItems(filteredReports);
                    loadingIndicator.setVisible(false);
                });
            } catch (SQLException e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Error loading reports", e.getMessage());
                    loadingIndicator.setVisible(false);
                });
            }
        }).start();
    }

    private void handleDelete(ReportEntry report) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Reported Material");
        confirm.setContentText("Are you sure you want to delete this material? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (reportService.deleteMaterial(report.getMaterialId())) {
                        reportsList.remove(report); // Remove from the source list instead
                        showSuccess("Material deleted successfully");
                    } else {
                        showError("Delete Failed", "Could not delete the material");
                    }
                } catch (SQLException e) {
                    showError("Delete Error", e.getMessage());
                }
            }
        });

    }

    private void openMaterial(ReportEntry report) {
        try {
            Desktop.getDesktop().browse(new URI(report.getFileUrl()));
        } catch (Exception e) {
            showError("Error", "Could not open material file: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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