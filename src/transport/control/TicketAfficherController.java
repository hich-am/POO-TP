package transport.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import transport.core.TitreTransport;

public class TicketAfficherController {
    @FXML private ListView<String> listeTickets;

    private final String FICHIER_DATA = System.getProperty("user.dir") + File.separator + "fare_media.dat";

    @FXML
    public void initialize() {
        try {
            File fichier = new File(FICHIER_DATA);
            if (!fichier.exists()) {
                listeTickets.getItems().add("Aucun titre n'a été enregistré.");
                return;
            }

            List<TitreTransport> titres = chargerTitres();
            if (titres.isEmpty()) {
                listeTickets.getItems().add("Aucun titre vendu.");
                return;
            }

            Collections.reverse(titres);
            ObservableList<String> listeAffichage = FXCollections.observableArrayList();
            for (TitreTransport t : titres) {
                listeAffichage.add(t.toString());
            }
            listeTickets.setItems(listeAffichage);
        } catch (Exception e) {
            listeTickets.getItems().add("Erreur lors du chargement : " + e.getMessage());
        }
    }

    private List<TitreTransport> chargerTitres() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHIER_DATA))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return (List<TitreTransport>) obj;
            }
            return new ArrayList<>();
        } catch (EOFException e) {
            return new ArrayList<>();
        }
    }

    @FXML
    private void gererRetour(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
    }

    private void afficherMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
