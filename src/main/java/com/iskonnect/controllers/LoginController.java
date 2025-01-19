// Path: src/main/java/com/iskonnect/controllers/LoginController.java

package com.iskonnect.controllers;

import com.iskonnect.application.Main;
import com.iskonnect.models.User;
import com.iskonnect.services.UserService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    private void navigateToStudentInterface() {
        try {
            System.out.println("Loading student interface...");
            Main.setMainRoot();
        } catch (IOException e) {
            System.out.println("Failed to load student interface: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                "Could not load student interface.");
        }
    }

    private void navigateToAdminInterface() {
        try {
            System.out.println("Loading admin interface...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/base.fxml"));
            Scene scene = new Scene(loader.load(), 780, 460);
            Main.getStage().setScene(scene);
        } catch (IOException e) {
            System.out.println("Failed to load admin interface: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                "Could not load admin interface.");
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