// Path: src/main/java/com/iskonnect/controllers/StudentViewController.java
package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.util.List;

public class StudentViewController extends BaseViewController {
    @FXML private Button homeButton;
    @FXML private Button contributeButton;
    @FXML private Button leaderboardButton;
    @FXML private Button collectionsButton;
    private Button selectedButton;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        setSelectedButton(homeButton);
        loadPage("student", "home");
    }

    @FXML
    public void handleHome() {
        setSelectedButton(homeButton);
        loadPage("student", "home");
    }

    @FXML
    public void handleContribute() {
        setSelectedButton(contributeButton);
        loadPage("student", "contribute");
    }

    @FXML
    public void handleLeaderboard() {
        setSelectedButton(leaderboardButton);
        loadPage("student", "leaderboard");
    }

    @FXML
    public void handleCollections() {
        setSelectedButton(collectionsButton);
        loadPage("student", "collections");
    }

    private void setSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
        }
        selectedButton = button;
        selectedButton.getStyleClass().add("selected");
    }
}