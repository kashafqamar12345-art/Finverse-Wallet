package org.example.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.models.User;
import org.example.services.WalletService;
import org.example.utils.SceneManager;
import org.example.utils.SessionManager;

public class TransferController {

    @FXML private TextField receiverField;
    @FXML private TextField amountField;
    @FXML private TextArea  noteArea;
    @FXML private Label     balanceLabel;
    @FXML private Label     messageLabel;
    @FXML private Button    transferButton;

    private final WalletService walletService = new WalletService();

    @FXML
    public void initialize() {
        messageLabel.setVisible(false);
        // Show current balance
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            double bal = walletService.getBalance(user.getUserId());
            balanceLabel.setText("Available: PKR " +
                    String.format("%,.2f", bal));
        }
    }

    @FXML
    private void handleTransfer() {
        String receiver = receiverField.getText().trim();
        String amtText  = amountField.getText().trim();

        if (receiver.isEmpty()) {
            showMessage("Please enter the recipient's username.", true);
            return;
        }
        if (amtText.isEmpty()) {
            showMessage("Please enter an amount.", true);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtText);
        } catch (NumberFormatException e) {
            showMessage("Enter a valid number for amount.", true);
            return;
        }

        User user   = SessionManager.getCurrentUser();
        String result = walletService.transfer(user.getUserId(), receiver, amount);

        boolean success = result.startsWith("SUCCESS");
        showMessage(result.replace("SUCCESS: ", "").replace("ERROR: ", ""), !success);

        if (success) {
            receiverField.clear();
            amountField.clear();
            if (noteArea != null) noteArea.clear();

            // Update balance display
            double newBal = walletService.getBalance(user.getUserId());
            balanceLabel.setText("Available: PKR " +
                    String.format("%,.2f", newBal));
        }
    }

    @FXML
    private void goToDashboard() {
        Stage stage = SceneManager.getStage(transferButton);
        SceneManager.switchTo(stage, "/FXML/Dashboard.fxml");
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setTextFill(isError
                ? Color.web("#FF6B6B")
                : Color.web("#4ECDC4"));
        messageLabel.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(300), messageLabel);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }
}