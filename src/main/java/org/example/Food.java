package org.example;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FOOD")
public class Food extends Produs {
    private int gramaj;
    boolean isVegan;

    public Food() {}

    public Food(String nume, double pret, int calorii, boolean vegan, Categorie categorie) {
        super(nume, pret, categorie);
        this.gramaj = calorii;
        this.isVegan = vegan;
    }

    public int getCalorii() {
        return gramaj;
    }
    public boolean isVegan() {
        return isVegan;
    }

    @Override
    public String toString() {
        // Afișează: Pizza (32.0 RON) - 500g [FEL_PRINCIPAL]
        String veg = isVegan() ? " [Vegetarian]" : "";
        return String.format("%s (%.2f RON) - %d g [%s]%s",
                getNume(), getPret(), getCalorii(), getCategorie(), veg);
    }
}
