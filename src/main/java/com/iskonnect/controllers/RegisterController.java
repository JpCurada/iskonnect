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
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }

        if (!studentNumberField.getText().matches("\\d{4}-\\d{5}-[A-Z]{2}-\\d")) {
            showError("Invalid student number format");
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