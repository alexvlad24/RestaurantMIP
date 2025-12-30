package org.example;

import javafx.concurrent.Task;
import java.util.List;

// Extindem Task<List<Comanda>> pentru că returnăm o listă de comenzi
public class IncarcareIstoricTask extends Task<List<Comanda>> {

    private final ProdusRepository repo;

    public IncarcareIstoricTask(ProdusRepository repo) {
        this.repo = repo;
    }

    @Override
    protected List<Comanda> call() throws Exception {
        // Codul de aici rulează pe un Background Thread!
        updateMessage("Se încarcă istoricul...");

        // Apelăm metoda lentă din repository (cea cu Thread.sleep)
        return repo.getToateComenzile();
    }
}
