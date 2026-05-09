package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db.DBConnection;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Test database connection
        DBConnection.connect();

        // Load FXML
        URL fxmlLocation = getClass().getResource("/FXML/login.fxml");

        if (fxmlLocation == null) {
            throw new RuntimeException("Cannot find login.fxml in resources/FXML/");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 600);

        // Load CSS
        URL cssLocation = getClass().getResource("/CSS/style.css");

        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        } else {
            System.out.println("Warning: style.css not found.");
        }

        // Stage setup
        stage.setTitle("FinVerse Wallet");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}