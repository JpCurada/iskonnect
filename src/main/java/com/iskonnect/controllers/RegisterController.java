// Path: src/main/java/com/iskonnect/controllers/RegisterController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Student;
import com.iskonnect.services.UserService;
import com.iskonnect.application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.regex.Pattern;

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
        if (validateAllInputs()) {
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
                showError("Registration failed", "There was an error during registration. Please try again.");
            }
        }
    }

    private boolean validateAllInputs() {
        // Check for empty fields
        if (studentNumberField.getText().isEmpty() || 
            firstNameField.getText().isEmpty() ||
            lastNameField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty()) {
            showError("Validation Error", "All fields are required");
            return false;
        }

        // Validate student number format (2024-00000-MN-0)
        if (!studentNumberField.getText().matches("\\d{4}-\\d{5}-[A-Z]{2}-\\d")) {
            showError("Invalid Format", "Student number must be in format: 2024-00000-MN-0");
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid Format", "Please enter a valid email address");
            return false;
        }

        // Validate password
        String password = passwordField.getText();
        if (!validatePassword(password)) {
            showError("Invalid Password", 
                "Password must contain:\n" +
                "- At least 8 characters\n" +
                "- At least one uppercase letter\n" +
                "- At least one lowercase letter\n" +
                "- At least one number\n" +
                "- At least one special character (!@#$%^&*(),.?\":{}|<>)"
            );
            return false;
        }

        // Validate password match
        if (!password.equals(confirmPasswordField.getText())) {
            showError("Password Mismatch", "Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        // Check minimum length
        if (password.length() < 8) return false;

        // Check for uppercase
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;

        // Check for lowercase
        if (!Pattern.compile("[a-z]").matcher(password).find()) return false;

        // Check for numbers
        if (!Pattern.compile("\\d").matcher(password).find()) return false;

        // Check for special characters
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) return false;

        return true;
    }

    @FXML
    private void switchToLogin() {
        try {
            Main.setLoginRoot("auth/login");
        } catch (Exception e) {
            showError("Navigation Error", "Could not return to login page");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }
}