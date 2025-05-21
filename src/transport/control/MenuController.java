package transport.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuController {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @FXML
    public void gererTicketAjouter(ActionEvent event) throws IOException {
        chargerScene(event, "/transport/ui/ticketajouter.fxml");
    }

    @FXML
    public void gererTicketAfficher(ActionEvent event) throws IOException {
        chargerScene(event, "/transport/ui/ticketafficher.fxml");
    }

    @FXML
    public void gererTicketValider(ActionEvent event) throws IOException {
        chargerScene(event, "/transport/ui/ticketvalider.fxml");
    }

    @FXML
    public void gererReclamation(ActionEvent event) throws IOException {
        chargerScene(event, "/transport/ui/reclamation.fxml");
    }

    @FXML
    public void gererReclamationAfficher(ActionEvent event) throws IOException {
        chargerScene(event, "/transport/ui/reclamationafficher.fxml");
    }

    @FXML
    private void gererUtilisateurs(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        chargerScene(stage, "/transport/ui/utilisateur.fxml");
    }

    private void chargerScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    public static void chargerScene(Stage stage, String fxmlPath) {
        try {
            if (stage == null) {
                throw new IllegalStateException("Stage is null when trying to load " + fxmlPath);
            }

            URL url = MenuController.class.getResource(fxmlPath);
            if (url == null) {
                throw new IllegalStateException("Cannot find FXML file: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ": " + e.getMessage());
        }
    }
}
