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
    @FXML private Text totalMaterialsText;
    @FXML private FlowPane badgesPane;
    
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
        
        badgesPane.getChildren().clear();
        
        for (Badge badge : badges) {
            VBox badgeCard = createBadgeCard(badge);
            badgesPane.getChildren().add(badgeCard);
        }
    }

    private VBox createBadgeCard(Badge badge) {
        VBox badgeCard = new VBox(10);
        badgeCard.getStyleClass().add("badge-card");
        badgeCard.setPrefWidth(200);
        badgeCard.setAlignment(Pos.CENTER);

        // Badge Image
        ImageView badgeImage = new ImageView(new Image(badge.getImageUrl()));
        badgeImage.setFitHeight(64);
        badgeImage.setFitWidth(64);
        badgeImage.setPreserveRatio(true);

        // Badge Name
        Label badgeName = new Label(badge.getName());
        badgeName.getStyleClass().add("badge-name");

        // Badge Description
        Label badgeDescription = new Label(badge.getDescription());
        badgeDescription.setWrapText(true);
        badgeDescription.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");

        badgeCard.getChildren().addAll(badgeImage, badgeName, badgeDescription);
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