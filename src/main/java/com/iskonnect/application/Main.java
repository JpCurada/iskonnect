package com.iskonnect.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    }

    public static void setMainRoot() throws IOException {
        try {
            System.out.println("Loading main interface...");
            Scene scene = new Scene(loadFXML("student/base"), 780, 460);
            stage.setScene(scene);
            // Make main interface resizable
            stage.setResizable(true);
            // Set minimum dimensions but allow resizing
            stage.setMinWidth(780);
            stage.setMinHeight(460);
            System.out.println("Main interface loaded successfully");
        } catch (IOException e) {
            System.out.println("Error loading main interface: " + e.getMessage());
            throw e;
        }
    }

    public static void setAdminRoot() throws IOException {
        Scene scene = new Scene(loadFXML("admin/base"), 780, 460);
        stage.setScene(scene);
        // Make admin interface resizable
        stage.setResizable(true);
        // Set minimum dimensions but allow resizing
        stage.setMinWidth(780);
        stage.setMinHeight(460);
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
        // Add a shutdown hook to close the Hikari connection pool
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down connection pool...");
            DatabaseConnection.closePool();
        }));

        // Launch the JavaFX application
        launch();
    }
}