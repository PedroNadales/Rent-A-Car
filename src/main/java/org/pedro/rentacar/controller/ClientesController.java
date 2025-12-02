package org.pedro.rentacar.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.pedro.rentacar.dao.ClienteDAO;
import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.Cliente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClientesController {

    @FXML private TextField tfNombre;
    @FXML private TextField tfDni;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfEmail;
    @FXML private TextField tfBuscar;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;

    private ClienteDAO clienteDAO;
    private ObservableList<Cliente> clientesList;

    @FXML
    public void initialize() {
        clienteDAO = new ClienteDAO();
        clientesList = FXCollections.observableArrayList();

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdCliente()).asObject());
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colDni.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDni()));
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        try {
            conn = Database.getInstance().getConnection(); // conexión viva de Singleton
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cargarClientes();

        btnAgregar.setOnAction(e -> agregarCliente());
        btnActualizar.setOnAction(e -> actualizarCliente());
        btnEliminar.setOnAction(e -> eliminarCliente());
    }

    private Connection conn; // conexión persistente para toda la ventana

    private void cargarClientes() {
        try {
            List<Cliente> list = clienteDAO.listar();
            clientesList.setAll(list);
            tablaClientes.setItems(clientesList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void agregarCliente() {
        try {
            Cliente c = new Cliente(tfNombre.getText(), tfDni.getText(), tfTelefono.getText(), tfEmail.getText());
            int id = clienteDAO.insertar(c);
            if (id != -1) {
                clientesList.add(c);
                limpiarCampos();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarCliente() {
        Cliente c = tablaClientes.getSelectionModel().getSelectedItem();
        if (c == null) return;
        try {
            c.setNombre(tfNombre.getText());
            c.setDni(tfDni.getText());
            c.setTelefono(tfTelefono.getText());
            c.setEmail(tfEmail.getText());
            if (clienteDAO.actualizar(c)) {
                tablaClientes.refresh();
                limpiarCampos();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void eliminarCliente() {
        Cliente c = tablaClientes.getSelectionModel().getSelectedItem();
        if (c == null) return;
        try {
            if (clienteDAO.eliminar(c.getIdCliente())) {
                clientesList.remove(c);
                limpiarCampos();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void volverAPrincipal(ActionEvent event) {
        try {
            // Cerrar la ventana actual
            Stage currentStage = (Stage) btnVolver.getScene().getWindow();
            
            // Cargar la vista principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Configurar la escena
            Scene scene = new Scene(root);
            Stage mainStage = new Stage();
            mainStage.setScene(scene);
            mainStage.setTitle("Rent-A-Car");
            
            // Configurar pantalla completa
            mainStage.setFullScreenExitHint("");
            mainStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            mainStage.setFullScreen(true);
            
            // Mostrar la ventana principal
            mainStage.show();
            
            // Cerrar la ventana actual después de que se muestre la nueva
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void limpiarCampos() {
        tfNombre.clear();
        tfDni.clear();
        tfTelefono.clear();
        tfEmail.clear();
    }

    @FXML
    private void buscarClientes() {
        String busqueda = tfBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarClientes();
            return;
        }
        
        try {
            List<Cliente> clientes = clienteDAO.buscarPorTexto(busqueda);
            clientesList.setAll(clientes);
            tablaClientes.setItems(clientesList);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Mostrar mensaje de error al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de búsqueda");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al buscar clientes.");
            alert.showAndWait();
        }
    }

    @FXML
    private void mostrarTodosClientes() {
        tfBuscar.clear();
        cargarClientes();
    }
}
