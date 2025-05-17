package transport.control;

import java.io.Serializable;
import java.time.LocalDate;
import transport.core.*;

class ReclamationSerializedData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final LocalDate date;
    private final String nomPersonne;
    private final String prenomPersonne;
    private final TypeReclamation type;
    private final String cibleType;
    private final String cibleNom;
    private final String description;

    public ReclamationSerializedData(Reclamation r) {
        this.numero = r.getNumero();
        this.date = r.getDate();
        this.nomPersonne = "";  // Since we're creating dummy persons
        this.prenomPersonne = r.getPersonne().toString();
        this.type = r.getType();
        this.cibleType = r.getCible() instanceof Station ? "Station" : "MoyenTransport";
        this.cibleNom = r.getCible().toString();
        this.description = r.getDescription();
    }

    public Reclamation toReclamation() {
        Personne personne = new Usager(nomPersonne, prenomPersonne, LocalDate.now(), false);
        Suspendable cible = cibleType.equals("Station") ? 
            new Station(cibleNom) : 
            new MoyenTransport(cibleNom);
        return new Reclamation(personne, type, cible, description, date);
    }
}