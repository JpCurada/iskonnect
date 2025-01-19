import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the base layout from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("stage.fxml"));
            BorderPane root = loader.load();

            // Create the scene with the base layout
            Scene scene = new Scene(root);

            // Set up the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("My Application");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
