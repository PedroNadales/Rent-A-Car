package org.pedro.rentacar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

/*
 * Controlador de la ventana principal de Rent-A-Car
 */
public class MainController {

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnVehiculos;

    @FXML
    private Button btnReservas;
    
    @FXML
    private Button btnSalir;

    // Método que se ejecuta al inicializar el controlador
    @FXML
    public void initialize() {
        // Configuramos acciones para los botones
        btnClientes.setOnAction(this::abrirPantallaClientes);
        btnVehiculos.setOnAction(this::abrirPantallaVehiculos);
        btnReservas.setOnAction(this::abrirPantallaReservas);
        btnSalir.setOnAction(e -> salirAplicacion());
    }

    private void abrirPantallaClientes(ActionEvent event) {
        abrirVentana("/fxml/ClientesView.fxml", "Gestión de Clientes", btnClientes);
    }

    private void abrirPantallaVehiculos(ActionEvent event) {
        abrirVentana("/fxml/VehiculosView.fxml", "Gestión de Vehículos", btnVehiculos);
    }

    private void abrirPantallaReservas(ActionEvent event) {
        abrirVentana("/fxml/ReservasView.fxml", "Gestión de Reservas", btnReservas);
    }
    
    private void salirAplicacion() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    // Método común para abrir ventanas y cerrar la principal
    private void abrirVentana(String fxmlPath, String titulo, Button botonActual) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Configurar la escena
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(titulo);
            
            // Configurar pantalla completa
            stage.setFullScreenExitHint(""); // Ocultar mensaje de salida de pantalla completa
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Deshabilitar tecla de salida
            stage.setFullScreen(true); // Pantalla completa real (como F11)
            
            stage.show();

            // Cerrar la ventana principal
            Stage mainStage = (Stage) botonActual.getScene().getWindow();
            mainStage.hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
