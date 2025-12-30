package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relația Many-to-One: Mai multe iteme aparțin unei singure comenzi
    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    private String numeProdus; // Salvăm numele (snapshot) ca să rămână istoricul chiar dacă produsul se șterge
    private double pret;       // Salvăm prețul la momentul vânzării

    public ComandaItem() {}

    public ComandaItem(String numeProdus, double pret) {
        this.numeProdus = numeProdus;
        this.pret = pret;
    }

    // Getters & Setters
    public void setComanda(Comanda comanda) { this.comanda = comanda; }
    public String getNumeProdus() { return numeProdus; }
    public double getPret() { return pret; }

    @Override
    public String toString() {
        return numeProdus + " (" + pret + " RON)";
    }
}
