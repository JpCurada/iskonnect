package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class BadgeNotificationController {
    @FXML
    private Label badgeNameLabel;
    @FXML
    private Label badgeDescriptionLabel;
    @FXML
    private ImageView badgeIcon; // Add this field

    public void setBadgeDetails(String badgeName, String badgeDescription, String imageUrl) {
        badgeNameLabel.setText(badgeName);
        badgeDescriptionLabel.setText(badgeDescription);
        badgeIcon.setImage(new Image(imageUrl)); // Set the badge image
    }

    @FXML
    private void handleOk() {
        // Close the dialog
        Stage stage = (Stage) badgeNameLabel.getScene().getWindow();
        stage.close();
    }
}