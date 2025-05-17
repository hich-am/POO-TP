package transport.core;

import java.time.LocalDate;
import java.io.Serializable;

public abstract class TitreTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;
    private final int id;
    private final LocalDate dateAchat;
    private final double prix;

    public TitreTransport(double prix) {
        this.id = idCounter++;
        this.dateAchat = LocalDate.now();
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public double getPrix() {
        return prix;
    }

    public abstract boolean estValide(LocalDate date) throws TitreNonValideException;

    @Override
    public String toString() {
        return String.format("Titre #%d - Prix: %.2f DA - Acheté le: %s", 
            id, prix, dateAchat.toString());
    }
}
