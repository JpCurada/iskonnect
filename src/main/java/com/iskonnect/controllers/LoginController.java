package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private DatabaseConnection dbConnection;

    @FXML
    private void initialize() {
        dbConnection = new DatabaseConnection();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (validateLogin(email, password)) {
            // Switch to dashboard based on user type
            switchToDashboard();
        } else {
            showError("Invalid credentials");
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            Main.setRoot("auth/register");
        } catch (Exception e) {
            showError("Could not load registration page");
        }
    }

    private boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM user_credentials WHERE email = ? AND password_hash = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password); // In production, use proper password hashing
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchToDashboard() {
        try {
            Main.setRoot("student/dashboard");
        } catch (Exception e) {
            showError("Could not load dashboard");
        }
    }
}