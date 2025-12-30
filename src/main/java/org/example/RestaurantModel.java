package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantModel {

    // --- ACCES DATE (REPOSITORY) ---
    private final ProdusRepository repo = new ProdusRepository();

    // --- STATE (STAREA APLICAȚIEI) ---
    private final ObservableList<Produs> toateProdusele = FXCollections.observableArrayList();
    private final ObservableList<Produs> cosCumparaturi = FXCollections.observableArrayList();
    private final ObservableList<User> listaOspatari = FXCollections.observableArrayList();
    private final ObservableList<Comanda> istoricComenzi = FXCollections.observableArrayList();

    private User currentUser = null;
    private int masaSelectata = 0;

    // --- FLAGS OFERTE (MANAGER) ---
    private boolean isHappyHourActive = false;
    private boolean isMealDealActive = false;
    private boolean isPartyPackActive = false;

    // =================================================================================
    // 1. LOGICA DE BUSINESS: REGULI REDUCERE (LAMBDA EXPRESII)
    // =================================================================================

    // Regula 1: Happy Hour (A 2-a băutură la jumătate de preț)
    private final ReguliReducere happyHourRule = (comanda) -> {
        List<Produs> bauturi = comanda.getProduse().stream()
                .filter(p -> p instanceof Drink)
                .collect(Collectors.toList());
        double discount = 0.0;
        for (int i = 1; i < bauturi.size(); i += 2) {
            discount += bauturi.get(i).getPret() * 0.5;
        }
        return discount;
    };

    // Regula 2: Meal Deal (Pizza -> Cel mai ieftin desert redus cu 25%)
    private final ReguliReducere mealDealRule = (comanda) -> {
        boolean arePizza = comanda.getProduse().stream().anyMatch(p -> p instanceof Pizza);
        if (!arePizza) return 0.0;

        Optional<Produs> desertIeftin = comanda.getProduse().stream()
                .filter(p -> p.getCategorie() == Categorie.DESERT)
                .min(Comparator.comparing(Produs::getPret));

        return desertIeftin.map(produs -> produs.getPret() * 0.25).orElse(0.0);
    };

    // Regula 3: Party Pack (La 4 Pizza, cea mai ieftină este gratis)
    private final ReguliReducere partyPackRule = (comanda) -> {
        List<Produs> pizze = comanda.getProduse().stream()
                .filter(p -> p instanceof Pizza)
                .sorted(Comparator.comparing(Produs::getPret))
                .collect(Collectors.toList());

        if (pizze.size() >= 4) {
            return pizze.get(0).getPret();
        }
        return 0.0;
    };

    public RestaurantModel() {
        refreshProduse();

        // Asigurăm un cont de admin dacă baza e goală (pentru testare rapidă)
        if (repo.gasesteUser("admin", "admin") == null) {
            System.out.println("Creare user admin default...");
            repo.adaugaUser(new User("admin", "admin", "MANAGER"));
            repo.adaugaUser(new User("ion", "1234", "OSPATAR"));
        }

        if (repo.getMasa(1) == null) {
            System.out.println("Se initializeaza mesele...");
            for (int i = 1; i <= 8; i++) {
                // Creăm mesele 1-8 cu capacitate de 4 locuri
                repo.salveazaMasa(new Masa(i, 4));
            }
        }
    }

    public void stergeProdus(Produs p) {
        // 1. Șterge efectiv din Baza de Date folosind Repository-ul
        repo.stergeProdus(p);

        // 2. Reîncarcă lista locală (ObservableList)
        // Astfel, tabelul din interfață se actualizează instantaneu
        refreshProduse();
    }

    public void refreshProduse() {
        toateProdusele.setAll(repo.gasesteToateProdusele());
    }

    public void refreshIstoricGlobal() {
        istoricComenzi.setAll(repo.getToateComenzile());
    }

    public void refreshOspatari() {
        listaOspatari.setAll(repo.getOspatari());
    }

    // Metoda pentru ANGAJARE
    public void angajeazaOspatar(String username, String password) {
        // Creăm userul cu rolul fix de OSPATAR
        User u = new User(username, password, "OSPATAR");
        repo.adaugaUser(u);
        refreshOspatari();
    }

    public boolean login(String user, String pass) {
        User u = repo.gasesteUser(user, pass);
        if (u != null) {
            this.currentUser = u;
            // Dacă e ospătar, îi încărcăm istoricul personal
            if ("OSPATAR".equals(u.getRol())) {
                istoricComenzi.setAll(repo.getComenziUser(u));
            }
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
        this.cosCumparaturi.clear();
        this.istoricComenzi.clear();
        this.masaSelectata = 0;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // =================================================================================
    // 4. LOGICA DE CALCUL BON & COMANDĂ
    // =================================================================================
    public String calculeazaTotalBon() {
        // Creăm o comandă temporară pentru a pasa lista de produse către Lambda-uri
        Comanda comandaTemp = new Comanda();
        comandaTemp.setProduse(new ArrayList<>(cosCumparaturi));

        double totalBrut = cosCumparaturi.stream().mapToDouble(Produs::getPret).sum();
        double totalDiscount = 0.0;
        StringBuilder raport = new StringBuilder();

        raport.append(String.format("Total Brut: %.2f RON\n", totalBrut));

        if (isHappyHourActive) {
            double d = happyHourRule.aplicaReducere(comandaTemp);
            if (d > 0) {
                totalDiscount += d;
                raport.append(String.format(" - Happy Hour: -%.2f RON\n", d));
            }
        }

        if (isMealDealActive) {
            double d = mealDealRule.aplicaReducere(comandaTemp);
            if (d > 0) {
                totalDiscount += d;
                raport.append(String.format(" - Meal Deal: -%.2f RON\n", d));
            }
        }

        if (isPartyPackActive) {
            double d = partyPackRule.aplicaReducere(comandaTemp);
            if (d > 0) {
                totalDiscount += d;
                raport.append(String.format(" - Party Pack: -%.2f RON\n", d));
            }
        }

        raport.append("---------------------\n");
        raport.append(String.format("TOTAL FINAL: %.2f RON", totalBrut - totalDiscount));
        return raport.toString();
    }

    public void plaseazaComanda() {
        // 1. Calculăm totalul (cod existent)
        String bon = calculeazaTotalBon();
        String linieTotal = bon.substring(bon.lastIndexOf("TOTAL FINAL:") + 13).replace(" RON", "").trim();
        linieTotal = linieTotal.replace(",", ".");
        double total = Double.parseDouble(linieTotal);

        // 2. --- FIXUL ESTE AICI ---
        // Căutăm obiectul Masa real în baza de date folosind numărul selectat (int)
        Masa obiectMasa = repo.getMasa(masaSelectata);

        // Verificăm dacă masa există (siguranță)
        if (obiectMasa == null) {
            // Dacă masa nu există în DB, creăm una temporar sau aruncăm eroare.
            // Aici presupunem că există.
            obiectMasa = new Masa(masaSelectata, 4);
        }

        // Acum pasăm OBIECTUL Masa, nu int-ul
        Comanda cmd = new Comanda(currentUser, obiectMasa, total);

        // 3. Adăugăm produsele (cod existent - ComandaItem)
        for (Produs p : cosCumparaturi) {
            ComandaItem item = new ComandaItem(p.getNume(), p.getPret());
            cmd.adaugaItem(item);
        }

        // 4. Setăm și lista transient pentru calcule (cod existent)
        cmd.setProduse(new ArrayList<>(cosCumparaturi));

        // 5. Salvăm
        repo.salveazaComanda(cmd);

        istoricComenzi.add(cmd);
        refreshIstoricGlobal();
        cosCumparaturi.clear();
        masaSelectata = 0;
    }

    // =================================================================================
    // 5. METODE PENTRU GUEST (FILTRARE)
    // =================================================================================
    public List<Produs> filtreazaProduse(String cautare, boolean doarVeg, boolean doarBauturi, double maxPret) {
        return toateProdusele.stream()
                .filter(p -> {
                    String nume = Optional.ofNullable(p.getNume()).orElse("").toLowerCase();
                    return nume.contains(cautare.toLowerCase());
                })
                .filter(p -> !doarVeg || (p instanceof Food && ((Food)p).isVegan()))
                .filter(p -> !doarBauturi || (p instanceof Drink))
                .filter(p -> p.getPret() <= maxPret)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // 6. METODE DE ADMINISTRARE (MANAGER CRUD)
    // =================================================================================
    public void adaugaProdus(Produs p) {
        repo.salveazaProdus(p);
        refreshProduse();
    }

    public void stergeOspatar(User u) {
        repo.stergeUser(u);
        refreshOspatari();
    }

    // Modificare produs existent
    public void actualizeazaProdus(Produs p, String numeNou, double pretNou) {
        // Actualizăm obiectul Java
        // Nota: Asigură-te că clasa Produs are setNume si setPret!
        // Daca nu, trebuie adaugate in Produs.java (vezi mai jos)
        p.setNume(numeNou);
        p.setPret(pretNou);

        // Salvăm în DB (Hibernate va face UPDATE datorită ID-ului existent)
        repo.salveazaProdus(p);
        refreshProduse();
    }

    public void importaDate() {
        // Aici ar trebui logica de citire JSON -> List<Produs>
        // Pentru demo/examen, putem simula sau apela Managerul daca e implementat
        System.out.println("Se importă datele...");
        // De exemplu: List<Produs> importate = RestaurantManager.importa("meniu.json");
        // for(Produs p : importate) repo.salveazaProdus(p);
        refreshProduse();
    }

    public void exportaDate() throws RestaurantException {
        // Folosește clasa utilitară existentă
        RestaurantManager.exportaMeniu(toateProdusele, "meniu_export.json");
    }

    // =================================================================================
    // 7. GETTERS & SETTERS (BOILERPLATE)
    // =================================================================================
    public ObservableList<Produs> getToateProdusele() { return toateProdusele; }
    public ObservableList<Produs> getCos() { return cosCumparaturi; }
    public ObservableList<User> getOspatari() { return listaOspatari; }
    public ObservableList<Comanda> getIstoric() { return istoricComenzi; }
    public void setIstoric(List<Comanda> lista) {
        istoricComenzi.setAll(lista);
    }

    public void adaugaInCos(Produs p) { cosCumparaturi.add(p); }
    public void setMasa(int m) { this.masaSelectata = m; }
    public int getMasa() { return masaSelectata; }

    public void setHappyHour(boolean val) { this.isHappyHourActive = val; }
    public void setMealDeal(boolean val) { this.isMealDealActive = val; }
    public void setPartyPack(boolean val) { this.isPartyPackActive = val; }
    public ProdusRepository getRepo() {
        return repo;
    }
}