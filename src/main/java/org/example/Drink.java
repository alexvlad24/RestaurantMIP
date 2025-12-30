package org.example;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DRINK")
public class Drink extends Produs {
    private double volum;

    public Drink(String nume, double pret, double volum, Categorie categorie) {
        super(nume, pret, categorie);
        this.volum = volum;
    }

    public Drink() {}

    public double getVolum() {
        return volum;
    }

    public boolean isAlcool() {
        return getNume().equals("Vin") || getNume().equals("Bere") || getNume().equals("Whisky") ||
                getNume().equals("Vodka") || getNume().equals("Rom") || getNume().equals("Tequila") || getNume().equals("Cognac")
                || getNume().equals("Gin") || getNume().equals("Champagne")  || getNume().equals("Cocktail") ||
                getNume().equals("Martini") || getNume().equals("Mojito");
    }

    @Override
    public String toString() {
        // Afișează: Cola (10.0 RON) - 330ml [BĂUTURI_RĂCORITOARE]
        // Folosim volumul (care e double sau int la tine)
        return String.format("%s (%.2f RON) - %.0f L [%s]",
                getNume(), getPret(), (double)volum, getCategorie());
    }
}
