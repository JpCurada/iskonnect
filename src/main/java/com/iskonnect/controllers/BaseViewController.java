// Path: src/main/java/com/iskonnect/controllers/BaseViewController.java
package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.IOException;

public abstract class BaseViewController {
    // Match exact FXML IDs from admin/base.fxml and student/base.fxml
    @FXML protected VBox contentArea;
    @FXML protected Text adminNameText;  // For admin
    @FXML protected Text adminIdText;    // For admin
    @FXML protected Text fullNameText;   // For student
    @FXML protected Text studentNumberText; // For student
    
    @FXML
    protected void initialize() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            // Check which view we're in and set appropriate text fields
            if (adminNameText != null && adminIdText != null) {
                // Admin view
                adminNameText.setText(session.getFullName());
                adminIdText.setText(session.getUserId());
            } else if (fullNameText != null && studentNumberText != null) {
                // Student view
                fullNameText.setText(session.getFullName());
                studentNumberText.setText(session.getUserId());
            }
        }
    }

    @FXML
    protected void handleLogout() {
        UserSession.getInstance().endSession();
        try {
            Main.setLoginRoot("auth/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadPage(String viewType, String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + viewType + "/" + page + ".fxml"));
            VBox pageContent = loader.load();
            contentArea.getChildren().setAll(pageContent);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + page + ": " + e.getMessage());
        }
    }
}