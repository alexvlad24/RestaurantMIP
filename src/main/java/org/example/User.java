package org.example;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // Folosim "users" la plural deoarece "user" e cuvânt rezervat în SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // Valori posibile: "MANAGER", "OSPATAR"

    // --- RELAȚIA IMPUSĂ DE BAREM ---
    // OneToMany: Un user are mai multe comenzi.
    // mappedBy = "user": Câmpul 'user' din clasa Comanda gestionează relația.
    // cascade = CascadeType.REMOVE: CRITIC! Dacă ștergi userul, Hibernate șterge automat toate comenzile lui.
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Comanda> comenzi = new ArrayList<>();

    // Constructor gol obligatoriu pentru JPA/Hibernate
    public User() {}

    public User(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // --- GETTERS & SETTERS ---

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<Comanda> getComenzi() {
        return comenzi;
    }
    public void setComenzi(List<Comanda> comenzi) {
        this.comenzi = comenzi;
    }

    // --- METODE AJUTĂTOARE PENTRU VIEW ---

    // Suprascriem toString pentru ca ListView<User> în interfață să afișeze numele frumos
    @Override
    public String toString() {
        return username + " [" + rol + "]";
    }
}
