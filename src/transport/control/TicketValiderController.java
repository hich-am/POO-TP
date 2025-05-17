package transport.control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

import transport.core.TitreTransport;
import transport.core.Ticket;
import transport.core.CartePersonnelle;

public class TicketValiderController {

    @FXML private TextField champId;
    @FXML private Label labelResultat;

    // Change the file path to be absolute
    private final String FICHIER_DATA = System.getProperty("user.dir") + File.separator + "fare_media.dat";

    @FXML
    public void gererValider(ActionEvent event) {
        String id = champId.getText().trim();
        if (id.isEmpty()) {
            labelResultat.setText("Veuillez entrer un ID.");
            return;
        }

        boolean trouve = false;

        try {
            List<TitreTransport> titres = chargerTitres();
            if (titres.isEmpty()) {
                labelResultat.setText("Aucun titre n'est enregistré.");
                return;
            }

            for (TitreTransport titre : titres) {
                if (String.valueOf(titre.getId()).equals(id)) {
                    trouve = true;  // Set trouve to true when found
                    if (titre instanceof Ticket) {
                        Ticket t = (Ticket) titre;
                        if (t.getDateAchat().isEqual(LocalDate.now())) {
                            labelResultat.setText("Ticket valide ✅");
                        } else {
                            labelResultat.setText("Ticket expiré ❌");
                        }
                    } else if (titre instanceof CartePersonnelle) {
                        CartePersonnelle c = (CartePersonnelle) titre;
                        if (c.getDateAchat().plusMonths(12).isAfter(LocalDate.now())) {
                            labelResultat.setText("Carte valide ✅");
                        } else {
                            labelResultat.setText("Carte expirée ❌");
                        }
                    }
                    break;
                }
            }

            if (!trouve) labelResultat.setText("ID invalide ❌");

            // Auto-return after success
            final Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            if (trouve) {
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (Exception e) {
            labelResultat.setText("Erreur lors du chargement des données.");
        }
    }

    private List<TitreTransport> chargerTitres() throws IOException, ClassNotFoundException {
        File fichier = new File(FICHIER_DATA);
        if (!fichier.exists()) {
            // Create parent directories if they don't exist
            fichier.getParentFile().mkdirs();
            // Create empty list if file doesn't exist
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fichier))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return (List<TitreTransport>) obj;
            }
            return new ArrayList<>();
        } catch (EOFException e) {
            // Handle empty file
            return new ArrayList<>();
        }
    }

    @FXML
    private void gererRetour(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
    }
}
