package org.example;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantController {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final RestaurantModel model;
    private final RestaurantView view;

    public RestaurantController(RestaurantModel model, RestaurantView view) {
        this.model = model;
        this.view = view;

        // PORNIRE: Setăm ecranul de login și îi activăm butoanele
        setupLoginScreen();
    }

    // ============================================================
    // 0. METODA CENTRALĂ DE LOGIN (REPARĂ PROBLEMA TA)
    // ============================================================
    private void setupLoginScreen() {
        // 1. Desenăm butoanele grafic (în View)
        view.initLoginScreen();

        // 2. LEGAM IMEDIAT ACȚIUNILE DE NOILE BUTOANE (în Controller)

        // Buton Login Staff
        view.getBtnLogin().setOnAction(e -> {
            String user = view.getUserField().getText();
            String pass = view.getPassField().getText();

            if (model.login(user, pass)) {
                User u = model.getCurrentUser();
                view.getLoginMessageLabel().setText("");

                if ("MANAGER".equals(u.getRol())) {
                    view.buildManagerScreen(u.getUsername());
                    initManagerBindings();
                } else {
                    view.buildWaiterScreen(u.getUsername());
                    initWaiterBindings();
                }
            } else {
                view.getLoginMessageLabel().setText("User sau parolă incorectă!");
            }
        });

        // Buton Guest
        view.getBtnGuestMode().setOnAction(e -> {
            view.buildGuestScreen();
            initGuestBindings();
        });
    }

    // ============================================================
    // 1. LOGICA GUEST (CLIENT)
    // ============================================================
    private void initGuestBindings() {
        updateGuestList();

        view.getGuestSearchField().textProperty().addListener(o -> updateGuestList());
        view.getChkVeg().selectedProperty().addListener(o -> updateGuestList());
        view.getChkDrinks().selectedProperty().addListener(o -> updateGuestList());
        view.getPriceSlider().valueProperty().addListener(o -> updateGuestList());

        view.getGuestProdList().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                view.getGuestDetailsArea().setText(newVal.toString());
            }
        });

        // REPARATIE LOGOUT GUEST: Apelăm setupLoginScreen() în loc de doar view.init...
        view.getBtnBackFromGuest().setOnAction(e -> setupLoginScreen());
    }

    private void updateGuestList() {
        view.getGuestProdList().getItems().setAll(
            model.filtreazaProduse(
                view.getGuestSearchField().getText(),
                view.getChkVeg().isSelected(),
                view.getChkDrinks().isSelected(),
                view.getPriceSlider().getValue()
            )
        );
    }

    // ============================================================
    // 2. LOGICA OSPATAR
    // ============================================================
    private void initWaiterBindings() {
        // ---------------------------------------------------------
        // 1. POPULARE LISTE (LEGAREA DATELOR)
        // ---------------------------------------------------------
        view.getWaiterMenuList().setItems(model.getToateProdusele());
        view.getWaiterCartList().setItems(model.getCos());
        view.getWaiterHistoryList().setItems(model.getIstoric());

        // ---------------------------------------------------------
        // 2. ACȚIUNI COȘ DE CUMPĂRĂTURI
        // ---------------------------------------------------------

        // A. Adăugare în coș (din Meniu)
        view.getBtnAddCart().setOnAction(e -> {
            Produs p = view.getWaiterMenuList().getSelectionModel().getSelectedItem();
            if (p != null) {
                model.adaugaInCos(p);
            } else {
                showAlert("Info", "Selectează un produs din meniu pentru a-l adăuga.");
            }
        });

        // B. Ștergere element selectat din coș (NOU - Cerința "șterge linii")
        view.getBtnRemoveCartItem().setOnAction(e -> {
            Produs selectat = view.getWaiterCartList().getSelectionModel().getSelectedItem();
            if (selectat != null) {
                model.getCos().remove(selectat);
                // Bonul se va recalcula automat datorită listener-ului de la pasul 6
            } else {
                showAlert("Atenție", "Selectează un produs din lista coșului pentru a-l șterge.");
            }
        });

        // C. Golire completă coș
        view.getBtnClearCart().setOnAction(e -> model.getCos().clear());

        // ---------------------------------------------------------
        // 3. SELECȚIE MASĂ
        // ---------------------------------------------------------
        view.getTableSelector().setOnAction(e -> {
            if (view.getTableSelector().getValue() != null) {
                model.setMasa(view.getTableSelector().getValue());
            }
        });

        // ---------------------------------------------------------
        // 4. FINALIZARE COMANDĂ
        // ---------------------------------------------------------
        view.getBtnPlaceOrder().setOnAction(e -> {
            // Validări
            if (model.getMasa() == 0) {
                showAlert("Eroare", "Trebuie să selectezi o masă!");
                return;
            }
            if (model.getCos().isEmpty()) {
                showAlert("Eroare", "Coșul este gol!");
                return;
            }

            // Execuție
            model.plaseazaComanda();

            // Feedback
            view.getWaiterBonArea().setText("COMANDA A FOST TRIMISĂ LA BUCĂTĂRIE!\nMasa a fost eliberată.");
            showAlert("Succes", "Comanda a fost înregistrată cu succes!");
        });

        // ---------------------------------------------------------
        // 5. LOGOUT
        // ---------------------------------------------------------
        view.getBtnLogoutWaiter().setOnAction(e -> {
            model.logout();
            setupLoginScreen(); // Ne întoarcem la login și re-legăm butoanele
        });

        // ---------------------------------------------------------
        // 6. CALCUL AUTOMAT BON (OFERTE)
        // ---------------------------------------------------------
        // Ascultă orice modificare în lista coșului (adăugare sau ștergere)
        model.getCos().addListener((ListChangeListener<Produs>) c -> {
            String textBon = model.calculeazaTotalBon();
            view.getWaiterBonArea().setText(textBon);
        });

        // ---------------------------------------------------------
        // 7. AFIȘARE DETALII PRODUS (NOU - Panoul de Sus din Split)
        // ---------------------------------------------------------
        // Când ospătarul dă click pe un produs din Meniu, afișăm detaliile sus
        view.getWaiterMenuList().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String info = "Produs: " + newVal.getNume() + "\n" +
                              "Categorie: " + newVal.getCategorie() + "\n" +
                              "Preț unitar: " + newVal.getPret() + " RON\n";

                // Detalii specifice în funcție de tip
                if (newVal instanceof Food) {
                    info += "Calorii: " + ((Food)newVal).getCalorii() + " kcal\n" +
                            "Vegan: " + (((Food)newVal).isVegan() ? "DA" : "NU");
                } else if (newVal instanceof Drink) {
                    info += "Volum: " + ((Drink)newVal).getVolum() + " L";
                }

                view.getWaiterDetailsArea().setText(info);
            }
        });
    }

    // ============================================================
    // 3. LOGICA MANAGER
    // ============================================================
    private void initManagerBindings() {
        // ============================================================
        // A. GESTIUNE PERSONAL (TAB 1)
        // ============================================================

        // 1. Populare inițială listă angajați
        model.refreshOspatari();
        view.getStaffList().setItems(model.getOspatari());

        // 2. LOGICA DE ANGAJARE (ADD)
        view.getBtnAddStaff().setOnAction(e -> {
            String user = view.getTxtStaffUser().getText();
            String pass = view.getTxtStaffPass().getText();

            if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Eroare", "Completează utilizatorul și parola!");
                return;
            }

            try {
                model.angajeazaOspatar(user, pass);
                view.getTxtStaffUser().clear();
                view.getTxtStaffPass().clear();
                showAlert("Succes", "Ospătarul " + user + " a fost angajat!");
            } catch (Exception ex) {
                showAlert("Eroare", "Utilizatorul există deja sau date invalide.");
            }
        });

        // 3. LOGICA DE CONCEDIERE (DELETE) - Cu dublă confirmare
        view.getBtnFireStaff().setOnAction(e -> {
            User u = view.getStaffList().getSelectionModel().getSelectedItem();
            if (u == null) {
                showAlert("Atenție", "Selectează un angajat!");
                return;
            }

            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION,
                    "Ești sigur că vrei să îl concediezi pe " + u.getUsername() + "?", ButtonType.YES, ButtonType.NO);
            alert1.showAndWait();

            if (alert1.getResult() == ButtonType.YES) {
                Alert alert2 = new Alert(Alert.AlertType.WARNING,
                        "ATENȚIE! Se vor șterge și toate comenzile acestui ospătar!\nContinui?", ButtonType.YES, ButtonType.CANCEL);
                alert2.showAndWait();

                if (alert2.getResult() == ButtonType.YES) {
                    model.stergeOspatar(u);
                    showAlert("Info", "Angajat șters.");
                }
            }
        });

        // ============================================================
        // B. CONTROL OFERTE (TAB 2)
        // ============================================================
        view.getChkHappyHour().selectedProperty().addListener((o, old, val) -> model.setHappyHour(val));
        view.getChkMealDeal().selectedProperty().addListener((o, old, val) -> model.setMealDeal(val));
        view.getChkPartyPack().selectedProperty().addListener((o, old, val) -> model.setPartyPack(val));

        // ============================================================
        // C. GESTIUNE MENIU (TAB 3 - ADAPTAT PENTRU TINE)
        // ============================================================

        // 0. Interfața Dinamică
        view.getProductTypeSelector().setOnAction(e -> {
            String tip = view.getProductTypeSelector().getValue();
            if ("Mâncare".equals(tip)) {
                view.getTxtExtraInt().setPromptText("Gramaj (g) / Calorii");
                view.getChkExtraBool().setVisible(true); // Arătăm checkbox "Vegetarian"
                view.getCatSelector().setDisable(false);
            } else {
                view.getTxtExtraInt().setPromptText("Volum (L)");
                view.getChkExtraBool().setVisible(false); // Ascundem checkbox (Drink nu are boolean la tine)
                view.getCatSelector().setDisable(false);  // Categoria rămâne activă (Drink are categorie la tine)
            }
        });

        // 1. Populare Listă
        model.refreshProduse();
        view.getManagerProdList().setItems(model.getToateProdusele());

        // 2. Selectare Produs (Umple câmpurile)
        view.getManagerProdList().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                view.getNewProdName().setText(newVal.getNume());
                view.getNewProdPrice().setText(String.valueOf(newVal.getPret()));

                if (newVal instanceof Food) {
                    view.getProductTypeSelector().setValue("Mâncare");
                    view.getTxtExtraInt().setText(String.valueOf(((Food) newVal).getCalorii()));
                    view.getChkExtraBool().setSelected(((Food) newVal).isVegan());
                    view.getCatSelector().setValue(((Food) newVal).getCategorie());
                } else if (newVal instanceof Drink) {
                    view.getProductTypeSelector().setValue("Băutură");
                    // Cast la Drink pentru a lua volumul
                    view.getTxtExtraInt().setText(String.valueOf(((Drink) newVal).getVolum()));
                    // Nu setăm checkbox-ul
                }
            }
        });

        // 3. ADĂUGARE PRODUS (CREATE) - LOGICA TĂ SPECIALĂ
        view.getBtnAddProd().setOnAction(e -> {
            try {
                String nume = view.getNewProdName().getText();
                double pret = Double.parseDouble(view.getNewProdPrice().getText());
                String tip = view.getProductTypeSelector().getValue();
                Categorie cat = view.getCatSelector().getValue(); // Categoria e necesară pt ambele

                // Citim valoarea ca double (ca să acopere și volumul de la Drink)
                double valoareNumerica = Double.parseDouble(view.getTxtExtraInt().getText());

                if (nume.isEmpty() || pret <= 0) throw new Exception();

                if ("Mâncare".equals(tip)) {
                    boolean isVeg = view.getChkExtraBool().isSelected();
                    // Food: castăm valoarea la int pt calorii
                    model.adaugaProdus(new Food(nume, pret, (int)valoareNumerica, isVeg, cat));
                } else {
                    // Drink: folosim valoarea double pt volum, fără boolean
                    model.adaugaProdus(new Drink(nume, pret, valoareNumerica, cat));
                }

                // Reset
                view.getNewProdName().clear();
                view.getNewProdPrice().clear();
                view.getTxtExtraInt().clear();
                view.getChkExtraBool().setSelected(false);
                showAlert("Succes", "Produs adăugat!");

            } catch (Exception ex) {
                showAlert("Eroare", "Date invalide! Verifică numerele.");
            }
        });

        // 4. ACTUALIZARE (UPDATE)
        view.getBtnUpdateProd().setOnAction(e -> {
            Produs selectat = view.getManagerProdList().getSelectionModel().getSelectedItem();
            if (selectat != null) {
                try {
                    String nume = view.getNewProdName().getText();
                    double pret = Double.parseDouble(view.getNewProdPrice().getText());
                    model.actualizeazaProdus(selectat, nume, pret);

                    view.getNewProdName().clear();
                    view.getNewProdPrice().clear();
                    showAlert("Succes", "Produs actualizat!");
                } catch (Exception ex) {
                    showAlert("Eroare", "Preț invalid.");
                }
            }
        });

        // 5. ȘTERGERE (DELETE)
        view.getBtnDeleteProd().setOnAction(e -> {
            Produs selectat = view.getManagerProdList().getSelectionModel().getSelectedItem();
            if (selectat != null) {
                model.stergeProdus(selectat);
            }
        });

        // 6. EXPORT / IMPORT
        view.getBtnExportJson().setOnAction(e -> {
            try {
                model.exportaDate();
                showAlert("Succes", "Export JSON reușit!");
            } catch (Exception ex) { showAlert("Eroare", ex.getMessage()); }
        });
        view.getBtnImportJson().setOnAction(e -> {
            model.importaDate();
            showAlert("Info", "Import declanșat.");
        });

        // ============================================================
        // D. ISTORIC GLOBAL (TAB 4)
        // ============================================================
        model.refreshIstoricGlobal();
        view.getGlobalHistoryList().setItems(model.getIstoric());
        view.getBtnRefreshHistory().setOnAction(e -> model.refreshIstoricGlobal());

        // ============================================================
        // E. LOGOUT
        // ============================================================
        view.getBtnLogoutManager().setOnAction(e -> {
            model.logout();
            setupLoginScreen();
        });

        view.getBtnRefreshHistory().setOnAction(e -> {
            // 1. Cream task-ul (Instanțiem clasa nouă)
            // Modelul conține repo-ul, deci îl luăm de acolo sau îl accesăm direct dacă e disponibil
            // Presupunând că în Model ai acces la repo, sau poți face un getter în Model:
            // Varianta simplă: folosim repo-ul din model dacă e public, sau facem task-ul să ceară Model.

            // Putem modifica constructorul Task-ului să accepte Modelul dacă e mai ușor,
            // dar aici presupunem că poți lua repo-ul din model:
            Task<List<Comanda>> task = new IncarcareIstoricTask(model.getRepo());

            // 2. Legăm UI-ul de proprietățile task-ului (BINDING - Soluția Profi)
            // Spinner-ul apare automat DOAR când task-ul rulează
            view.getSpinner().visibleProperty().bind(task.runningProperty());

            // Opțional: Blocăm butonul cât timp rulează task-ul
            view.getBtnRefreshHistory().disableProperty().bind(task.runningProperty());

            // Opțional: Dacă ai un Label de status, poți lega mesajul:
            // view.getStatusLabel().textProperty().bind(task.messageProperty());

            // 3. Definim ce facem când termină cu succes
            task.setOnSucceeded(ev -> {
                // getValue() returnează rezultatul metodei call() adică List<Comanda>
                List<Comanda> rezultat = task.getValue();

                // Actualizăm Modelul și Interfața
                model.setIstoric(rezultat); // Dacă ai setter
                view.getGlobalHistoryList().getItems().setAll(rezultat);

                // Binding-ul se ocupă singur să ascundă spinner-ul! Nu trebuie setVisible(false) manual.
            });

            // 4. Definim ce facem în caz de eroare
            task.setOnFailed(ev -> {
                Throwable ex = task.getException();
                ex.printStackTrace();
                showAlert("Eroare", "Nu s-a putut încărca istoricul: " + ex.getMessage());
            });

            // 5. Pornim execuția pe un alt fir
            executor.execute(task);
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}