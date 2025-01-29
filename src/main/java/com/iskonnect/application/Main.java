package com.iskonnect.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.iskonnect.utils.DatabaseConnection;
import java.io.IOException;

public class Main extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setTitle("ISKOnnect");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ISKONNECT_TRANSPARENT.png")));
        setLoginRoot("auth/login");
        stage.show();
    }

    public static void setLoginRoot(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml), 780, 460);
        stage.setScene(scene);
        // Make authentication pages non-resizable
        stage.setResizable(false);
        // Set fixed dimensions
        stage.setMinWidth(780);
        stage.setMaxWidth(780);
        stage.setMinHeight(460);
        stage.setMaxHeight(460);
        stage.setFullScreen(false); // Ensure login is not fullscreen
        centerStage(); // Center the login window
    }

    private static void centerStage() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void setMainRoot() throws IOException {
        try {
            System.out.println("Loading student interface...");
            Parent root = loadFXML("student/base");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Make main interface resizable
            stage.setResizable(true);
            // Remove size restrictions
            stage.setMinWidth(780);
            stage.setMinHeight(460);
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.setMaxHeight(Double.MAX_VALUE);
            
            // Set fullscreen
            stage.setMaximized(true);
            
            System.out.println("Main interface loaded successfully");
        } catch (IOException e) {
            System.out.println("Error loading main interface: " + e.getMessage());
            throw e;
        }
    }

    public static void setAdminRoot() throws IOException {
        Parent root = loadFXML("admin/base");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // Make admin interface resizable
        stage.setResizable(true);
        // Remove size restrictions
        stage.setMinWidth(780);
        stage.setMinHeight(460);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
        
        // Set fullscreen
        stage.setMaximized(true);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String resourcePath = "/fxml/" + fxml + ".fxml";
        System.out.println("Loading FXML: " + resourcePath);
        return new FXMLLoader(Main.class.getResource(resourcePath)).load();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down connection pool...");
            DatabaseConnection.closePool();
        }));
        launch();
    }

    // Add method to toggle fullscreen
    public static void toggleFullScreen() {
        stage.setFullScreen(!stage.isFullScreen());
    }
}