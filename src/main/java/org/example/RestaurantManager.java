package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RestaurantManager {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static RestaurantConfiguration incarcaConfigurare(String fisier) throws RestaurantException {
        Path cale = Paths.get(fisier);
        if (!Files.exists(cale)) {
            throw new RestaurantException("Fișierul de configurare '" + fisier + "' lipsește! Contactați suportul.");
        }

        try {
            return mapper.readValue(cale.toFile(), RestaurantConfiguration.class);
        } catch (IOException e) {
            throw new RestaurantException("Fișierul de configurare este corupt sau nu poate fi citit.", e);
        }
    }

    public static void exportaMeniu(List<Produs> produse, String fisierDestinatie) throws RestaurantException {
        try {
            mapper.writeValue(Paths.get(fisierDestinatie).toFile(), produse);
            System.out.println(" Meniu exportat cu succes în: " + fisierDestinatie);
        } catch (IOException e) {
            throw new RestaurantException("Nu s-a putut exporta meniul.", e);
        }
    }
}
