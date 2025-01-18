package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.models.User;
import com.iskonnect.services.UserService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private UserService userService;

    @FXML
    private void initialize() {
        userService = new UserService();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
    
        if (validateInput(email, password)) {
            try {
                User user = userService.authenticate(email, password);
    
                if (user != null) {
                    System.out.println("Login successful for user: " + user.getFullName());
                    
                    UserSession.getInstance().startSession(
                        user.getUserId(),
                        user.getUserType(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                    );
    
                    System.out.println("Session started, attempting to load main interface...");
                    try {
                        Main.setMainRoot();
                        System.out.println("Main interface loaded successfully");
                    } catch (IOException e) {
                        System.out.println("Failed to load main interface: " + e.getMessage());
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                            "Could not load main interface.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", 
                        "Invalid email or password. Please try again.");
                }
            } catch (Exception e) {
                handleLoginError(e);
            }
        }
    }
    
    @FXML
    private void switchToRegister() {
        try {
            Main.setLoginRoot("auth/register");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                "Could not load registration page.");
            e.printStackTrace();
        }
    }



    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return false;
        }
        return true;
    }

    private void attemptLogin(String email, String password) {
        try {
            User user = userService.authenticate(email, password);

            if (user != null) {
                startUserSession(user);
                navigateToMainInterface();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", 
                    "Invalid email or password. Please try again.");
            }
        } catch (Exception e) {
            handleLoginError(e);
        }
    }

    private void startUserSession(User user) {
        UserSession.getInstance().startSession(
            user.getUserId(),
            user.getUserType(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
    }

    private void navigateToMainInterface() {
        try {
            Main.setMainRoot();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                "Could not load main interface.");
            e.printStackTrace();
        }
    }

    private void handleLoginError(Exception e) {
        showAlert(Alert.AlertType.ERROR, "Error", 
            "An error occurred during login. Please try again.");
        e.printStackTrace();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}