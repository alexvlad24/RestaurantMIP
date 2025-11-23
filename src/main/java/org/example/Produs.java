package org.example;
import org.example.Categorie;
public sealed abstract class Produs permits  Drink, Food{
    private String nume;
    private double pret;
    private Categorie categorie;
    public Produs(String nume, double pret, Categorie categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    public String getNume() {
        return nume;
    }
    public double getPret() {
        return pret;
    }
    public Categorie getCategorie() {
        return categorie;
    }
    @Override
    public String toString() {
        return "Produs{" +
                "nume='" + nume + '\'' +
                ", pret=" + pret + ", categorie=" + categorie +
                '}';
    }
}
