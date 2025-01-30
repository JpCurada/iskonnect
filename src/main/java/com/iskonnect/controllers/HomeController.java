package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.MaterialService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private HBox pageNumberContainer;

    private MaterialService materialService;
    private List<Material> materials;

    private int currentPage = 1;
    private int itemsPerPage = 9; // Number of items to display per page
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

        // Set up pagination button actions
        previousPageButton.setOnAction(e -> handlePreviousPage());
        nextPageButton.setOnAction(e -> handleNextPage());
    }

    private void loadMaterials() {
        loadingIndicator.setVisible(true); // Show loading indicator
        try {
            materials = materialService.getMaterials(currentPage, itemsPerPage); // Fetch materials with pagination
            totalPages = (int) Math.ceil((double) materialService.getTotalMaterialsCount() / itemsPerPage); // Calculate total pages
            displayMaterials(materials); // Display materials for the current page
        } catch (Exception e) {
            showError("Failed to load materials");
            e.printStackTrace();
        } finally {
            loadingIndicator.setVisible(false); // Hide loading indicator
            updatePaginationControls(); // Update pagination controls
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
            if (column == 3) { // Assuming 3 columns
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
            loadMaterials(); // Fetch materials for the next page
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadMaterials(); // Fetch materials for the previous page
        }
    }

    private void updatePaginationControls() {
        previousPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(currentPage >= totalPages);
        pageNumberContainer.getChildren().clear(); // Clear existing page number buttons
    
        // Create buttons for each page number
        for (int i = 1; i <= totalPages; i++) {
            final int pageNumber = i; // Create a final variable to hold the page number
            Button pageButton = new Button(String.valueOf(pageNumber));
            pageButton.setOnAction(e -> {
                currentPage = pageNumber; // Set the current page to the clicked page
                loadMaterials(); // Load materials for the selected page
            });
            pageNumberContainer.getChildren().add(pageButton); // Add the button to the container
        }
    }

    private void filterMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadMaterials(); // Reload original materials if search is empty
            return;
        }

        List<Material> filtered = materials.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getCollege().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getCourse().toLowerCase().contains(searchText.toLowerCase()) ||
                        m.getSubject().toLowerCase().contains(searchText.toLowerCase()))
            .toList();
        
        materials = filtered; // Update materials to filtered list
        totalPages = (int) Math.ceil((double) materials.size() / itemsPerPage); // Recalculate total pages
        currentPage = 1; // Reset to first page
        displayMaterials(materials); // Display filtered materials
        updatePaginationControls(); // Update pagination controls
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        filterMaterials(searchText);
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadMaterials(); // Reload original materials
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}