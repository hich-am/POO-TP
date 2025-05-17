package transport.core;
import java.util.*;

public class ServiceReclamation {
    private  final int SEUIL = 3;
    private  Map<TypeReclamation,TreeSet<Reclamation>> reclamationsParType = new TreeMap<>();
    private  Map<Personne, TreeSet<Reclamation>> reclamationsParPersonne = new HashMap<>();
    private  Map<Suspendable, TreeSet<Reclamation>> reclamationsParSuspendable = new HashMap<>();
    public void soumettre(Reclamation reclamation) {
        reclamationsParType.computeIfAbsent(reclamation.getType(), k -> new TreeSet<>(Comparator.comparingInt(Reclamation::getNumero)))
                .add(reclamation);

        reclamationsParPersonne.computeIfAbsent(reclamation.getPersonne(), k -> new TreeSet<>(Comparator.comparingInt(Reclamation::getNumero)))
                .add(reclamation);

        reclamationsParSuspendable.computeIfAbsent(reclamation.getCible(), k -> new TreeSet<>(Comparator.comparingInt(Reclamation::getNumero)))
                .add(reclamation);

        if (reclamationsParSuspendable.get(reclamation.getCible()).size() >= SEUIL) {
            reclamation.getCible().suspendre();
        }
    }

    public void afficherReclamations() {
        reclamationsParType.forEach((type, reclamations) -> {
            System.out.println("------------Réclamation de Type " + type);
            reclamations.forEach(System.out::println);
        });
    }

    public void afficherReclamations(Personne personne) {
        TreeSet<Reclamation> reclamations = reclamationsParPersonne.get(personne);
        if (reclamations != null) {
            reclamations.forEach(System.out::println);
        } else {
            System.out.println("Aucune réclamation pour " + personne);
        }
    }

    public void afficherReclamations(Suspendable cible) {
        TreeSet<Reclamation> reclamations = reclamationsParSuspendable.get(cible);
        if (reclamations != null) {
            reclamations.forEach(System.out::println);
        } else {
            System.out.println("Aucune réclamation pour " + cible);
        }
    }

    public void resoudre(Reclamation reclamation) {
        Suspendable cible = reclamation.getCible();
        TreeSet<Reclamation> reclamations = reclamationsParSuspendable.get(cible);
        if (reclamations != null) {
            reclamations.remove(reclamation);
            if (reclamations.size() < SEUIL) {
                cible.reactiver();
            }
        }
    }

}