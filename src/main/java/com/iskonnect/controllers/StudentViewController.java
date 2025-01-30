// src/main/java/com/iskonnect/controllers/StudentViewController.java
package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.iskonnect.utils.UserSession;
import com.iskonnect.application.Main;
import java.io.IOException;
import java.util.List;

public class StudentViewController {
    @FXML private Text fullNameText;
    @FXML private Text studentNumberText;
    @FXML private VBox contentArea;
    @FXML private Button homeButton;
    @FXML private Button contributeButton;
    @FXML private Button leaderboardButton;
    @FXML private Button collectionsButton;
    private Button selectedButton;

    @FXML
    public void initialize() {
        initializeUserInfo();
        loadPage("home"); // Load default page

        List<Button> menuButtons = List.of(
                homeButton, contributeButton, leaderboardButton, collectionsButton
        );

        // Set event handlers for each button
        for (Button button : menuButtons) {
            button.setOnAction(event -> {
                setSelectedButton(button);
                loadPage(getPageFromButton(button));
            });
        }

        // Set the default selected button to "homeButton"
        setSelectedButton(homeButton);
    }

    private void initializeUserInfo() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            fullNameText.setText(session.getFullName());
            studentNumberText.setText(String.valueOf(session.getUserId()));
        }
    }

    private void setSelectedButton(javafx.scene.control.Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected"); // Remove highlight from previous button
        }
        selectedButton = button;
        selectedButton.getStyleClass().add("selected"); // Add highlight to new button
    }

    private String getPageFromButton(javafx.scene.control.Button button) {
        if (button == homeButton) return "home";
        if (button == contributeButton) return "contribute";
        if (button == leaderboardButton) return "leaderboard";
        if (button == collectionsButton) return "collections";
        return "home"; // Default page if no match
    }

    @FXML
    private void handleHome() {
        loadPage("home");
    }

    @FXML
    private void handleContribute() {
        loadPage("contribute");
    }

    @FXML
    private void handleLeaderboard() {
        loadPage("leaderboard");
    }

    @FXML
    private void handleContributions() {
        loadPage("contributions");
    }

    @FXML
    private void handleCollections() {
        loadPage("collections");
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().endSession();
        try {
            Main.setLoginRoot("auth/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/" + page + ".fxml"));
            VBox pageContent = loader.load();
            contentArea.getChildren().setAll(pageContent);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + page + ": " + e.getMessage());
        }
    }
}