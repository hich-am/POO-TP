package transport.control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;
import transport.core.*;

public class ReclamationAfficherController {
    @FXML private ListView<String> listeReclamations;
    @FXML private Label labelAlerte;

    private final String FICHIER_RECLAMATION = System.getProperty("user.dir") + File.separator + "reclamations.dat";

    @FXML
    public void initialize() {
        try {
            List<Reclamation> reclamations = chargerReclamations();
            if (reclamations.isEmpty()) {
                listeReclamations.getItems().add("Aucune réclamation enregistrée.");
                return;
            }

            // Group reclamations by target
            Map<Suspendable, List<Reclamation>> reclamationsParCible = new HashMap<>();
            for (Reclamation r : reclamations) {
                reclamationsParCible.computeIfAbsent(r.getCible(), k -> new ArrayList<>()).add(r);
            }

            // Check for suspensions and display warnings
            for (Map.Entry<Suspendable, List<Reclamation>> entry : reclamationsParCible.entrySet()) {
                if (entry.getValue().size() >= 3) {
                    labelAlerte.setText("⚠️ " + entry.getKey().toString() + " est suspendu(e) : 3 réclamations ou plus.");
                }
            }

            // Display all reclamations
            for (Reclamation r : reclamations) {
                listeReclamations.getItems().add(r.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            listeReclamations.getItems().add("Erreur lors du chargement des réclamations: " + e.getMessage());
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

    @FXML
    private void gererRetour(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MenuController.chargerScene(stage, "/transport/ui/menu.fxml");
    }
}
