package org.example.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.services.AuthService;
import org.example.utils.SceneManager;

public class RegisterController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         messageLabel;
    @FXML private Button        registerButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {
        String username  = usernameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();

        String result = String.valueOf(authService.register(username, email, password));

        if ("SUCCESS".equals(result)) {
            showMessage("Account created! Please log in.", false);
            // Clear fields
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            // Auto-navigate to login after 1.5 seconds
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(Duration.millis(1500));
            pause.setOnFinished(e -> goToLogin());
            pause.play();

        } else {
            showMessage(result.replace("ERROR: ", ""), true);
        }
    }

    @FXML
    private void goToLogin() {
        Stage stage = SceneManager.getStage(registerButton);
        SceneManager.switchTo(stage, "/FXML/login.fxml");
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setTextFill(isError
                ? Color.web("#FF6B6B")
                : Color.web("#4ECDC4"));
        messageLabel.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(300), messageLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}