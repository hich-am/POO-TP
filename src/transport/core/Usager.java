package transport.core;

import java.time.LocalDate;

public class Usager extends Personne {
    public Usager(String nom, String prenom, LocalDate dateNaissance, boolean handicape) {
        super(nom, prenom, dateNaissance, handicape);
    }
}