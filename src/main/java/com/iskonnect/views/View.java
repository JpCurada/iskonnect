// View.java (Base class)
package com.iskonnect.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public abstract class View {
    protected Parent root;
    
    protected void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Parent getRoot() {
        return root;
    }
}