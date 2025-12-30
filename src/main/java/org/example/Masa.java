package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "mese")
public class Masa {

    @Id
    private int numar;

    private int locuri;
    private boolean esteOcupata;

    public Masa() {}

    public Masa(int numar, int locuri) {
        this.numar = numar;
        this.locuri = locuri;
        this.esteOcupata = false;
    }

    // Getters & Setters
    public int getNumar() { return numar; }
    public void setNumar(int numar) { this.numar = numar; }

    public int getLocuri() { return locuri; }
    public void setLocuri(int locuri) { this.locuri = locuri; }

    public boolean isEsteOcupata() { return esteOcupata; }
    public void setEsteOcupata(boolean esteOcupata) { this.esteOcupata = esteOcupata; }

    @Override
    public String toString() {
        return "Masa " + numar + " (" + locuri + " locuri)";
    }
}
