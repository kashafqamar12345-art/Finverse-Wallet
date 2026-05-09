package org.example.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition; // Added missing import
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.models.User;
import org.example.models.Wallet;
import org.example.services.AuthService;
import org.example.services.WalletService;
import org.example.utils.SceneManager;
import org.example.utils.SessionManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton; // Ensure this matches fx:id="loginButton" in FXML

    private final AuthService authService = new AuthService();
    private final WalletService walletService = new WalletService();

    @FXML
    public void initialize() {
        // Ensure the button is enabled when the view loads
        if (loginButton != null) {
            loginButton.setDisable(false);
        }

        errorLabel.setVisible(false);

        // Accessibility: Allow pressing Enter in either field to login
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    public void handleLogin() { // Changed to public for better FXML visibility
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            shake(usernameField);
            shake(passwordField);
            return;
        }

        User user = authService.login(username, password);

        if (user != null) {
            Wallet wallet = walletService.getWallet(user.getUserId());

            // Using static access (adjust if your SessionManager uses .getInstance())
            SessionManager.setCurrentUser(user);
            SessionManager.setCurrentWallet(wallet);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            // Ensure this path matches your resources folder exactly
            SceneManager.switchTo(stage, "/FXML/Dashboard.fxml");
        } else {
            showError("Invalid username or password.");
            passwordField.clear();
            shake(loginButton); // Shake the button to indicate failure
        }
    }

    @FXML
    public void goToRegister() { // Changed to public
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneManager.switchTo(stage, "/FXML/Register.fxml");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), errorLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}