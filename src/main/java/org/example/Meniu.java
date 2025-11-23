package org.example;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class Meniu {

    private List<Produs> listaProduse;
    public Meniu(List<Produs> listaProduse) {
        this.listaProduse = listaProduse;
    }

    public List<Produs> getProduseByCategorie(Categorie categorie) {
        return listaProduse.stream()
            .filter(produs -> produs.getCategorie() == categorie)
            .collect(Collectors.toList());
    }

    public List<Food> getPreparateVegetarieneSortate() {
        return listaProduse.stream()
            .filter(produs -> produs instanceof Food)
            .map(produs -> (Food) produs)
            .filter(food -> food.isVegan())
            .sorted(Comparator.comparing(Produs::getNume))
            .collect(Collectors.toList());
    }

    public OptionalDouble getPretMediuDesert() {
        return listaProduse.stream()
            .filter(produs -> produs.getCategorie() == Categorie.DESERT)
            .mapToDouble(Produs::getPret)
            .average();
    }

    public boolean hasProdusScump() {
        return listaProduse.stream()
            .anyMatch(produs -> produs.getPret() > 100.0);
    }

    public Optional<Produs> cautaProdus(String nume) {
        return listaProduse.stream()
            .filter(produs -> produs.getNume().equalsIgnoreCase(nume))
            .findFirst();
    }
}
