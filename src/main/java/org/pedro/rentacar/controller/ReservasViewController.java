package org.pedro.rentacar.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.pedro.rentacar.dao.ClienteDAO;
import org.pedro.rentacar.dao.ReservaVehiculoDAO;
import org.pedro.rentacar.dao.VehiculoDAO;
import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.Cliente;
import org.pedro.rentacar.model.EstadoVehiculo;
import org.pedro.rentacar.model.Reserva;
import org.pedro.rentacar.model.Vehiculo;
import org.pedro.rentacar.service.ReservaService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservasViewController {

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, Integer> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, LocalDate> colInicio;
    @FXML private TableColumn<Reserva, LocalDate> colFin;
    @FXML private TableColumn<Reserva, BigDecimal> colTotal;
    @FXML private TableColumn<Reserva, String> colVehiculos;

    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private ListView<Vehiculo> listaVehiculos;

    @FXML private Button btnCrearReserva;
    @FXML private Button btnCancelar;
    @FXML private Button btnDevolver;
    @FXML private Button btnVolver;

    private ReservaService reservaService;
    private ClienteDAO clienteDAO = new ClienteDAO();
    private VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private ReservaVehiculoDAO reservaVehiculoDAO = new ReservaVehiculoDAO();
    private Connection conn;

    @FXML
    public void initialize() {
        try {
            conn = Database.getInstance().getConnection(); // una sola conexión
            reservaService = new ReservaService();

            configurarTabla();

            btnCrearReserva.setOnAction(e -> crearReserva());
            btnCancelar.setOnAction(e -> cancelarReserva());
            btnDevolver.setOnAction(e -> devolverVehiculos());

            cargarClientes();
            cargarVehiculosDisponibles();
            cargarReservas();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdReserva()).asObject());
        colCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCliente().getNombre()));
        colInicio.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaInicio()));
        colFin.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaFin()));
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getTotal()));

        // Configurar columna de vehículos para mostrar marca, modelo y matrícula
        if (colVehiculos != null) {
            colVehiculos.setCellValueFactory(c -> {
                List<Vehiculo> vehiculos = c.getValue().getVehiculos();
                if (vehiculos != null && !vehiculos.isEmpty()) {
                    String vehiculosStr = vehiculos.stream()
                            .map(v -> String.format("%s %s (%s)", 
                                v.getMarca(), 
                                v.getModelo(), 
                                v.getMatricula()))
                            .reduce((v1, v2) -> v1 + ", " + v2)
                            .orElse("");
                    return new javafx.beans.property.SimpleStringProperty(vehiculosStr);
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });
        }
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            
            // Configurar cómo se muestra cada cliente en el ComboBox
            comboClientes.setCellFactory(lv -> new ListCell<Cliente>() {
                @Override
                protected void updateItem(Cliente cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (cliente == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%s (%s)", 
                            cliente.getNombre(), 
                            cliente.getDni()));
                    }
                }
            });
            
            // Configurar cómo se muestra el cliente seleccionado
            comboClientes.setButtonCell(new ListCell<Cliente>() {
                @Override
                protected void updateItem(Cliente cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (cliente == null || empty) {
                        setText("Seleccione un cliente");
                    } else {
                        setText(String.format("%s (%s)", 
                            cliente.getNombre(), 
                            cliente.getDni()));
                    }
                }
            });
            
            comboClientes.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarVehiculosDisponibles() {
        try {
            List<Vehiculo> vehiculos = vehiculoDAO.listar()
                    .stream()
                    .filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE)
                    .toList();

            // Configurar el cell factory para mostrar imagen, marca, modelo y matrícula
            listaVehiculos.setCellFactory(lv -> new ListCell<Vehiculo>() {
                private final ImageView imageView = new ImageView();
                private final Label label = new Label();
                private final HBox content = new HBox(10);

                {
                    content.setAlignment(Pos.CENTER_LEFT);
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(60);
                    imageView.setPreserveRatio(true);
                    content.getChildren().addAll(imageView, label);
                    content.setPadding(new Insets(5));
                }

                @Override
                protected void updateItem(Vehiculo vehiculo, boolean empty) {
                    super.updateItem(vehiculo, empty);
                    if (vehiculo == null || empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        // Cargar la imagen si existe
                        if (vehiculo.getFotoUrl() != null && !vehiculo.getFotoUrl().isEmpty()) {
                            try {
                                File file = new File("src/main/resources/" + vehiculo.getFotoUrl());
                                if (file.exists()) {
                                    Image image = new Image(file.toURI().toString());
                                    imageView.setImage(image);
                                } else {
                                    imageView.setImage(null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                imageView.setImage(null);
                            }
                        } else {
                            imageView.setImage(null);
                        }
                        
                        // Configurar el texto
                        label.setText(String.format("%s %s (%s) - %.2f€/día", 
                            vehiculo.getMarca(), 
                            vehiculo.getModelo(), 
                            vehiculo.getMatricula(),
                            vehiculo.getPrecioDia()));
                        
                        setGraphic(content);
                        setText(null);
                    }
                }
            });

            listaVehiculos.setItems(FXCollections.observableArrayList(vehiculos));
            listaVehiculos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarReservas() {
        try (Connection conn = Database.getInstance().getConnection()) {
            List<Reserva> reservas = reservaService.listarReservas(conn);

            // Para cada reserva, cargar sus vehículos
            for (Reserva reserva : reservas) {
                List<Vehiculo> vehiculos = reservaVehiculoDAO.findVehiculosByReserva(reserva.getIdReserva(), conn);
                reserva.setVehiculos(vehiculos);
            }

            tablaReservas.setItems(FXCollections.observableArrayList(reservas));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // --------------------------
    //   ACCIONES
    // --------------------------

    private void crearReserva() {
        try {
            Cliente c = comboClientes.getValue();
            LocalDate inicio = fechaInicio.getValue();
            LocalDate fin = fechaFin.getValue();
            List<Vehiculo> vehiculos = listaVehiculos.getSelectionModel().getSelectedItems();

            if (c == null || inicio == null || fin == null || vehiculos.isEmpty()) {
                System.out.println("⚠️ Faltan datos para crear la reserva.");
                return;
            }

            if (fin.isBefore(inicio)) {
                System.out.println("⚠️ La fecha de fin no puede ser anterior a la fecha de inicio.");
                return;
            }

            // Calculamos el total automáticamente
            long dias = inicio.until(fin).getDays() + 1; // incluimos el primer día
            BigDecimal total = vehiculos.stream()
                    .map(v -> v.getPrecioDia().multiply(BigDecimal.valueOf(dias)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Reserva r = new Reserva(c, inicio, fin, total);

            // Iniciamos la transacción
            conn.setAutoCommit(false);

            try {
                boolean ok = reservaService.crearReservaConVehiculos(r, vehiculos, conn);

                if (ok) {
                    conn.commit();  // Confirmamos la transacción
                    cargarReservas();
                    cargarVehiculosDisponibles(); // refrescar lista
                } else {
                    conn.rollback();  // Si hay error, hacemos rollback
                    System.out.println("Error al crear la reserva");
                }
            } catch (SQLException e) {
                conn.rollback();  // Si hay excepción, hacemos rollback
                e.printStackTrace();
                System.out.println("Error al crear la reserva: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);  // Volvemos al modo auto-commit
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al crear la reserva: " + e.getMessage());
        }
    }


    private void cancelarReserva() {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();
        if (r == null) return;

        try {
            reservaService.cancelarReserva(r, conn);
            cargarReservas();
            cargarVehiculosDisponibles();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void devolverVehiculos() {
        Reserva r = tablaReservas.getSelectionModel().getSelectedItem();
        if (r == null) return;

        try {
            reservaService.devolverVehiculos(r, conn);
            cargarReservas();
            cargarVehiculosDisponibles();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    //   VOLVER A PRINCIPAL
    // --------------------------

    @FXML
    private void volverAPrincipal(ActionEvent event) {
        try {
            // Cerrar la conexión si está abierta
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            
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
            mainStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            mainStage.setFullScreen(true);
            
            // Mostrar la ventana principal
            mainStage.show();
            
            // Cerrar la ventana actual después de que se muestre la nueva
            currentStage.close();
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
