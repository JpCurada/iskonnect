package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.MaterialService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeController {
    @FXML private Text dateText;
    @FXML private Text firstNameText;
    @FXML private TextField searchField;
    @FXML private GridPane materialsGrid;
    @FXML private Text welcomeText;
    @FXML private Text greetingText;
    @FXML private Text questionMark;
    @FXML private Button nextPageButton;
    @FXML private Button previousPageButton;

    private MaterialService materialService;
    private List<Material> materials;

    private int currentPage = 1;
    private int itemsPerPage = 8; // Number of items to display per page
    private int totalPages;

    @FXML
    public void initialize() {
        materialService = new MaterialService();

        // Load font
        try {
            InputStream regularFont = getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf");
            InputStream lightFont = getClass().getResourceAsStream("/fonts/Poppins-Light.ttf");
            InputStream blackFont = getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf");

            if (regularFont == null || lightFont == null || blackFont == null) {
                throw new IOException("One or more font files could not be found.");
            }

            Font.loadFont(regularFont, 10);
            Font.loadFont(lightFont, 10);
            Font.loadFont(blackFont, 10);

        } catch (Exception e) {
            System.err.println("Could not load font: " + e.getMessage());
            e.printStackTrace();
        }

        // Set current date
        dateText.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));

        // Set user's first name
        firstNameText.setText(UserSession.getInstance().getFirstName());

        // Load materials
        loadMaterials();

        // Add enter key handler for search
        searchField.setOnAction(e -> handleSearch());
    }

    private void loadMaterials() {
        try {
            materials = materialService.getAllMaterials();
            totalPages = (int) Math.ceil((double) materials.size() / itemsPerPage);
            displayMaterials(getMaterialsForCurrentPage());
        } catch (Exception e) {
            showError("Failed to load materials");
            e.printStackTrace();
        }
        updatePaginationControls();
    }

    private List<Material> getMaterialsForCurrentPage() {
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, materials.size());
        return materials.subList(start, end);
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

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadMaterials();
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadMaterials();
        }
    }

    private void updatePaginationControls() {
        previousPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(currentPage == totalPages);
    }

    private void filterMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayMaterials(getMaterialsForCurrentPage());
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