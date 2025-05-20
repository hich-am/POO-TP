package transport.control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.application.Platform;
import java.io.*;
import java.util.*;
import java.time.LocalDate;
import transport.core.*;

public class ReclamationController {
    @FXML private TextField champUtilisateur;
    @FXML private ComboBox<String> choixTypeReclamation;
    @FXML private ComboBox<String> choixTypeCible;
    @FXML private ComboBox<String> choixCible;
    @FXML private TextArea champCommentaire;
    @FXML private Label labelResultat;

    private final String FICHIER_RECLAMATION = System.getProperty("user.dir") + File.separator + "reclamations.dat";
    private final ServiceReclamation service = new ServiceReclamation();

    @FXML
    public void initialize() {
        choixTypeReclamation.getItems().addAll("TECHNIQUE", "SERVICE");
        
        choixTypeCible.getItems().addAll("Station", "Moyen de transport");
        
        choixTypeCible.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            choixCible.getItems().clear();
            if (newVal != null) {
                if (newVal.equals("Station")) {
                    choixCible.getItems().addAll("Station A", "Station B", "Station C");
                } else {
                    choixCible.getItems().addAll("Bus 1", "Bus 2", "Metro 1");
                }
            }
        });
    }

    @FXML
    private void gererSoumettre(ActionEvent event) {
        try {
            String utilisateur = champUtilisateur.getText().trim();
            String typeStr = choixTypeReclamation.getValue();
            String typeCible = choixTypeCible.getValue();
            String cibleNom = choixCible.getValue();
            String commentaire = champCommentaire.getText().trim();

            if (utilisateur.isEmpty() || typeStr == null || typeCible == null || 
                cibleNom == null || commentaire.isEmpty()) {
                afficherMessage("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            Personne personne = new Usager(utilisateur, "", LocalDate.now(), false);
            
            Suspendable cible = typeCible.equals("Station") ? 
                              new Station(cibleNom) : 
                              new MoyenTransport(cibleNom);
            
            TypeReclamation type = TypeReclamation.valueOf(typeStr);
            Reclamation reclamation = new Reclamation(personne, type, cible, commentaire, LocalDate.now());
            
            List<Reclamation> singleReclamation = new ArrayList<>();
            singleReclamation.add(reclamation);
            sauverReclamations(singleReclamation);
            
            service.soumettre(reclamation);

            afficherMessage("Réclamation enregistrée avec succès!\nNuméro: " + reclamation.getNumero());
            clearFields();

            autoReturn(event);

        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage());
        }
    }

    private void clearFields() {
        champUtilisateur.clear();
        champCommentaire.clear();
        choixTypeReclamation.getSelectionModel().clearSelection();
        choixTypeCible.getSelectionModel().clearSelection();
        choixCible.getSelectionModel().clearSelection();
    }

    private void autoReturn(ActionEvent event) {
        final Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> MenuController.chargerScene(stage, "/transport/ui/menu.fxml"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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

    private void sauverReclamations(List<Reclamation> reclamations) throws IOException {
        List<Reclamation> existingReclamations = chargerReclamations();
        existingReclamations.addAll(reclamations);
        
        List<ReclamationSerializedData> dataList = new ArrayList<>();
        for (Reclamation r : existingReclamations) {
            dataList.add(new ReclamationSerializedData(r));
        }
        
        File fichier = new File(FICHIER_RECLAMATION);
        fichier.getParentFile().mkdirs();
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fichier))) {
            out.writeObject(dataList);
        }
    }

    private List<Reclamation> chargerReclamations() {
        File fichier = new File(FICHIER_RECLAMATION);
        if (!fichier.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fichier))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                List<ReclamationSerializedData> dataList = (List<ReclamationSerializedData>) obj;
                List<Reclamation> reclamations = new ArrayList<>();
                

                    for (ReclamationSerializedData data : dataList) {
                    reclamations.add(data.toReclamation());
                }
                return reclamations;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static class ReclamationData implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int numero;
        private final LocalDate date;
        private final String nomPersonne;
        private final String prenomPersonne;
        private final TypeReclamation type;
        private final String cibleType;
        private final String cibleNom;
        private final String description;

        public ReclamationData(Reclamation r) {
            this.numero = r.getNumero();
            this.date = r.getDate();
            this.prenomPersonne = r.getPersonne().toString();
            this.nomPersonne = "";
            this.type = r.getType();
            this.cibleType = r.getCible() instanceof Station ? "Station" : "MoyenTransport";
            this.cibleNom = r.getCible().toString(); 
            this.description = r.getDescription();
        }

        public Reclamation toReclamation() {
            Personne personne = new Usager(prenomPersonne, "", LocalDate.now(), false);
            Suspendable cible = cibleType.equals("Station") ? 
                new Station(cibleNom) : 
                new MoyenTransport(cibleNom);
            return new Reclamation(personne, type, cible, description, date);
        }
    }
}
