package org.pedro.rentacar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Configurar la ventana principal
            primaryStage.setScene(scene);
            primaryStage.setTitle("Rent-A-Car");
            
            // Configurar pantalla completa
            primaryStage.setFullScreenExitHint(""); // Ocultar mensaje de salida
            primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH); // Deshabilitar tecla de salida
            primaryStage.setFullScreen(true); // Pantalla completa real (F11)
            
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
