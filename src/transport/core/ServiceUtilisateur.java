package transport.core;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ServiceUtilisateur {
    private static final String FICHIER_USERS = "utilisateur.dat";
    private List<Personne> utilisateurs;
    private static ServiceUtilisateur instance;
    private int lastEmployeeNumber = 0;

    private ServiceUtilisateur() {
        this.utilisateurs = chargerUtilisateurs();
        // Initialize lastEmployeeNumber based on existing employees
        for (Personne p : utilisateurs) {
            if (p instanceof Employe) {
                String matricule = ((Employe) p).getMatricule();
                try {
                    int number = Integer.parseInt(matricule.substring(3));
                    lastEmployeeNumber = Math.max(lastEmployeeNumber, number);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    // Skip invalid matricule format
                }
            }
        }
    }

    public static ServiceUtilisateur getInstance() {
        if (instance == null) {
            instance = new ServiceUtilisateur();
        }
        return instance;
    }

    public Personne ajouterUsager(String nom, String prenom, LocalDate dateNaissance, boolean handicape) {
        Personne usager = new Usager(nom, prenom, dateNaissance, handicape);
        utilisateurs.add(usager);
        sauverUtilisateurs();
        return usager;
    }

    private String generateMatricule() {
        lastEmployeeNumber++;
        return String.format("EMP%04d", lastEmployeeNumber);
    }

    public Personne ajouterEmploye(String nom, String prenom, LocalDate dateNaissance, 
                                 boolean handicape, Fonction fonction) {
        String matricule = generateMatricule();
        Personne employe = new Employe(nom, prenom, dateNaissance, handicape, matricule, fonction);
        utilisateurs.add(employe);
        sauverUtilisateurs();
        return employe;
    }

    public List<Personne> getUtilisateurs() {
        return new ArrayList<>(utilisateurs);
    }

    private List<Personne> chargerUtilisateurs() {
        File fichier = new File(FICHIER_USERS);
        if (!fichier.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fichier))) {
            return (List<Personne>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void sauverUtilisateurs() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FICHIER_USERS))) {
            out.writeObject(utilisateurs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}