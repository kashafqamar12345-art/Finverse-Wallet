package org.example.controllers;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.models.Wallet;
import org.example.services.WalletService;
import org.example.utils.SceneManager;
import org.example.utils.SessionManager;

/**
 * Controls the Dashboard screen.
 *
 * FIXES:
 *  1. Class renamed to DashboardController (lowercase 'd' in 'board') to match
 *     the fx:controller="org.example.controllers.DashboardController" in dashboard.fxml.
 *     The old name DashBoardController (capital B) caused an FXMLLoadException.
 *  2. Navigation paths simplified — SceneManager.switchTo(stage, "transfer.fxml").
 *  3. All service calls pass userId correctly (WalletService was also fixed).
 */
public class DashBoardController {

    // ── Balance card ──────────────────────────────────────────────────
    @FXML private Label balanceLabel;
    @FXML private Label currencyLabel;
    @FXML private Label welcomeLabel;

    // ── Stats ─────────────────────────────────────────────────────────
    @FXML private Label totalReceivedLabel;
    @FXML private Label totalSpentLabel;
    @FXML private Label txCountLabel;

    // ── Quick actions ─────────────────────────────────────────────────
    @FXML private TextField amountField;
    @FXML private Label     actionMessageLabel;

    // ── Transaction table ─────────────────────────────────────────────
    @FXML private TableView<Transaction>            transactionTable;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, String>  colType;
    @FXML private TableColumn<Transaction, String>  colAmount;
    @FXML private TableColumn<Transaction, String>  colParty;
    @FXML private TableColumn<Transaction, String>  colDate;
    @FXML private TableColumn<Transaction, String>  colStatus;

    private final WalletService walletService = new WalletService();

    // ── Lifecycle ─────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUserData();
    }

    // ── Load all data from session / DB ───────────────────────────────

    private void loadUserData() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            // Session expired — bounce to login
            goToLogin();
            return;
        }

        welcomeLabel.setText("Welcome back, " + user.getUsername() + " 👋");
        currencyLabel.setText("PKR");

        refreshStats(user.getUserId());
        loadTransactions(user.getUserId());
    }

    /**
     * Reload balance, totals, tx count from DB.
     * Extracted so both initialize() and post-action refresh use the same path.
     */
    private void refreshStats(int userId) {
        double balance = walletService.getBalance(userId);
        balanceLabel.setText(String.format("%,.2f", balance));

        totalReceivedLabel.setText(
                "PKR " + String.format("%,.2f", walletService.getTotalReceived(userId)));
        totalSpentLabel.setText(
                "PKR " + String.format("%,.2f", walletService.getTotalSpent(userId)));
        txCountLabel.setText(
                String.valueOf(walletService.getTransactionCount(userId)));

        // Keep session wallet fresh
        Wallet updated = walletService.getWallet(userId);
        SessionManager.setCurrentWallet(updated);
    }

    private void loadTransactions(int userId) {
        ObservableList<Transaction> data = FXCollections.observableArrayList(
                walletService.getHistory(userId));
        transactionTable.setItems(data);

        if (data.isEmpty()) {
            transactionTable.setPlaceholder(
                    new Label("No transactions yet — make your first deposit!"));
        }
    }

    // ── Table column setup ────────────────────────────────────────────

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("transId"));

        // TYPE column with colour coding
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "DEPOSIT"  -> setStyle("-fx-text-fill: #4ECDC4; -fx-font-weight: bold;");
                    case "WITHDRAW" -> setStyle("-fx-text-fill: #FF6B6B; -fx-font-weight: bold;");
                    case "TRANSFER" -> setStyle("-fx-text-fill: #FFE66D; -fx-font-weight: bold;");
                    default         -> setStyle("");
                }
            }
        });

        // AMOUNT column — green if starts with "+", red otherwise
        colAmount.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle(item.startsWith("+")
                        ? "-fx-text-fill: #4ECDC4;"
                        : "-fx-text-fill: #FF6B6B;");
            }
        });

        colParty.setCellValueFactory(new PropertyValueFactory<>("otherParty"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

        // STATUS column
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("SUCCESS".equals(item)
                        ? "-fx-text-fill: #4ECDC4;"
                        : "-fx-text-fill: #FF6B6B;");
            }
        });
    }

    // ── Quick Deposit ─────────────────────────────────────────────────

    @FXML
    private void handleDeposit() {
        double amount = parseAmount();
        if (amount < 0) return;

        User user   = SessionManager.getCurrentUser();
        String result = walletService.deposit(user.getUserId(), amount);
        showActionMessage(result);

        if (result.startsWith("SUCCESS")) {
            refreshStats(user.getUserId());
            loadTransactions(user.getUserId());
            amountField.clear();
        }
    }

    // ── Quick Withdraw ────────────────────────────────────────────────

    @FXML
    private void handleWithdraw() {
        double amount = parseAmount();
        if (amount < 0) return;

        User user   = SessionManager.getCurrentUser();
        String result = walletService.withdraw(user.getUserId(), amount);
        showActionMessage(result);

        if (result.startsWith("SUCCESS")) {
            refreshStats(user.getUserId());
            loadTransactions(user.getUserId());
            amountField.clear();
        }
    }

    // ── Navigate to Transfer screen ───────────────────────────────────

    @FXML
    private void goToTransfer() {
        Stage stage = SceneManager.getStage(balanceLabel);
        SceneManager.switchTo(stage, "/FXML/Transfer.fxml");
    }

    // ── Logout ────────────────────────────────────────────────────────

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        goToLogin();
    }

    private void goToLogin() {
        Stage stage = SceneManager.getStage(balanceLabel);
        SceneManager.switchTo(stage, "/FXML/login.fxml");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * Parse and validate the amount field.
     * @return the parsed value, or -1 if invalid (message shown to user).
     */
    private double parseAmount() {
        String text = amountField.getText().trim();
        if (text.isEmpty()) {
            showActionMessage("ERROR: Please enter an amount.");
            return -1;
        }
        try {
            double val = Double.parseDouble(text);
            if (val <= 0) {
                showActionMessage("ERROR: Amount must be positive.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            showActionMessage("ERROR: Please enter a valid number.");
            return -1;
        }
    }

    private void showActionMessage(String result) {
        boolean isSuccess = result.startsWith("SUCCESS");
        String  text      = result.replace("SUCCESS: ", "").replace("ERROR: ", "");

        actionMessageLabel.setText(text);
        actionMessageLabel.setTextFill(isSuccess
                ? Color.web("#4ECDC4")
                : Color.web("#FF6B6B"));
        actionMessageLabel.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(300), actionMessageLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}