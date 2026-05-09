package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Handles navigation between FXML screens.
 * Usage: SceneManager.switchTo(stage, "dashboard.fxml");
 */
public class SceneManager {

    private static final String FXML_PATH = "/FXML/";

    private SceneManager() {}


    public static void switchTo(Stage stage, String fxmlFile) {
        try {

            // If fxmlFile starts with "/", use it directly.
            String fullPath = fxmlFile.startsWith("/") ? fxmlFile : FXML_PATH + fxmlFile;

            var resource = SceneManager.class.getResource(fullPath);

            if (resource == null) {
                throw new IOException("Cannot find FXML file at: " + fullPath);
            }

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            var cssResource = SceneManager.class.getResource("/CSS/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException | NullPointerException e) {
            System.err.println("Navigation Error: Check if " + fxmlFile + " exists in resources.");
            e.printStackTrace();
        }
    }
    /**
     * Get the stage from any node inside a scene.
     */
    public static Stage getStage(javafx.scene.Node node) {
        if (node.getScene() == null) {
            throw new NullPointerException("Node is not attached to a Scene.");
        }

        if (node.getScene().getWindow() == null) {
            throw new NullPointerException("Scene has no Window.");
        }

        return (Stage) node.getScene().getWindow();
    }
}