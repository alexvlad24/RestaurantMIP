module org.example {
    // 1. Module JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Uneori necesar explicit pentru imagini/ferestre

    // 2. Utilitare (JSON)
    requires com.fasterxml.jackson.databind;

    // 3. Persistență (Baza de Date)
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    // --- LINIA LIPSĂ CRITICĂ PENTRU EXECUTABIL ---
    // Fără asta, jlink nu pune driverul de Postgres în folderul "bin"
    requires org.postgresql.jdbc;

    // 4. Deschideri (Permisiuni pentru Reflection)
    // JavaFX trebuie să vadă controllerele, Hibernate entitățile, Jackson modelele JSON
    opens org.example to javafx.graphics, javafx.fxml, org.hibernate.orm.core, com.fasterxml.jackson.databind;

    // 5. Export
    exports org.example;
}