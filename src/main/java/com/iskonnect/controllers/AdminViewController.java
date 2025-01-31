// Path: src/main/java/com/iskonnect/controllers/AdminViewController.java
package com.iskonnect.controllers;

import javafx.fxml.FXML;

public class AdminViewController extends BaseViewController {
    
    @FXML
    @Override
    public void initialize() {
        super.initialize();
        loadPage("admin", "dashboard");
    }

    @FXML
    public void handleDashboard() {
        loadPage("admin", "dashboard");
    }

    @FXML
    public void handleUsers() {
        loadPage("admin", "users");
    }

    @FXML
    public void handleReports() {
        loadPage("admin", "reports");
    }
}