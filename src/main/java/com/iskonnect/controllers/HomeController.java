// Path: src/main/java/com/iskonnect/controllers/HomeController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Vote;
import com.iskonnect.models.Material;
import com.iskonnect.services.MaterialService;
import com.iskonnect.services.VoteService;
import com.iskonnect.utils.UserSession;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomeController {
    @FXML private Text dateText;
    @FXML private Text firstNameText;
    @FXML private TextField searchField;
    @FXML private GridPane materialsGrid;
    @FXML private Text welcomeText;
    @FXML private Text greetingText;
    @FXML private Text questionMark;

    private MaterialService materialService;
    private VoteService voteService;
    private List<Material> materials;

    @FXML
    public void initialize() {
        materialService = new MaterialService();
        voteService = new VoteService();

        // Set current date
        dateText.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));

        // Set user's first name
        firstNameText.setText(UserSession.getInstance().getFirstName());

        //make text responsive
        dateText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeResponsive(dateText, newScene, 50, null, 14, 18);
            }
        });
        //hello isko
        welcomeText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeResponsive(welcomeText, newScene, 30, "-fx-font-weight: bold;", 20, 28);
            }
        });
        //kamusta
        greetingText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeResponsive(greetingText, newScene, 50, null, 12, 16);
            }
        });
        //?
        greetingText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeResponsive(questionMark, newScene, 50, null, 12, 16);
            }
        });
        //fname
        firstNameText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeResponsive(firstNameText, newScene, 50, "-fx-font-weight: bold;", 12, 16);
            }
        });

        // Load materials
        loadMaterials();

        // Add enter key handler for search
        searchField.setOnAction(e -> handleSearch());
    }

    private void makeResponsive(javafx.scene.Node node, Scene scene, double divisor, String additionalStyle, double minFontSize, double maxFontSize) {
        node.styleProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            double baseSize = scene.getWidth() / divisor;
                            double size = Math.min(Math.max(baseSize, minFontSize), maxFontSize);
                            String style = String.format("-fx-font-size: %.2fpx;", size);
                            if (additionalStyle != null) {
                                style += additionalStyle;
                            }
                            return style;
                        },
                        scene.widthProperty()
                )
        );
    }

    private void loadMaterials() {
        try {
            materials = materialService.getAllMaterials();
            displayMaterials(materials);
        } catch (Exception e) {
            showError("Failed to load materials");
            e.printStackTrace();
        }
    }

    private void displayMaterials(List<Material> materialsToDisplay) {
        materialsGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Material material : materialsToDisplay) {
            VBox card = createMaterialCard(material);
            materialsGrid.add(card, column, row);
            
            column++;
            if (column == 4) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createMaterialCard(Material material) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/MaterialCard.fxml"));
            VBox card = loader.load();
            MaterialCardController cardController = loader.getController();
            cardController.setMaterial(material);
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to create material card");
            return new VBox();
        }
    }

    private void filterMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayMaterials(materials);
            return;
        }

        List<Material> filtered = materials.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getCollege().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getCourse().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getSubject().toLowerCase().contains(searchText.toLowerCase()))
            .toList();
        
        displayMaterials(filtered);
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        filterMaterials(searchText);
    }

    @FXML
    private void handleRefresh() {
        // Clear search field
        searchField.clear();
        // Reload all materials
        loadMaterials();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}