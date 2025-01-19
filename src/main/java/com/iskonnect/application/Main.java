package com.iskonnect.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
    }

    public static void setMainRoot() throws IOException {
        try {
            System.out.println("Loading main interface...");
            Scene scene = new Scene(loadFXML("student/base"), 780, 460);
            stage.setScene(scene);
            System.out.println("Main interface loaded successfully");
        } catch (IOException e) {
            System.out.println("Error loading main interface: " + e.getMessage());
            throw e;
        }
    }

    public static void setAdminRoot() throws IOException {
        Scene scene = new Scene(loadFXML("admin/base"), 780, 460);
        stage.setScene(scene);
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
        launch();
    }
}