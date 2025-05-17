package transport.core;

import java.time.*;
public class CartePersonnelle extends TitreTransport {
    private static final long serialVersionUID = 1L;
    private final Personne man;
    private final TypeCarte type;

    public CartePersonnelle(Personne man) throws ReductionImpossibleException {
        super(calculatePrix(man));
        this.man = man;
        this.type = findtype(man);
    }

    private static double calculatePrix(Personne man) throws ReductionImpossibleException {
        double premprix = 5000.0;
        if (man instanceof Employe) {
            return premprix * 0.6; 
        } else if (man.isHandicape()) {
            return premprix * 0.5; 
        } else {
            int age = Period.between(man.getDateNaissance(), LocalDate.now()).getYears();
            if (age < 25) {
                return premprix * 0.7; 
            } else if (age > 65) {
                return premprix * 0.75; 
            } else {
                throw new ReductionImpossibleException("Vous n'avez droit à aucune réduction");
            }
        }
    }

    private static TypeCarte findtype(Personne man) {
        if (man instanceof Employe) {
            return TypeCarte.PARTENAIRE;
        } else if (man.isHandicape()) {
            return TypeCarte.SOLIDARITE;
        } else {
            int age = Period.between(man.getDateNaissance(), LocalDate.now()).getYears();
            if (age < 25) {
                return TypeCarte.JUNIOR;
            } else if (age > 65) {
                return TypeCarte.SENIOR;
            }
        }
        return null;
    }

    public TypeCarte getType() {
        return type;
    }

    @Override
    public boolean estValide(LocalDate date) throws TitreNonValideException {
        if ( getDateAchat().plusMonths(12).isAfter(date)) {
            return true;
        } else {
            throw new TitreNonValideException("Carte personnelle numéro " + getId() + " invalide");
        }
    }

    @Override
    public String toString() {
        return String.format("Carte %s #%d - Prix: %.2f DA - Propriétaire: %s - Valable jusqu'au: %s",
            type, getId(), getPrix(), man.toString(), 
            getDateAchat().plusMonths(12).toString());
    }
}
