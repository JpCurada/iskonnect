// Path: src/main/java/com/iskonnect/controllers/HomeController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Vote;
import com.iskonnect.models.Material;
import com.iskonnect.services.MaterialService;
import com.iskonnect.services.VoteService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;



public class HomeController {
    @FXML private Text dateText;
    @FXML private Text firstNameText;
    @FXML private TextField searchField;
    @FXML private GridPane materialsGrid;

    private MaterialService materialService;
    private VoteService voteService;
    private List<Material> materials;
    private Map<Integer, Boolean> userVotes = new HashMap<>();

    @FXML
    public void initialize() {
        materialService = new MaterialService();
        voteService = new VoteService();
        
        // Set current date
        dateText.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        
        // Set user's first name
        firstNameText.setText(UserSession.getInstance().getFirstName());
        
        // Load materials
        loadMaterials();
        
        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMaterials(newValue);
        });
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
        VBox card = new VBox(10);
        card.getStyleClass().add("material-card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_LEFT);

        // Preview Image Placeholder
        ImageView preview = new ImageView(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        preview.setFitWidth(200);
        preview.setFitHeight(120);
        preview.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        // Tags
        HBox tags = new HBox(5);
        Label collegeTag = createTag(material.getCollege());
        Label courseTag = createTag(material.getCourse());
        tags.getChildren().addAll(collegeTag, courseTag);

        // Material Name
        Label name = new Label(material.getTitle());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        name.setWrapText(true);

        // Contributor
        Label contributor = new Label("By " + material.getUploaderId());
        contributor.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");

        // Upvote Section
        HBox voteBox = createVoteBox(material);

        // Add click handler to open material
        card.setOnMouseClicked(e -> openMaterial(material.getFileUrl()));

        card.getChildren().addAll(preview, tags, name, contributor, voteBox);
        return card;
    }

    private Label createTag(String text) {
        Label tag = new Label(text);
        tag.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 11;");
        return tag;
    }

    private HBox createVoteBox(Material material) {
        HBox voteBox = new HBox(5);
        voteBox.setAlignment(Pos.CENTER_LEFT);

        Button upvoteBtn = new Button("↑");
        upvoteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #A31D1D; -fx-font-weight: bold;");
        
        Label voteCount = new Label(String.valueOf(voteService.getVoteCount(material.getMaterialId())));
        voteCount.setStyle("-fx-font-weight: bold;");

        upvoteBtn.setOnAction(e -> {
            e.consume(); // Prevent card click
            handleVote(material, voteCount);
        });

        voteBox.getChildren().addAll(upvoteBtn, voteCount);
        return voteBox;
    }

    private void handleVote(Material material, Label voteCount) {
        int materialId = material.getMaterialId();
        boolean hasVoted = userVotes.getOrDefault(materialId, false);
        
        String voteType = hasVoted ? "DOWNVOTE" : "UPVOTE";
        if (voteService.addVote(new Vote(materialId, UserSession.getInstance().getUserId(), voteType))) {
            int newCount = Integer.parseInt(voteCount.getText()) + (hasVoted ? -1 : 1);
            voteCount.setText(String.valueOf(newCount));
            userVotes.put(materialId, !hasVoted);
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
                        m.getCourse().toLowerCase().contains(searchText.toLowerCase()))
            .toList();
        
        displayMaterials(filtered);
    }

    @FXML
    private void handleSearch() {
        filterMaterials(searchField.getText());
    }

    private void openMaterial(String fileUrl) {
        try {
            Desktop.getDesktop().browse(new URI(fileUrl));
        } catch (Exception e) {
            showError("Failed to open material");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}