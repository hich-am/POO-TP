package transport.control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import transport.core.*;
import java.time.LocalDate;
import javafx.stage.Stage;
import javafx.scene.Node;

public class UtilisateurController {
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private DatePicker dateNaissance;
    @FXML private CheckBox checkHandicape;
    @FXML private ComboBox<String> typeUtilisateur;
    @FXML private ComboBox<Fonction> choixFonction;
    @FXML private VBox employeFields;
    @FXML private ListView<Personne> listeUtilisateurs;

    private ServiceUtilisateur service;

    @FXML
    public void initialize() {
        service = ServiceUtilisateur.getInstance();
        
        typeUtilisateur.setItems(FXCollections.observableArrayList("Usager", "Employé"));
        choixFonction.setItems(FXCollections.observableArrayList(Fonction.values()));
        
        typeUtilisateur.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            employeFields.setVisible("Employé".equals(newVal));
            employeFields.setManaged("Employé".equals(newVal));
        });
        
        refreshList();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            String nom = champNom.getText().trim();
            String prenom = champPrenom.getText().trim();
            LocalDate date = dateNaissance.getValue();
            boolean handicape = checkHandicape.isSelected();

            if (nom.isEmpty() || prenom.isEmpty() || date == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                return;
            }

            Personne personne;
            if ("Employé".equals(typeUtilisateur.getValue())) {
                Fonction fonction = choixFonction.getValue();
                
                if (fonction == null) {
                    showAlert("Erreur", "Veuillez sélectionner une fonction");
                    return;
                }
                
                personne = service.ajouterEmploye(nom, prenom, date, handicape, fonction);
            } else {
                personne = service.ajouterUsager(nom, prenom, date, handicape);
            }

            showAlert("Succès", "Utilisateur ajouté avec succès");
            clearFields();
            refreshList();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void gererRetour(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
    }

    private void refreshList() {
        listeUtilisateurs.setItems(FXCollections.observableArrayList(service.getUtilisateurs()));
    }

    private void clearFields() {
        champNom.clear();
        champPrenom.clear();
        dateNaissance.setValue(null);
        checkHandicape.setSelected(false);
        choixFonction.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}