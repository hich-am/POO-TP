package transport.core;

import java.time.LocalDate;

public class Ticket extends TitreTransport {
    private static final long serialVersionUID = 1L;

    public Ticket() {
        super(50.0);
    }

    @Override
    public boolean estValide(LocalDate date) throws TitreNonValideException {
        if (date.isEqual(getDateAchat())) {
            return true;
        } else {
            throw new TitreNonValideException("Ticket numéro " + getId() + " expiré - valable uniquement le : " + getDateAchat());
        }
    }

    @Override
    public String toString() {
        return String.format("Ticket #%d - Prix: %.2f DA - Valable le: %s", 
            getId(), getPrix(), getDateAchat().toString());
    }
}