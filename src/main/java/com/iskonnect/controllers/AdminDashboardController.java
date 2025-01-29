package com.iskonnect.controllers;

import com.iskonnect.services.AdminDashboardService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.sql.SQLException;

public class AdminDashboardController {
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalMaterialsLabel;
    @FXML private Label reportedMaterialsLabel;
    @FXML private BarChart<Number, String> topMaterialsChart;
    @FXML private PieChart userDistributionChart;
    @FXML private BarChart<Number, String> topContributorsChart;
    @FXML private BarChart<Number, String> collegeDistributionChart;
    @FXML private ComboBox<String> timeRangeComboBox;

    private final AdminDashboardService dashboardService;

    public AdminDashboardController() {
        this.dashboardService = new AdminDashboardService();
    }

    @FXML
    public void initialize() {
        setupTimeRangeComboBox();
        loadDashboardData("All Time"); // Load initial data with default time range
    }

    private void setupTimeRangeComboBox() {
        timeRangeComboBox.getItems().addAll("All Time", "Past Week", "Past Month", "Past Year");
        timeRangeComboBox.setValue("All Time"); // Default value
        timeRangeComboBox.setOnAction(event -> {
            String selectedTimeRange = timeRangeComboBox.getValue();
            loadDashboardData(selectedTimeRange);
        });
    }

    private void loadDashboardData(String timeRange) {
        loadStatistics(timeRange);
        loadTopMaterials(timeRange);
        loadUserDistribution(timeRange);
        loadTopContributors(timeRange);
        loadCollegeDistribution(timeRange);
    }

    private void loadStatistics(String timeRange) {
        try {
            AdminDashboardService.DashboardStats stats = dashboardService.getDashboardStats(timeRange);
            totalStudentsLabel.setText(String.valueOf(stats.getTotalStudents()));
            totalMaterialsLabel.setText(String.valueOf(stats.getTotalMaterials()));
            reportedMaterialsLabel.setText(String.valueOf(stats.getReportedMaterials()));
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding error handling/user feedback here
        }
    }

    private void loadTopMaterials(String timeRange) {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Upvotes");
            
            dashboardService.getTopMaterials(10, timeRange).forEach(material -> 
                series.getData().add(new XYChart.Data<>(material.getUpvotes(), material.getTitle()))
            );
            
            topMaterialsChart.getData().clear(); // Clear existing data
            topMaterialsChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding error handling/user feedback here
        }
    }

    private void loadUserDistribution(String timeRange) {
        try {
            AdminDashboardService.UserDistribution distribution = dashboardService.getUserDistribution(timeRange);
            
            PieChart.Data activeData = new PieChart.Data("Active Contributors", distribution.getActiveUsers());
            PieChart.Data inactiveData = new PieChart.Data("Non-Contributors", distribution.getInactiveUsers());
            
            userDistributionChart.getData().clear(); // Clear existing data
            userDistributionChart.getData().addAll(activeData, inactiveData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding error handling/user feedback here
        }
    }

    private void loadTopContributors(String timeRange) {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Received Upvotes");
            
            dashboardService.getTopContributors(10, timeRange).forEach(contributor -> 
                series.getData().add(new XYChart.Data<>(contributor.getUpvotes(), contributor.getName()))
            );
            
            topContributorsChart.getData().clear(); // Clear existing data
            topContributorsChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding error handling/user feedback here
        }
    }

    private void loadCollegeDistribution(String timeRange) {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Materials");
            
            dashboardService.getCollegeDistribution(timeRange).forEach(college -> 
                series.getData().add(new XYChart.Data<>(college.getMaterialCount(), college.getCollegeName()))
            );
            
            collegeDistributionChart.getData().clear(); // Clear existing data
            collegeDistributionChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding error handling/user feedback here
        }
    }
}