package transport.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform;

import java.io.*;
import java.util.*;
import java.time.LocalDate;

import transport.core.TitreTransport;
import transport.core.Ticket;
import transport.core.CartePersonnelle;
import transport.core.TitreNonValideException;

import transport.core.*;

public class TicketAfficherController {
    @FXML private ListView<TitreTransport> listeTickets;
    @FXML private Label labelResultat;

    private final String FICHIER_DATA = System.getProperty("user.dir") + File.separator + "fare_media.dat";

    @FXML
    public void initialize() {
        try {
            List<TitreTransport> titres = chargerTitres();
            if (titres.isEmpty()) {
                labelResultat.setText("Aucun titre n'a été enregistré.");
                return;
            }

            Collections.reverse(titres);
            listeTickets.setItems(FXCollections.observableArrayList(titres));

            // Add cell factory to customize display
            listeTickets.setCellFactory(lv -> new ListCell<TitreTransport>() {
                @Override
                protected void updateItem(TitreTransport item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            });
        } catch (Exception e) {
            labelResultat.setText("Erreur lors du chargement : " + e.getMessage());
        }
    }

    @FXML
    public void gererValider(ActionEvent event) {
        TitreTransport selectedTitre = listeTickets.getSelectionModel().getSelectedItem();
        if (selectedTitre == null) {
            labelResultat.setText("Veuillez sélectionner un titre à valider.");
            return;
        }

        try {
            if (selectedTitre instanceof Ticket) {
                Ticket ticket = (Ticket) selectedTitre;
                if (ticket.estValide(LocalDate.now())) {
                    labelResultat.setText("✅ Ticket #" + ticket.getId() + " valide");
                }
            } else if (selectedTitre instanceof CartePersonnelle) {
                CartePersonnelle carte = (CartePersonnelle) selectedTitre;
                if (carte.estValide(LocalDate.now())) {
                    labelResultat.setText("✅ Carte #" + carte.getId() + " valide");
                }
            }
        } catch (TitreNonValideException e) {
            labelResultat.setText("❌ " + e.getMessage());
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
