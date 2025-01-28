package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class BadgeNotificationController {
    @FXML
    private Label badgeNameLabel; // Label for the badge name
    @FXML
    private Label badgeDescriptionLabel; // Label for the badge description
    @FXML
    private ImageView badgeIcon; // ImageView for the badge icon

    // Method to set the badge details
    public void setBadgeDetails(String badgeName, String badgeDescription, String imageUrl) {
        badgeNameLabel.setText("You are now a " + badgeName); // Set the badge name with a prefix
        badgeDescriptionLabel.setText(badgeDescription); // Set the badge description
        badgeIcon.setImage(new Image(imageUrl)); // Set the badge image
    }

    // Method to handle the button action
    @FXML
    private void handleOk() {
        // Close the dialog
        Stage stage = (Stage) badgeNameLabel.getScene().getWindow();
        stage.close();
    }
}