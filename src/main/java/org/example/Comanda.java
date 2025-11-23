package org.example;

import java.util.HashMap;
import java.util.Map;

public class Comanda {
    private  double TVA ;
    private Map<Produs, Integer> continutComanda;

    public Comanda(double tvaProcent) {
        this.TVA = tvaProcent ;
        this.continutComanda = new HashMap<>();
    }

    public void adaugaProdus(Produs produs, int cantitate) {
        this.continutComanda.merge(produs, cantitate, (valoareVeche, cantitateNoua) -> valoareVeche + cantitateNoua);
    }

    public Map<Produs, Integer> getComanda() {
        return continutComanda;
    }

    public double calculeazaTotal(ReguliReducere reguliReducere) {
        double reducere = reguliReducere.aplicaReducere(this);
        double totalFaraTVA = 0.0;
        for (Map.Entry<Produs, Integer> entry : continutComanda.entrySet()) {
            Produs produs = entry.getKey();
            Integer cantitate = entry.getValue();
            totalFaraTVA += produs.getPret() * cantitate;
        }
        double totalCuTVA = (totalFaraTVA - reducere) * (1 + TVA);
        return totalCuTVA;
    }
}