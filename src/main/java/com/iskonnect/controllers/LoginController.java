package com.iskonnect.controllers;

import com.iskonnect.models.User;
import com.iskonnect.models.Student;
import com.iskonnect.services.UserService;
import com.iskonnect.application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = userService.authenticate(email, password);
        if (user != null) {
            if (user instanceof Student) {
                switchToStudentDashboard();
            } else {
                switchToAdminDashboard();
            }
        } else {
            showError("Invalid credentials");
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            Main.setLoginRoot("auth/register");
        } catch (Exception e) {
            showError("Could not load registration page");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchToStudentDashboard() {
        try {
            Main.setMainRoot("base_layout");
        } catch (Exception e) {
            showError("Could not load dashboard");
        }
    }

    private void switchToAdminDashboard() {
        try {
            Main.setMainRoot("admin/dashboard");
        } catch (Exception e) {
            showError("Could not load dashboard");
        }
    }
}