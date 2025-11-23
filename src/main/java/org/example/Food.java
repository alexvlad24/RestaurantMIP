package org.example;

public non-sealed class Food extends Produs {
    private int gramaj;
    boolean isVegan;

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
        return super.toString()+", gramaj=" + gramaj + "Categorie=" + getCategorie() +'}';
    }
}
