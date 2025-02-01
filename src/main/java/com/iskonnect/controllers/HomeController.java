package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.MaterialService;
import com.iskonnect.services.VoteService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import java.io.InputStream;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController {
    @FXML private Text dateText;
    @FXML private Text firstNameText;
    @FXML private TextField searchField;
    @FXML private GridPane materialsGrid;
    @FXML private Text welcomeText;
    @FXML private Text greetingText;
    @FXML private Text questionMark;
    @FXML private HBox paginationContainer;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Text pageText;

    private MaterialService materialService;
    private VoteService voteService;
    private List<Material> allMaterials;
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int ITEMS_PER_PAGE = 12; // 4 columns * 3 rows

    @FXML
    public void initialize() {
        materialService = new MaterialService();
        voteService = new VoteService();

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

        // Make text responsive
        dateText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                makeAllTextResponsive(newScene);
            }
        });

        prevButton.setText("<");
        nextButton.setText(">");

        // Initialize pagination buttons
        prevButton.setOnAction(e -> handlePrevPage());
        nextButton.setOnAction(e -> handleNextPage());

        // Load materials
        loadMaterials();

        // Add enter key handler for search
        searchField.setOnAction(e -> handleSearch());
    }

    private void makeAllTextResponsive(Scene scene) {
        // Initial setup
        updateTextSizes(scene.getWidth());

        // Listen for changes
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            updateTextSizes(newWidth.doubleValue());
        });
    }

    private void updateTextSizes(double width) {
        double scalingFactor = width / 1000.0; //base width for scaling

        //store styles in variables to maintain consistency
        welcomeText.setStyle(String.format("-fx-font-size: %.2fpx; -fx-font-family: 'Poppins Bold'",
                Math.min(Math.max(45 * scalingFactor, 20), 45)));

        greetingText.setStyle(String.format("-fx-font-size: %.2fpx; -fx-font-family: 'Poppins Regular'",
                Math.min(Math.max(20 * scalingFactor, 12), 20)));

        firstNameText.setStyle(String.format("-fx-font-size: %.2fpx; -fx-font-family: 'Poppins Regular' ",
                Math.min(Math.max(20 * scalingFactor, 12), 20)));

        questionMark.setStyle(String.format("-fx-font-size: %.2fpx; -fx-font-family: 'Poppins Regular'",
                Math.min(Math.max(20 * scalingFactor, 12), 20)));

        dateText.setStyle(String.format("-fx-font-size: %.2fpx;  -fx-font-family: 'Poppins Light'",
                Math.min(Math.max(16 * scalingFactor, 12), 16)));
    }

    private void loadMaterials() {
        try {
            // Get total count first for pagination
            int totalItems = materialService.getTotalMaterialsCount();
            totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            updatePagination();

            // Load first page
            loadPage(1);
        } catch (Exception e) {
            showError("Failed to load materials");
            e.printStackTrace();
        }
    }

    private void loadPage(int page) {
        List<Material> pageItems = materialService.getAllMaterials(page, ITEMS_PER_PAGE);
        displayMaterials(pageItems);
    }

    private void displayMaterials(List<Material> materialsToDisplay) {
        materialsGrid.getChildren().clear(); // Clear existing materials

        int column = 0;
        int row = 0;

        for (Material material : materialsToDisplay) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/MaterialCard.fxml"));
                VBox card = loader.load();
                MaterialCardController cardController = loader.getController();
                cardController.setMaterial(material);

                // Enable caching for each card
                card.setCache(true);
                card.setCacheHint(CacheHint.SPEED);

                GridPane.setConstraints(card, column, row);
                materialsGrid.getChildren().add(card);

                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
                showError("Failed to create material card");
            }
        }
    }

    private void filterMaterials(String searchText) {
        try {
            materialsGrid.getChildren().clear();

            if (searchText == null || searchText.trim().isEmpty()) {
                currentPage = 1;
                loadMaterials();
                return;
            }

            // Get total count of search results
            int totalItems = materialService.getSearchResultsCount(searchText);
            totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            updatePagination();

            // Load first page of search results
            List<Material> searchResults = materialService.searchMaterials(searchText, currentPage, ITEMS_PER_PAGE);
            displayMaterials(searchResults);

        } catch (Exception e) {
            showError("Error performing search");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        filterMaterials(searchText);
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
            loadPage(currentPage);
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
            loadPage(currentPage);
        }
    }

    private void updatePagination() {
        pageText.setText(String.format("Page %d of %d", currentPage, totalPages));
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
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