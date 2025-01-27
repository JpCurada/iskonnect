// Path: src/main/java/com/iskonnect/controllers/AdminDashboardController.java

package com.iskonnect.controllers;

import com.iskonnect.services.AdminDashboardService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
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

    private final AdminDashboardService dashboardService;

    public AdminDashboardController() {
        this.dashboardService = new AdminDashboardService();
    }

    @FXML
    public void initialize() {
        loadStatistics();
        loadTopMaterials();
        loadUserDistribution();
        loadTopContributors();
        loadCollegeDistribution();
    }

    private void loadStatistics() {
        try {
            AdminDashboardService.DashboardStats stats = dashboardService.getDashboardStats();
            totalStudentsLabel.setText(String.valueOf(stats.getTotalStudents()));
            totalMaterialsLabel.setText(String.valueOf(stats.getTotalMaterials()));
            reportedMaterialsLabel.setText(String.valueOf(stats.getReportedMaterials()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTopMaterials() {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Upvotes");
            
            dashboardService.getTopMaterials(10).forEach(material -> 
                series.getData().add(new XYChart.Data<>(material.getUpvotes(), material.getTitle()))
            );
            
            topMaterialsChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserDistribution() {
        try {
            AdminDashboardService.UserDistribution distribution = dashboardService.getUserDistribution();
            
            PieChart.Data activeData = new PieChart.Data("Active Contributors", distribution.getActiveUsers());
            PieChart.Data inactiveData = new PieChart.Data("Non-Contributors", distribution.getInactiveUsers());
            
            userDistributionChart.getData().addAll(activeData, inactiveData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTopContributors() {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Received Upvotes");
            
            dashboardService.getTopContributors(10).forEach(contributor -> 
                series.getData().add(new XYChart.Data<>(contributor.getUpvotes(), contributor.getName()))
            );
            
            topContributorsChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCollegeDistribution() {
        try {
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Materials");
            
            dashboardService.getCollegeDistribution().forEach(college -> 
                series.getData().add(new XYChart.Data<>(college.getMaterialCount(), college.getCollegeName()))
            );
            
            collegeDistributionChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}