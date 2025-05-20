package transport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent racine = FXMLLoader.load(getClass().getResource("/transport/ui/menu.fxml"));
        primaryStage.setTitle("ESI-RUN - Menu Principal");
        Scene scene = new Scene(racine, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); 
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
