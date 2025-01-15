package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import com.iskonnect.services.MaterialService;
import java.util.List;
import com.iskonnect.models.Material;

public class HomeController {
    @FXML private TextField searchField;
    @FXML private GridPane materialsGrid;
    
    private MaterialService materialService = new MaterialService();

    @FXML
    private void initialize() {
        loadMaterials();
    }

    private void loadMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        // TODO: Add materials to grid
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        List<Material> searchResults = materialService.searchMaterials(query);
        // TODO: Update grid with search results
    }
}