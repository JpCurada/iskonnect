// Path: src/main/java/com/iskonnect/controllers/CollectionsController.java

package com.iskonnect.controllers;

import com.iskonnect.models.Material;
import com.iskonnect.services.BookmarkService;
import com.iskonnect.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.List;

public class CollectionsController {
    @FXML private GridPane materialsGrid;
    @FXML private Text totalMaterialsText;
    private BookmarkService bookmarkService;

    @FXML
    public void initialize() {
        bookmarkService = new BookmarkService();
        loadBookmarkedMaterials();
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
    }
}