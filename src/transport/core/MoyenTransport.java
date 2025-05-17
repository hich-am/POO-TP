package transport.core;

public class MoyenTransport implements Suspendable{
    private String identifiant;
    private boolean suspendu;

    public MoyenTransport(String identifiant) {
        this.identifiant = identifiant;
    }

    public String toString() {
        return this.identifiant;
    }


    public String getIdentifiant() { return identifiant; }


    @Override
    public void suspendre() {suspendu = true;}
    @Override
    public void reactiver() {suspendu = false;}
    @Override
    public boolean estSuspendu() {return suspendu;}
    @Override
    public String getEtat() {return suspendu ? "suspendu" : "actif";}
  

}