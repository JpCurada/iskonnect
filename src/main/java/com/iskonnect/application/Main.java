package com.iskonnect.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setTitle("ISKOnnect");
        
        setLoginRoot("auth/login");
        stage.show();
    }

    public static void setLoginRoot(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml), 780, 460);
        stage.setScene(scene);
    }

    public static void setMainRoot(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml), 921, 575);
        stage.setScene(scene);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}