// Path: src/main/java/com/iskonnect/controllers/CollectionsController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Badge;
import com.iskonnect.models.Material;
import com.iskonnect.services.BadgeService;
import com.iskonnect.services.BookmarkService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class CollectionsController {
    @FXML private GridPane materialsGrid;
    @FXML private GridPane badgesGrid; // Changed from FlowPane to GridPane
    @FXML private Text totalMaterialsText;
    
    private BookmarkService bookmarkService;
    private BadgeService badgeService;

    @FXML
    public void initialize() {
        bookmarkService = new BookmarkService();
        badgeService = new BadgeService();
        loadBookmarkedMaterials();
        loadUserBadges();
    }

    private void loadUserBadges() {
        String userId = UserSession.getInstance().getUserId();
        List<Badge> badges = badgeService.getUserBadges(userId);
        
        badgesGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Badge badge : badges) {
            VBox badgeCard = createBadgeCard(badge);
            badgesGrid.add(badgeCard, column, row);
            
            column++;
            if (column == 4) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createBadgeCard(Badge badge) {
        VBox badgeCard = new VBox(10);
        badgeCard.getStyleClass().add("badge-card");
        badgeCard.setPrefWidth(200);
        badgeCard.setAlignment(Pos.CENTER);

        // Badge Image with white circular background
        VBox imageContainer = new VBox();
        imageContainer.setStyle("-fx-background-color: #FEF9E1; -fx-background-radius: 50%; -fx-padding: 10;");
        imageContainer.setAlignment(Pos.CENTER);

        ImageView badgeImage = new ImageView(new Image(badge.getImageUrl()));
        badgeImage.setFitHeight(128);
        badgeImage.setFitWidth(128);
        badgeImage.setPreserveRatio(true);
        imageContainer.getChildren().add(badgeImage);

        // Badge Name
        Label badgeName = new Label(badge.getName());
        badgeName.getStyleClass().add("badge-name");
        badgeName.setWrapText(true);
        badgeName.setAlignment(Pos.CENTER);
        badgeName.setStyle("-fx-text-fill:rgb(134, 35, 35); -fx-font-size: 14;");

        // Badge Description
        Label badgeDescription = new Label(badge.getDescription());
        badgeDescription.setWrapText(true);
        badgeDescription.setAlignment(Pos.CENTER);
        badgeDescription.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");

        badgeCard.getChildren().addAll(imageContainer, badgeName, badgeDescription);
        return badgeCard;
    }

    private void loadBookmarkedMaterials() {
        String userId = UserSession.getInstance().getUserId();
        List<Material> materials = bookmarkService.getBookmarkedMaterials(userId);
        
        materialsGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Material material : materials) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/MaterialCard.fxml"));
                VBox card = loader.load();
                MaterialCardController controller = loader.getController();
                controller.setMaterial(material);
                
                materialsGrid.add(card, column, row);
                
                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        totalMaterialsText.setText(String.valueOf(materials.size()));
    }

    @FXML
    private void handleRefresh() {
        loadBookmarkedMaterials();
        loadUserBadges();
    }
}