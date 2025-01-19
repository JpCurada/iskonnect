// Path: src/main/java/com/iskonnect/controllers/AdminViewController.java

package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;

public class AdminViewController {
    @FXML private Text adminNameText;
    @FXML private Text adminIdText;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        initializeAdminInfo();
        loadPage("dashboard"); // Load default page
    }

    private void initializeAdminInfo() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            adminNameText.setText(session.getFullName());
            adminIdText.setText(session.getUserId());
        }
    }

    @FXML
    private void handleDashboard() {
        loadPage("dashboard");
    }

    @FXML
    private void handleUsers() {
        loadPage("users");
    }

    @FXML
    private void handleMaterials() {
        loadPage("materials");
    }

    @FXML
    private void handleReports() {
        loadPage("reports");
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().endSession();
        try {
            Main.setLoginRoot("auth/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/" + page + ".fxml"));
            VBox pageContent = loader.load();
            contentArea.getChildren().setAll(pageContent);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + page + ": " + e.getMessage());
        }
    }
}