package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RestaurantApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Inițializare Model (Date & Logică)
        // Acesta va verifica și dacă există userul admin/admin în baza de date
        RestaurantModel model = new RestaurantModel();

        // 2. Inițializare View (Interfață Grafică)
        // View-ul pornește implicit cu ecranul de Login (definit în constructorul său)
        RestaurantView view = new RestaurantView();

        // 3. Inițializare Controller (Leagă butoanele din View de logica din Model)
        // Controller-ul activează butoanele de Login, Guest, etc.
        new RestaurantController(model, view);

        // 4. Configurare Scenă și Fereastră
        // Setăm o dimensiune suficientă pentru tab-urile managerului și listele ospătarului
        Scene scene = new Scene(view, 900, 600);

        primaryStage.setTitle("Aplicație Restaurant");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}