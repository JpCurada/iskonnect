package com.iskonnect.controllers;

import com.iskonnect.models.User;
import com.iskonnect.services.UserService;
import com.iskonnect.utils.UserSession;
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
            // Start user session
            UserSession.getInstance().startSession(
                user.getUserId(),
                user.getUserType(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
            );

            if (user.getUserType().equals("ADMIN")) {
                switchToAdminDashboard();
            } else {
                switchToStudentDashboard();
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
            Main.setMainRoot("student/base_layout");
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