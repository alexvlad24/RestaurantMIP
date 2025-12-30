package org.example;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
@Table(name = "produse")
public abstract class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private double pret;
    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    public Produs() {}

    public Produs(String nume, double pret, Categorie categorie) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
    }

    public Long getId() { return id; } // Getter pt ID
    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }
    public void setNume(String nume) { this.nume = nume; }
    public Categorie getCategorie() { return categorie; }
    @Override
    public String toString() {
        return "Produs{" +
                "nume='" + nume + '\'' +
                ", pret=" + pret + ", categorie=" + categorie +
                '}';
    }
}
