package org.example;

import java.util.ArrayList;
import java.util.List;

public final class Pizza extends Food {

    private final String tipBlat;
    private final String tipSos;
    private final List<String> toppinguri;

    private Pizza(Builder builder) {

        super(builder.nume,
              builder.calculPretFinal(),
              500, builder.isVegan,
              Categorie.FEL_PRINCIPAL);

        this.tipBlat = builder.tipBlat;
        this.tipSos = builder.tipSos;
        this.toppinguri = builder.toppinguri;
    }

    @Override
    public String toString() {
        return String.format("Pizza: %s (Blat: %s, Sos: %s, Topping: %s) - %.2f RON",
                getNume(), tipBlat, tipSos, toppinguri, getPret());
    }

    public static class Builder {

        private final String tipBlat;
        private final String tipSos;

        private List<String> toppinguri = new ArrayList<>();
        private String nume = "Pizza Customizată";
        private double pretBaza = 25.0;
        private double pretPerTopping = 3.0;
        private boolean isVegan = true;

        public Builder(String tipBlat, String tipSos) {
            this.tipBlat = tipBlat;
            this.tipSos = tipSos;
        }

        public Builder cuTopping(String topping) {
            this.toppinguri.add(topping);
            if (topping.equals("Salam") || topping.equals("Prosciutto")) {
                this.isVegan = false;
            }
            return this;
        }

        public Builder cuNume(String nume) {
            this.nume = nume;
            return this;
        }

        private double calculPretFinal() {
            return this.pretBaza + (this.toppinguri.size() * this.pretPerTopping);
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}
