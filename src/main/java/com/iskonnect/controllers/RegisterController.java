package com.iskonnect.controllers;

import com.iskonnect.models.Student;
import com.iskonnect.services.UserService;
import com.iskonnect.application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField studentNumberField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        if (validateInputs()) {
            Student newStudent = new Student(
                studentNumberField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText()
            );

            if (userService.registerUser(newStudent, passwordField.getText())) {
                showSuccess("Registration successful!");
                switchToLogin();
            } else {
                showError("Registration failed");
            }
        }
    }

    private boolean validateInputs() {
        // Check for empty fields
        if (studentNumberField.getText().isEmpty() || 
            firstNameField.getText().isEmpty() ||
            lastNameField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty()) {
            showError("All fields are required");
            return false;
        }

        // Validate password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }

        // Validate student number format (2024-00000-MN-0)
        if (!studentNumberField.getText().matches("\\d{4}-\\d{5}-[A-Z]{2}-\\d")) {
            showError("Invalid student number format. Must be like: 2024-00000-MN-0");
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return false;
        }

        return true;
    }

    @FXML
    private void switchToLogin() {
        try {
            Main.setLoginRoot("auth/login");
        } catch (Exception e) {
            showError("Could not return to login page");
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