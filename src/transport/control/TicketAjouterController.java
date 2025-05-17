package transport.control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;

import transport.core.*;

public class TicketAjouterController {
    @FXML private TextField champPrenom;
    @FXML private TextField champNom;
    @FXML private ComboBox<String> choixTypeUtilisateur;
    @FXML private ComboBox<String> choixTypeTitre;
    @FXML private ComboBox<String> choixPaiement;
    @FXML private DatePicker dateNaissancePicker; // Add this
    @FXML private CheckBox handicapeCheckBox;     // Add this
    
    private final String FICHIER_DATA = System.getProperty("user.dir") + File.separator + "fare_media.dat";

    @FXML
    public void initialize() {
        choixTypeUtilisateur.getItems().setAll("Conducteur", "Agent de station", "Usager");
        choixTypeTitre.getItems().setAll("Ticket", "Carte de navigation");
        choixPaiement.getItems().setAll("Espèces", "Dahabia", "BaridiMob");
        
        dateNaissancePicker.setValue(LocalDate.now().minusYears(20));
    }

    @FXML
    public void gererSoumettre(ActionEvent event) {
        try {
            String prenom = champPrenom.getText().trim();
            String nom = champNom.getText().trim();
            String typeUtilisateur = choixTypeUtilisateur.getValue();
            String typeTitre = choixTypeTitre.getValue();
            String paiement = choixPaiement.getValue();
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            boolean handicape = handicapeCheckBox.isSelected();

            if (prenom.isEmpty() || nom.isEmpty() || typeUtilisateur == null || 
                typeTitre == null || paiement == null || dateNaissance == null) {
                afficherMessage("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Create person based on type
            Personne personne;
            if (typeUtilisateur.equals("Conducteur")) {
                personne = new Employe(nom, prenom, dateNaissance, handicape,
                    "EMP" + System.currentTimeMillis(), Fonction.CONDUCTEUR);
            } else if (typeUtilisateur.equals("Agent de station")) {
                personne = new Employe(nom, prenom, dateNaissance, handicape,
                    "EMP" + System.currentTimeMillis(), Fonction.AGENT);
            } else {
                personne = new Usager(nom, prenom, dateNaissance, handicape);
            }

            // Create title - reduction will be automatically applied based on age and type
            TitreTransport nouveauTitre;
            if (typeTitre.equals("Ticket")) {
                nouveauTitre = new Ticket();
            } else {
                try {
                    nouveauTitre = new CartePersonnelle(personne);
                } catch (ReductionImpossibleException e) {
                    afficherMessage("Impossible de créer la carte: " + e.getMessage());
                    return;
                }
            }

            // Save the new title
            List<TitreTransport> titres = chargerTitres();
            titres.add(nouveauTitre);
            sauverTitres(titres);

            // Show success message with calculated price
            afficherMessage(String.format(
                "Titre créé avec succès!\nID: %d\nPrix: %.2f DA%s", 
                nouveauTitre.getId(), 
                nouveauTitre.getPrix(),
                nouveauTitre instanceof CartePersonnelle ? 
                    "\nType: " + ((CartePersonnelle)nouveauTitre).getType() : ""
            ));

            // Auto-return after success
            final Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> MenuController.chargerScene(currentStage, "/transport/ui/menu.fxml"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void gererRetour(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
    }

    private void afficherMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void sauverTitres(List<TitreTransport> titres) throws IOException {
        File fichier = new File(FICHIER_DATA);
        // Create parent directories if they don't exist
        fichier.getParentFile().mkdirs();
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fichier))) {
            out.writeObject(titres);
        }
    }

    private List<TitreTransport> chargerTitres() throws IOException, ClassNotFoundException {
        File fichier = new File(FICHIER_DATA);
        if (!fichier.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fichier))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return (List<TitreTransport>) obj;
            }
            return new ArrayList<>();
        } catch (EOFException e) {
            return new ArrayList<>();
        }
    }
}
