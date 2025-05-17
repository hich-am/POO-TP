package transport.core;

import java.time.LocalDate;
import java.io.Serializable;

public abstract class Personne implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private boolean handicape;

    public Personne(String nom, String prenom, LocalDate dateNaissance, boolean handicape) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.handicape = handicape;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public boolean isHandicape() {
        return handicape;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
