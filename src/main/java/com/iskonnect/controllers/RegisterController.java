package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {
    @FXML private TextField studentNumberField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private Button registerButton;

    private DatabaseConnection dbConnection;

    @FXML
    private void initialize() {
        dbConnection = new DatabaseConnection();
        courseComboBox.getItems().addAll("BSIT", "BSCS", "BSIS");
    }

    @FXML
    private void handleRegister() {
        if (validateInputs()) {  // Called here before registration
            if (registerUser()) {
                showSuccess("Registration successful!");
                switchToLogin();
            } else {
                showError("Registration failed");
            }
        }
    }

    // Add the validation method here
    private boolean validateInputs() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }
        try {
            Integer.parseInt(studentNumberField.getText());
        } catch (NumberFormatException e) {
            showError("Student number must be numeric");
            return false;
        }
        return true;
    }

    @FXML
    private void switchToLogin() {
        try {
            Main.setRoot("auth/login");
        } catch (Exception e) {
            showError("Could not return to login page");
        }
    }

    private boolean registerUser() {
        String credentialsQuery = "INSERT INTO user_credentials (user_id, email, password_hash) VALUES (?, ?, ?)";
        String userQuery = "INSERT INTO users (user_id, username, user_type, points) VALUES (?, ?, 'STUDENT', 0)";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            int userId = Integer.parseInt(studentNumberField.getText());
            
            // Insert into user_credentials
            PreparedStatement credStmt = conn.prepareStatement(credentialsQuery);
            credStmt.setInt(1, userId);
            credStmt.setString(2, emailField.getText());
            credStmt.setString(3, passwordField.getText());
            credStmt.executeUpdate();
            
            // Insert into users table
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setInt(1, userId);
            userStmt.setString(2, nameField.getText());
            userStmt.executeUpdate();
            
            conn.commit();  // Commit transaction
            return true;
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();  // Rollback on error
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showError("Registration failed: " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}