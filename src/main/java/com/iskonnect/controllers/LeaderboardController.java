package com.iskonnect.controllers;

import com.iskonnect.services.LeaderboardService;
import com.iskonnect.services.LeaderboardService.LeaderboardEntry;
import com.iskonnect.services.LeaderboardService.LeaderboardStats;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LeaderboardController {
    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML private TableColumn<LeaderboardEntry, String> nameColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> pointsColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> materialsColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> badgesColumn;

    @FXML private ComboBox<String> timeFilter;
    @FXML private Text totalContributorsText;
    @FXML private Text totalMaterialsText;
    @FXML private Text totalPointsText;

    private final LeaderboardService leaderboardService;
    private final NumberFormat numberFormat;

    public LeaderboardController() {
        this.leaderboardService = new LeaderboardService();
        this.numberFormat = NumberFormat.getNumberInstance(Locale.US);
    }

    @FXML
    public void initialize() {
        leaderboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupTimeFilter();
        setupTable();
        loadLeaderboardData();
        updateStatistics();

        // Add listener for time filter changes
        timeFilter.setOnAction(e -> loadLeaderboardData());
    }

    private void setupTimeFilter() {
        timeFilter.setItems(FXCollections.observableArrayList(
                "All Time",
                "This Month",
                "This Week"
        ));
        timeFilter.setValue("All Time");
    }

    private void setupTable() {
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        materialsColumn.setCellValueFactory(new PropertyValueFactory<>("materials"));
        badgesColumn.setCellValueFactory(new PropertyValueFactory<>("badges"));

        // Format numbers in table cells
        pointsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : numberFormat.format(item));
            }
        });

        // Apply rank-based styles
        leaderboardTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LeaderboardEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (entry == null || empty) {
                    setStyle(""); // Reset style for empty rows
                } else {
                    switch (entry.getRank()) {
                        case 1 -> setStyle("-fx-background-color: #FFECB3;"); // Gold
                        case 2 -> setStyle("-fx-background-color: #D7CCC8;"); // Silver
                        case 3 -> setStyle("-fx-background-color: #CFD8DC;"); // Bronze
                        default -> setStyle(""); // Default style
                    }
                }
            }
        });
    }

    private void loadLeaderboardData() {
        try {
            List<LeaderboardEntry> entries = leaderboardService.getLeaderboard(timeFilter.getValue());

            // Set ranks
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).setRank(i + 1);
            }

            leaderboardTable.setItems(FXCollections.observableArrayList(entries));
        } catch (Exception e) {
            showError("Failed to load leaderboard data");
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        try {
            LeaderboardStats stats = leaderboardService.getStatistics();
            totalContributorsText.setText(numberFormat.format(stats.getTotalContributors()));
            totalMaterialsText.setText(numberFormat.format(stats.getTotalMaterials()));
            totalPointsText.setText(numberFormat.format(stats.getTotalPoints()));
        } catch (Exception e) {
            showError("Failed to load statistics");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}