package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELAȚIE USER (Cine a marcat comanda) ---
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- RELAȚIE MASĂ (Unde s-a servit) ---
    // Folosim entitatea Masa, nu un simplu int
    @ManyToOne
    @JoinColumn(name = "masa_nr", nullable = false)
    private Masa masa;

    private double total;
    private LocalDateTime dataora;

    // --- RELAȚIE DB (Ce s-a vândut efectiv - Istoric) ---
    // CascadeType.ALL = Când salvezi Comanda, se salvează automat și itemele ei
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ComandaItem> items = new ArrayList<>();

    // --- LISTĂ TEMPORARĂ PENTRU LOGICĂ (Nu se salvează în DB) ---
    // O folosim doar pentru a calcula ofertele (Happy Hour, etc.) înainte de salvare
    @Transient
    private List<Produs> produse = new ArrayList<>();

    public Comanda() {}

    // Constructorul primește acum obiectul Masa
    public Comanda(User user, Masa masa, double total) {
        this.user = user;
        this.masa = masa;
        this.total = total;
        this.dataora = LocalDateTime.now();
    }

    // --- METODE AJUTĂTOARE ---

    // Adaugă un item în lista de DB și setează relația inversă
    public void adaugaItem(ComandaItem item) {
        items.add(item);
        item.setComanda(this);
    }

    // --- GETTERS & SETTERS ---

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Masa getMasa() { return masa; }
    public void setMasa(Masa masa) { this.masa = masa; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public LocalDateTime getDataora() { return dataora; }

    public List<ComandaItem> getItems() { return items; }
    public void setItems(List<ComandaItem> items) { this.items = items; }

    // Metode pentru lista @Transient (folosite de Model pt calcule)
    public List<Produs> getProduse() { return produse; }
    public void setProduse(List<Produs> produse) { this.produse = produse; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM HH:mm");
        int nrMesei = (masa != null) ? masa.getNumar() : 0;

        // --- NOU: Construim un șir cu numele produselor ---
        StringBuilder produseString = new StringBuilder();
        if (items != null) {
            for (ComandaItem item : items) {
                if (produseString.length() > 0) {
                    produseString.append(", "); // Punem virgulă între produse
                }
                // Asigură-te că în ComandaItem ai metoda getNume() sau getNumeProdus()
                produseString.append(item.getNumeProdus());
            }
        }

        // Returnăm formatul complet: [Data] Masa X - Total RON (Produse...)
        return String.format("[%s] Masa %d - %.2f RON (%s)",
                dataora.format(dtf), nrMesei, total, produseString.toString());
    }
}