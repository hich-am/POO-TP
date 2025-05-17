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
    @FXML private ComboBox<String> choixReduction;
    @FXML private ComboBox<String> choixPaiement;
    
    private final String FICHIER_DATA = System.getProperty("user.dir") + File.separator + "fare_media.dat";

    @FXML
    public void initialize() {
        // Add listener to enable/disable reduction based on titre type
        choixTypeTitre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            choixReduction.setDisable(newVal.equals("Ticket"));
        });
    }

    @FXML
    public void gererSoumettre(ActionEvent event) {
        try {
            // Validate inputs
            String prenom = champPrenom.getText().trim();
            String nom = champNom.getText().trim();
            String typeUtilisateur = choixTypeUtilisateur.getValue();
            String typeTitre = choixTypeTitre.getValue();
            String reduction = choixReduction.getValue();
            String paiement = choixPaiement.getValue();

            if (prenom.isEmpty() || nom.isEmpty() || typeUtilisateur == null || typeTitre == null || paiement == null) {
                afficherMessage("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Create person based on type
            Personne personne;
            LocalDate dateNaissance = LocalDate.now().minusYears(
                typeUtilisateur.equals("Élève") ? 15 :
                typeUtilisateur.equals("Étudiant") ? 20 : 35
            );
            
            boolean handicape = reduction != null && reduction.equals("Solidarité");
            
            if (typeUtilisateur.equals("Travailleur") && reduction != null && reduction.equals("Partenaire")) {
                personne = new Employe(nom, prenom, dateNaissance, handicape, 
                    "EMP" + System.currentTimeMillis(), Fonction.AGENT);
            } else {
                personne = new Usager(nom, prenom, dateNaissance, handicape);
            }

            // Load existing tickets
            List<TitreTransport> titres = chargerTitres();

            // Create and add new ticket
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

            titres.add(nouveauTitre);
            sauverTitres(titres);

            afficherMessage("Titre créé avec succès!\nID: " + nouveauTitre.getId() + 
                          "\nPrix: " + nouveauTitre.getPrix() + " DA");

            // Auto-return after success
            final Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        MenuController.chargerScene(currentStage, "/transport/ui/menu.fxml");
                    });
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
