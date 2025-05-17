package transport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent racine = FXMLLoader.load(getClass().getResource("/transport/ui/menu.fxml"));
        primaryStage.setTitle("ESI-RUN - Menu Principal");
        primaryStage.setScene(new Scene(racine));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
