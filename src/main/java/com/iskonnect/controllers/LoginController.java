// Path: src/main/java/com/iskonnect/controllers/LoginController.java

package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.models.User;
import com.iskonnect.services.UserService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
                    
                    // Start user session
                    UserSession.getInstance().startSession(
                        user.getUserId(),
                        user.getUserType(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                    );
    
                    // Route based on user type
                    if (user.getUserType().equals("ADMIN")) {
                        navigateToAdminInterface();
                    } else {
                        navigateToStudentInterface();
                    }
                } else {
                    showError("Login Failed", 
                        "Invalid email or password.\n\n" +
                        "Please check your credentials and try again.");
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
        } catch (Exception e) {
            showError("Navigation Error", "Could not load registration page.");
            e.printStackTrace();
        }
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            showError("Validation Error", "Please enter both email and password.");
            return false;
        }
        
        if (email.isEmpty()) {
            showError("Validation Error", "Please enter your email.");
            return false;
        }
        
        if (password.isEmpty()) {
            showError("Validation Error", "Please enter your password.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid Format", "Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private void navigateToStudentInterface() {
        try {
            System.out.println("Loading student interface...");
            Main.setMainRoot();
        } catch (Exception e) {
            System.out.println("Failed to load student interface: " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Could not load student interface.");
        }
    }

    private void navigateToAdminInterface() {
        try {
            System.out.println("Loading admin interface...");
            Main.setAdminRoot();
        } catch (Exception e) {
            System.out.println("Failed to load admin interface: " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Could not load admin interface.");
        }
    }

    private void handleLoginError(Exception e) {
        showError("System Error", 
            "An unexpected error occurred during login.\n" +
            "Please try again later.");
        e.printStackTrace();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }
}