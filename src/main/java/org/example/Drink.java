package org.example;

public final class Drink extends Produs {
    private double volum;

    public Drink(String nume, double pret, double volum, Categorie categorie) {
        super(nume, pret, categorie);
        this.volum = volum;
    }

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
        return super.toString() + ", volum=" + volum + "Categorie=" + getCategorie() +'}';
    }
}
