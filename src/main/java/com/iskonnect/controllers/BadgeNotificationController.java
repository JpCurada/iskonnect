package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BadgeNotificationController {
    @FXML
    private Label badgeNameLabel;
    @FXML
    private Label badgeDescriptionLabel;

    public void setBadgeDetails(String badgeName, String badgeDescription) {
        badgeNameLabel.setText(badgeName);
        badgeDescriptionLabel.setText(badgeDescription);
    }

    @FXML
    private void handleOk() {
        // Close the dialog
        Stage stage = (Stage) badgeNameLabel.getScene().getWindow();
        stage.close();
    }
}