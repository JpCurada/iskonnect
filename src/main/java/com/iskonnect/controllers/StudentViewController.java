// src/main/java/com/iskonnect/controllers/StudentViewController.java
package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.iskonnect.utils.UserSession;
import com.iskonnect.application.Main;
import java.io.IOException;

public class StudentViewController {
    @FXML private Text fullNameText;
    @FXML private Text studentNumberText;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        initializeUserInfo();
        loadPage("home"); // Load default page
    }

    private void initializeUserInfo() {
        UserSession session = UserSession.getInstance();
        if (session != null) {
            fullNameText.setText(session.getFullName());
            studentNumberText.setText(String.valueOf(session.getUserId()));
        }
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