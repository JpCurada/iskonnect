package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import com.iskonnect.application.Main;

public class BaseController {
    @FXML private Label nameLabel;
    @FXML private Label studentNumberLabel;
    
    @FXML
    private void initialize() {
        // Set user info (this would come from your login session)
        nameLabel.setText("John Doe");
        studentNumberLabel.setText("2024-00000-MN-0");
    }

    @FXML
    private void navigateToHome() {
        try {
            Main.setMainRoot("content/home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToContribute() {
        try {
            Main.setMainRoot("content/contribute");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToLeaderboard() {
        try {
            Main.setMainRoot("content/leaderboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToContributions() {
        try {
            Main.setMainRoot("content/contributions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Main.setLoginRoot("auth/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}