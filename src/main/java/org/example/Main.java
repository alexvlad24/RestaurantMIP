package org.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        RestaurantConfiguration config = null;
        try {
            config = RestaurantManager.incarcaConfigurare("app-config.json");
            System.out.println("Configurație încărcată. TVA aplicat: " + (config.tvaProcent() * 100) + "%");
        } catch (RestaurantException e) {
            System.err.println("Eroare configurare: " + e.getMessage());
            // Fallback pe valori default dacă fișierul lipsește
            config = new RestaurantConfiguration("Restaurant Default", 0.09);
        }

        System.out.println("−−− Meniul Restaurantului \"" + config.numeRestaurant() + "\" −−−");

        System.out.println("--- Testare Pizza Builder ---");
        Pizza pizzaMargherita = new Pizza.Builder("Clasic", "Sos de roșii")
                .cuNume("Margherita")
                .cuTopping("Mozzarella")
                .cuTopping("Busuioc")
                .build();

        Pizza pizzaCasei = new Pizza.Builder("Pufos", "Sos alb")
                .cuNume("Pizza Casei")
                .cuTopping("Mozzarella")
                .cuTopping("Ciuperci")
                .cuTopping("Salam")
                .build();

        System.out.println(pizzaMargherita);
        System.out.println(pizzaCasei);

        System.out.println("\n--- Testare Meniu și Streams ---");

        List<Produs> produse = List.of(
                pizzaMargherita,
                pizzaCasei,
                new Food("Paste Carbonara", 45.0, 400, false, Categorie.FEL_PRINCIPAL),
                new Food("Salată Grecească", 32.0, 350, true, Categorie.APERITIV),
                new Food("Lava Cake", 25.0, 300, true, Categorie.DESERT),
                new Food("Tiramisu", 23.0, 320, true, Categorie.DESERT),
                new Food("Friptură Vită", 110.0, 450, false, Categorie.FEL_PRINCIPAL),
                new Drink("Apă Plată", 8.0, 0.5, Categorie.BAUTURA_RACORITOARE),
                new Drink("Vin Roșu", 18.0, 0.15, Categorie.BAUTURA_ALCOOLICA)
        );

        Meniu meniu = new Meniu(produse);

        System.out.println("\nAfisare deserturi:");
        meniu.getProduseByCategorie(Categorie.DESERT)
                .forEach(System.out::println);

        System.out.println("\nPreparate vegetariene sortate:");
        meniu.getPreparateVegetarieneSortate()
                .forEach(System.out::println);

        System.out.println("\nPreț mediu desert:");
        meniu.getPretMediuDesert()
                .ifPresent(medie -> System.out.printf("Media este: %.2f RON\n", medie));

        System.out.println("\nExistă produs > 100 RON?");
        System.out.println(meniu.hasProdusScump() ? "DA" : "NU");

        System.out.println("\nCăutare sigură:");

        Optional<Produs> gasit = meniu.cautaProdus("Lava Cake");
        gasit.ifPresent(produs -> System.out.println("Găsit: " + produs));

        Optional<Produs> negasit = meniu.cautaProdus("Ciorbă");
        System.out.println("Găsit: " + negasit.orElse(null));

        Food pizzaDiavola = new Food("Pizza Margherita", 45.0, 450, false, Categorie.FEL_PRINCIPAL);
        Drink bere = new Drink("Bere", 8.0, 500, Categorie.BAUTURA_ALCOOLICA);

        ReguliReducere reducereAlcool = (c) -> {
            double reducere = 0.0;
            for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
                Produs produs = entry.getKey();
                int cantitate = entry.getValue();
                if (produs instanceof Drink drink && drink.isAlcool()) {
                    reducere += (produs.getPret() * 0.2) * cantitate;
                }
            }
            return reducere;
        };

        ReguliReducere valentine = (c) -> {
            double totalPret = 0.0;
            for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
                totalPret += entry.getKey().getPret() * entry.getValue();
            }
            return totalPret * 0.1;
        };

        ReguliReducere freeDrink = (c) -> {
            String Drinks[] = {"Limonada", "Suc de portocale", "Apa minerala", "Bere", "Vin", "Cocktail", "Mojito", "Whisky", "Vodka"
                    , "Rom", "Tequila", "Cognac", "Gin", "Champagne", "Martini", "Cafea", "Ceai", "Smoothie", "Milkshake", "Energizant"};
            int numarBauturiGratis = 0;
            for (Map.Entry<Produs, Integer> entry : c.getComanda().entrySet()) {
                Produs produs = entry.getKey();
                int cantitate = entry.getValue();
                if (produs instanceof Food && produs.getNume().contains("Pizza ")) {
                    numarBauturiGratis += cantitate;
                }
            }

            for (int i = 0; i < numarBauturiGratis; i++) {
                int chosenDrink = (int) (Math.random() * Drinks.length);
                Produs bauturaGratis = new Drink(Drinks[chosenDrink], 0.0, 300, Categorie.BAUTURA_RACORITOARE);
                c.adaugaProdus(bauturaGratis, 1);
            }

            return 0.0;
        };

        Comanda comandaClient = new Comanda(config.tvaProcent());

        comandaClient.adaugaProdus(pizzaDiavola, 2);
        comandaClient.adaugaProdus(bere, 3);

        System.out.println("Total de plata fara reducere: " + comandaClient.calculeazaTotal((c) -> 0.0) + " RON");
        System.out.println("Total de plata cu reducere alcool: " + comandaClient.calculeazaTotal(reducereAlcool) + " RON");

        System.out.println("\n--- Export Date ---");
        try {
            RestaurantManager.exportaMeniu(produse, "meniu-export.json");
        } catch (RestaurantException e) {
            System.err.println("Nu s-a putut exporta meniul: " + e.getMessage());
        }
    }
}