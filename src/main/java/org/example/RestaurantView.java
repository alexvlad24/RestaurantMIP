package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RestaurantView extends BorderPane {

    // --- ELEMENTE LOGIN ---
    private TextField userField;
    private PasswordField passField;
    private Button btnLogin;
    private Button btnGuestMode;
    private Label loginMessageLabel;
    private TextArea waiterDetailsArea;

    // --- ELEMENTE GUEST (CLIENT) ---
    private TextField guestSearchField;
    private CheckBox chkVeg;
    private CheckBox chkDrinks;
    private Slider priceSlider;
    private ListView<Produs> guestProdList;
    private TextArea guestDetailsArea;
    private Button btnBackFromGuest;

    // --- ELEMENTE OSPATAR (WAITER) ---
    private ComboBox<Integer> tableSelector; // Selectare masa (1-6)
    private ListView<Produs> waiterMenuList;
    private ListView<Produs> waiterCartList;
    private TextArea waiterBonArea; // Afiseaza calculul reducerilor
    private Button btnAddCart;
    private Button btnClearCart;
    private Button btnPlaceOrder;
    private Button btnLogoutWaiter;
    private ListView<Comanda> waiterHistoryList; // Istoric personal

    // --- ELEMENTE MANAGER ---
    private ListView<User> staffList;
    private Button btnFireStaff;
    private Button btnAddStaff;
    private TextField txtStaffUser;
    private PasswordField txtStaffPass;
    private ListView<Comanda> globalHistoryList;
    private Button btnRefreshHistory;

    // Controale Oferte
    private CheckBox chkHappyHour;
    private CheckBox chkMealDeal;
    private CheckBox chkPartyPack;

    // CRUD Meniu
    private TextField newProdName;
    private TextField newProdPrice;
    private Button btnAddProd;
    private Button btnDeleteProd;
    private Button btnExportJson;
    private Button btnLogoutManager;

    private Button btnUpdateProd;
    private Button btnImportJson;
    private ListView<Produs> managerProdList;

    private Button btnRemoveCartItem;

    private ComboBox<String> typeSelector;   // Selectează Mâncare sau Băutură
    private TextField txtExtraInt;           // Va fi Gramaj (pt mâncare) sau Volum (pt băutură)
    private CheckBox chkExtraBool;           // Va fi Vegetarian (pt mâncare) sau Alcool (pt băutură)
    private ComboBox<Categorie> catSelector;

    private ProgressIndicator spinner;

    public RestaurantView() {
        initLoginScreen();
    }

    // ==========================================
    // 1. ECRAN LOGIN
    // ==========================================
    public void initLoginScreen() {
        this.setTop(null);
        this.setLeft(null);
        this.setRight(null);
        this.setBottom(null);

        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(40));
        loginBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        loginBox.setMaxWidth(350);
        loginBox.setMaxHeight(400);

        Label title = new Label("Restaurant Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        userField = new TextField();
        userField.setPromptText("Utilizator");

        passField = new PasswordField();
        passField.setPromptText("Parola");

        btnLogin = new Button("Autentificare Staff");
        btnLogin.setStyle("-fx-background-color: #2a9df4; -fx-text-fill: white;");
        btnLogin.setPrefWidth(200);

        btnGuestMode = new Button("Intră ca Client (Guest)");
        btnGuestMode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnGuestMode.setPrefWidth(200);

        loginMessageLabel = new Label();
        loginMessageLabel.setTextFill(Color.RED);

        loginBox.getChildren().addAll(title, new Separator(), userField, passField, btnLogin, new Label("--- sau ---"), btnGuestMode, loginMessageLabel);

        StackPane rootCenter = new StackPane(loginBox);
        rootCenter.setStyle("-fx-background-color: #e0e0e0;");
        this.setCenter(rootCenter);
    }

    // ==========================================
    // 2. ECRAN GUEST (CLIENT)
    // ==========================================
    public void buildGuestScreen() {
        // -- Zona Filtre (Stanga) --
        VBox filters = new VBox(10);
        filters.setPadding(new Insets(10));
        filters.setPrefWidth(200);
        filters.setStyle("-fx-background-color: #ffffff;");

        Label lblFilter = new Label("Filtrează Meniul");
        lblFilter.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        guestSearchField = new TextField();
        guestSearchField.setPromptText("Cauta produs...");

        chkVeg = new CheckBox("Doar Vegetarian");
        chkDrinks = new CheckBox("Doar Băuturi");

        Label lblPrice = new Label("Preț Maxim: 200 RON");
        priceSlider = new Slider(0, 200, 200);
        priceSlider.setShowTickLabels(true);
        priceSlider.setShowTickMarks(true);

        // Update label cand slider misca (logica UI simpla, permisa in View)
        priceSlider.valueProperty().addListener((obs, old, val) ->
            lblPrice.setText("Preț Maxim: " + String.format("%.0f", val.doubleValue()) + " RON"));

        filters.getChildren().addAll(lblFilter, guestSearchField, chkVeg, chkDrinks, new Separator(), lblPrice, priceSlider);

        // -- Zona Lista + Detalii (Centru - SplitPane) --
        guestProdList = new ListView<>();
        guestDetailsArea = new TextArea();
        guestDetailsArea.setEditable(false);
        guestDetailsArea.setWrapText(true);
        guestDetailsArea.setText("Selectează un produs pentru detalii...");

        SplitPane split = new SplitPane();
        split.getItems().addAll(guestProdList, guestDetailsArea);
        split.setDividerPositions(0.6); // 60% lista, 40% detalii

        // -- Top Bar --
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #333;");
        btnBackFromGuest = new Button("<- Înapoi la Login");
        Label guestTitle = new Label("Meniu Interactiv Clienți");
        guestTitle.setTextFill(Color.WHITE);
        guestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        topBar.getChildren().addAll(btnBackFromGuest, guestTitle);

        this.setTop(topBar);
        this.setLeft(filters);
        this.setCenter(split);
        this.setRight(null);
        this.setBottom(null);
    }

    // ==========================================
    // 3. ECRAN OSPATAR
    // ==========================================
    // ==========================================
// 3. ECRAN OSPATAR (LAYOUT ACTUALIZAT CONFORM CERINȚEI)
// ==========================================
// ==========================================
    // 3. ECRAN OSPATAR (FINAL & COMPLET)
    // ==========================================
    public void buildWaiterScreen(String username) {
        // A. TOP BAR (Info User, Selector Masă, Logout)
        HBox top = new HBox(15);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-width: 0 0 2 0;");

        Label lblUser = new Label("Ospătar conectat: " + username);
        lblUser.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tableSelector = new ComboBox<>();
        tableSelector.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        tableSelector.setPromptText("Selectează Masa");

        btnLogoutWaiter = new Button("Logout");

        top.getChildren().addAll(lblUser, new Separator(javafx.geometry.Orientation.VERTICAL), new Label("Masa Activă:"), tableSelector, new Region(), btnLogoutWaiter);
        HBox.setHgrow(top.getChildren().get(4), Priority.ALWAYS); // Spacer pentru a împinge logout-ul în dreapta

        // B. CENTRU: MENIUL DE PRODUSE (VECHI & BUN)
        VBox centerBox = new VBox(5);
        centerBox.setPadding(new Insets(10));

        waiterMenuList = new ListView<>();
        btnAddCart = new Button("Adaugă în Comandă >>");
        btnAddCart.setMaxWidth(Double.MAX_VALUE);
        btnAddCart.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        centerBox.getChildren().addAll(new Label("MENIU PRINCIPAL"), waiterMenuList, btnAddCart);

        // C. PANOUL LATERAL (SPLIT PANE) - CONFIRMAT

        // C1. Partea de SUS: Detalii Produs Selectat (Read-Only)
        VBox detailsBox = new VBox(5);
        detailsBox.setPadding(new Insets(10));

        Label lblDet = new Label("Detalii Produs:");
        lblDet.setStyle("-fx-font-weight: bold");

        waiterDetailsArea = new TextArea();
        waiterDetailsArea.setEditable(false);
        waiterDetailsArea.setWrapText(true);
        waiterDetailsArea.setPromptText("Selectează un produs din meniu pentru detalii...");

        detailsBox.getChildren().addAll(lblDet, waiterDetailsArea);

        // C2. Partea de JOS: Coșul și Bonul (CU BUTONUL NOU DE ȘTERGERE)
        VBox cartBox = new VBox(5);
        cartBox.setPadding(new Insets(10));

        waiterCartList = new ListView<>();
        waiterBonArea = new TextArea();
        waiterBonArea.setEditable(false);
        waiterBonArea.setStyle("-fx-font-family: 'Monospaced'; -fx-control-inner-background: #fff3e0;");
        waiterBonArea.setPrefHeight(100);

        // Butoane Acțiuni Coș
        HBox actions = new HBox(5);

        // --- AICI ESTE BUTONUL NOU ---
        btnRemoveCartItem = new Button("Șterge Selectat");
        btnRemoveCartItem.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");

        btnClearCart = new Button("Golește Tot");

        btnPlaceOrder = new Button("FINALIZEAZĂ");
        btnPlaceOrder.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPlaceOrder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnPlaceOrder, Priority.ALWAYS); // Butonul de finalizare ocupă restul spațiului

        actions.getChildren().addAll(btnRemoveCartItem, btnClearCart, btnPlaceOrder);

        cartBox.getChildren().addAll(new Label("Coș Curent:"), waiterCartList, new Label("Previzualizare Bon:"), waiterBonArea, actions);

        // Asamblare SplitPane Vertical
        SplitPane sideSplit = new SplitPane();
        sideSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        sideSplit.getItems().addAll(detailsBox, cartBox);
        sideSplit.setDividerPositions(0.3); // 30% sus, 70% jos

        // D. TABS: COMANDĂ vs ISTORIC (VECHI & BUN)
        TabPane mainTabs = new TabPane();

        // Tab 1: Comanda Curentă
        BorderPane orderPane = new BorderPane();
        orderPane.setCenter(centerBox);
        orderPane.setRight(sideSplit);
        Tab tabOrder = new Tab("Comandă Nouă", orderPane);
        tabOrder.setClosable(false);

        // Tab 2: Istoric Propriu
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(15));
        waiterHistoryList = new ListView<>();
        historyBox.getChildren().addAll(new Label("Istoricul Comenzilor Tale (Sesiune Curentă):"), waiterHistoryList);
        Tab tabHistory = new Tab("Istoric Propriu", historyBox);
        tabHistory.setClosable(false);

        mainTabs.getTabs().addAll(tabOrder, tabHistory);

        // Finalizare Layout
        this.setTop(top);
        this.setCenter(mainTabs);
        this.setLeft(null);
        this.setRight(null);
    }

    public void buildManagerScreen(String username) {
        // A. TOP BAR
        HBox top = new HBox(15);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-width: 0 0 2 0;");

        Label lblUser = new Label("Manager conectat: " + username);
        lblUser.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnLogoutManager = new Button("Logout");
        top.getChildren().addAll(lblUser, new Region(), btnLogoutManager);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);

        // B. TAB-URI
        TabPane tabs = new TabPane();

        // --------------------------------------------------------
        // TAB 1: GESTIUNE PERSONAL
        // --------------------------------------------------------
        VBox staffBox = new VBox(10);
        staffBox.setPadding(new Insets(15));

        staffList = new ListView<>();
        staffList.setPrefHeight(200);

        btnFireStaff = new Button("Concediază Ospătar Selectat");
        btnFireStaff.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red; -fx-font-weight: bold;");
        btnFireStaff.setMaxWidth(Double.MAX_VALUE);

        GridPane hireGrid = new GridPane();
        hireGrid.setHgap(10); hireGrid.setVgap(10);
        hireGrid.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        txtStaffUser = new TextField(); txtStaffUser.setPromptText("User Nou");
        txtStaffPass = new PasswordField(); txtStaffPass.setPromptText("Parolă");
        btnAddStaff = new Button("Angajează");
        btnAddStaff.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        hireGrid.add(new Label("ANGAJARE:"), 0, 0);
        hireGrid.add(new Label("User:"), 0, 1); hireGrid.add(txtStaffUser, 1, 1);
        hireGrid.add(new Label("Pass:"), 0, 2); hireGrid.add(txtStaffPass, 1, 2);
        hireGrid.add(btnAddStaff, 1, 3);

        staffBox.getChildren().addAll(new Label("Echipa:"), staffList, btnFireStaff, new Separator(), hireGrid);
        Tab tabStaff = new Tab("Personal", staffBox);
        tabStaff.setClosable(false);

        // --------------------------------------------------------
        // TAB 2: OFERTE
        // --------------------------------------------------------
        VBox offersBox = new VBox(15);
        offersBox.setPadding(new Insets(20));
        chkHappyHour = new CheckBox("Happy Hour (-50% la a 2-a băutură)");
        chkMealDeal = new CheckBox("Meal Deal (Pizza + Desert -25%)");
        chkPartyPack = new CheckBox("Party Pack (4 Pizza -> 1 Gratis)");
        offersBox.getChildren().addAll(new Label("Strategii Active:"), new Separator(), chkHappyHour, chkMealDeal, chkPartyPack);
        Tab tabOffers = new Tab("Oferte", offersBox);
        tabOffers.setClosable(false);

        // --------------------------------------------------------
        // TAB 3: MENIU (CU POLIMORFISM FOOD/DRINK)
        // --------------------------------------------------------
        VBox menuBox = new VBox(10);
        menuBox.setPadding(new Insets(15));

        managerProdList = new ListView<>();
        managerProdList.setPrefHeight(200);

        // Formular Dinamic
        GridPane formProd = new GridPane();
        formProd.setHgap(10); formProd.setVgap(10);
        formProd.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-radius: 5;");

        // 1. Selector Tip
        typeSelector = new ComboBox<>();
        typeSelector.getItems().addAll("Mâncare", "Băutură");
        typeSelector.getSelectionModel().selectFirst();

        // 2. Câmpuri Comune
        newProdName = new TextField(); newProdName.setPromptText("Nume Produs");
        newProdPrice = new TextField(); newProdPrice.setPromptText("Preț");

        // 3. Câmpuri Specifice (Se schimbă sensul lor din Controller)
        txtExtraInt = new TextField();
        txtExtraInt.setPromptText("Gramaj / Calorii"); // Default

        chkExtraBool = new CheckBox("Este Vegetarian?"); // Default

        catSelector = new ComboBox<>();
        catSelector.getItems().setAll(Categorie.values());
        catSelector.getSelectionModel().selectFirst();
        catSelector.setPromptText("Categorie");

        // Butoane CRUD
        btnAddProd = new Button("Adaugă");
        btnAddProd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnUpdateProd = new Button("Update");
        btnUpdateProd.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        // Așezare în Grid
        formProd.add(new Label("Tip Produs:"), 0, 0); formProd.add(typeSelector, 1, 0);
        formProd.add(new Label("Nume:"), 0, 1); formProd.add(newProdName, 1, 1);
        formProd.add(new Label("Preț (RON):"), 0, 2); formProd.add(newProdPrice, 1, 2);

        formProd.add(new Label("Detalii:"), 0, 3);
        // Punem gramajul și categoria pe aceeași linie pentru a economisi spațiu
        HBox detailsBox = new HBox(10, txtExtraInt, catSelector);
        formProd.add(detailsBox, 1, 3);

        formProd.add(chkExtraBool, 1, 4);

        HBox actionBox = new HBox(10, btnAddProd, btnUpdateProd);
        formProd.add(actionBox, 1, 5);

        // Butoane Fișiere
        HBox fileActions = new HBox(10);
        btnDeleteProd = new Button("Șterge Selectat");
        btnDeleteProd.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnExportJson = new Button("Export JSON");
        btnImportJson = new Button("Import JSON");
        fileActions.getChildren().addAll(btnDeleteProd, new Separator(Orientation.VERTICAL), btnExportJson, btnImportJson);

        menuBox.getChildren().addAll(new Label("Catalog Produse:"), managerProdList, formProd, new Separator(), fileActions);
        Tab tabMenu = new Tab("Meniu", menuBox);
        tabMenu.setClosable(false);

        // --------------------------------------------------------
        // TAB 4: ISTORIC GLOBAL
        // --------------------------------------------------------
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(15));
        globalHistoryList = new ListView<>();
        btnRefreshHistory = new Button("Reîmprospătează Istoricul");
        historyBox.getChildren().addAll(new Label("Vânzări Totale:"), btnRefreshHistory, globalHistoryList);
        Tab tabHistory = new Tab("Istoric", historyBox);
        tabHistory.setClosable(false);

        // FINAL
        tabs.getTabs().addAll(tabStaff, tabOffers, tabMenu, tabHistory);

        this.setTop(top);
        this.setCenter(tabs);

        spinner = new ProgressIndicator();
        spinner.setVisible(false); // Ascunsă inițial
        spinner.setMaxHeight(25);  // O facem micuță

        // O adăugăm în bara de sus (top), de exemplu după butonul de Logout sau lângă titlu
        top.getChildren().add(spinner);
    }

    // ==========================================
    // GETTERS (Pentru Controller)
    // ==========================================

    // Login
    public TextField getUserField() { return userField; }
    public PasswordField getPassField() { return passField; }
    public Button getBtnLogin() { return btnLogin; }
    public Button getBtnGuestMode() { return btnGuestMode; }
    public Label getLoginMessageLabel() { return loginMessageLabel; }

    // Guest
    public TextField getGuestSearchField() { return guestSearchField; }
    public CheckBox getChkVeg() { return chkVeg; }
    public CheckBox getChkDrinks() { return chkDrinks; }
    public Slider getPriceSlider() { return priceSlider; }
    public ListView<Produs> getGuestProdList() { return guestProdList; }
    public TextArea getGuestDetailsArea() { return guestDetailsArea; }
    public Button getBtnBackFromGuest() { return btnBackFromGuest; }

    // Waiter
    public ComboBox<Integer> getTableSelector() { return tableSelector; }
    public ListView<Produs> getWaiterMenuList() { return waiterMenuList; }
    public ListView<Produs> getWaiterCartList() { return waiterCartList; }
    public TextArea getWaiterBonArea() { return waiterBonArea; }
    public Button getBtnAddCart() { return btnAddCart; }
    public Button getBtnClearCart() { return btnClearCart; }
    public Button getBtnPlaceOrder() { return btnPlaceOrder; }
    public Button getBtnLogoutWaiter() { return btnLogoutWaiter; }
    public ListView<Comanda> getWaiterHistoryList() { return waiterHistoryList; }

    // Manager
    public ListView<User> getStaffList() { return staffList; }
    public Button getBtnFireStaff() { return btnFireStaff; }
    public Button getBtnAddStaff() { return btnAddStaff; }
    public TextField getTxtStaffUser() { return txtStaffUser; }
    public PasswordField getTxtStaffPass() { return txtStaffPass; }
    public CheckBox getChkHappyHour() { return chkHappyHour; }
    public CheckBox getChkMealDeal() { return chkMealDeal; }
    public CheckBox getChkPartyPack() { return chkPartyPack; }
    public TextField getNewProdName() { return newProdName; }
    public TextField getNewProdPrice() { return newProdPrice; }
    public Button getBtnAddProd() { return btnAddProd; }
    public Button getBtnDeleteProd() { return btnDeleteProd; }
    public Button getBtnExportJson() { return btnExportJson; }
    public Button getBtnLogoutManager() { return btnLogoutManager; }
    public TextArea getWaiterDetailsArea() { return waiterDetailsArea; }
    public ListView<Produs> getManagerProdList() { return managerProdList; }
    public Button getBtnUpdateProd() { return btnUpdateProd; }
    public Button getBtnImportJson() { return btnImportJson; }
    public Button getBtnRemoveCartItem() { return btnRemoveCartItem; }
    public ListView<Comanda> getGlobalHistoryList() { return globalHistoryList; }
    public Button getBtnRefreshHistory() { return btnRefreshHistory; }
public ComboBox<String> getProductTypeSelector() {
        return typeSelector;
    }    public TextField getTxtExtraInt() { return txtExtraInt; }
    public CheckBox getChkExtraBool() { return chkExtraBool; }
    public ComboBox<Categorie> getCatSelector() { return catSelector; }
    public ProgressIndicator getSpinner() {
        return spinner;
    }
}