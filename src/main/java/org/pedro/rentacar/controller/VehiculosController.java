package org.pedro.rentacar.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.pedro.rentacar.dao.MantenimientoDAO;
import org.pedro.rentacar.dao.VehiculoDAO;
import org.pedro.rentacar.db.Database;
import org.pedro.rentacar.model.EstadoVehiculo;
import org.pedro.rentacar.model.Mantenimiento;
import org.pedro.rentacar.model.Vehiculo;
import org.pedro.rentacar.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VehiculosController {

    @FXML private TextField tfMatricula;
    @FXML private TextField tfMarca;
    @FXML private TextField tfModelo;
    @FXML private TextField tfAnio;
    @FXML private TextField tfPrecioDia;
    @FXML private ComboBox<EstadoVehiculo> cbEstado;
    @FXML private TextField tfBuscar;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnVolver;
    @FXML private Button btnEliminar;

    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colId;
    @FXML private TableColumn<Vehiculo, String> colMatricula;
    @FXML private TableColumn<Vehiculo, String> colMarca;
    @FXML private TableColumn<Vehiculo, String> colModelo;
    @FXML private TableColumn<Vehiculo, Integer> colAnio;
    @FXML private TableColumn<Vehiculo, String> colPrecioDia;
    @FXML private TableColumn<Vehiculo, String> colEstado;
    @FXML private ImageView imgVehiculo;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnAgregarMantenimiento;
    @FXML private Button btnVerMantenimientos;
    
    private VehiculoDAO vehiculoDAO;
    private MantenimientoDAO mantenimientoDAO;
    private ObservableList<Vehiculo> vehiculosList;
    private Connection conn;
    private String fotoUrl;


    @FXML
    public void initialize() {
        vehiculoDAO = new VehiculoDAO();
        mantenimientoDAO = new MantenimientoDAO();
        vehiculosList = FXCollections.observableArrayList();

        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idVehiculo"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colPrecioDia.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%.2f €", cellData.getValue().getPrecioDia())));
        colEstado.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEstado().toString()));

        // Configurar el ComboBox de estados
        cbEstado.getItems().setAll(EstadoVehiculo.values());

        // Configurar el botón de selección de imagen
        btnSeleccionarImagen.setOnAction(e -> seleccionarImagen());
        
        // Configurar búsqueda al presionar Enter
        tfBuscar.setOnAction(e -> buscarVehiculos());



        try {
            conn = Database.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("Error de conexión", "No se pudo conectar a la base de datos");
        }

        cargarVehiculos();

        // Configurar los manejadores de eventos de los botones
        btnAgregar.setOnAction(e -> agregarVehiculo());
        btnActualizar.setOnAction(e -> actualizarVehiculo());
        btnEliminar.setOnAction(e -> eliminarVehiculo());
        btnAgregarMantenimiento.setOnAction(e -> mostrarDialogoMantenimiento());
        btnVerMantenimientos.setOnAction(e -> mostrarMantenimientos());
        
        // Deshabilitar botones hasta que se seleccione un vehículo
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnAgregarMantenimiento.setDisable(true);
        btnVerMantenimientos.setDisable(true);
        
        // Habilitar/deshabilitar botones según la selección
        tablaVehiculos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean seleccionado = newSelection != null;
            btnActualizar.setDisable(!seleccionado);
            btnEliminar.setDisable(!seleccionado);
            btnAgregarMantenimiento.setDisable(!seleccionado);
            btnVerMantenimientos.setDisable(!seleccionado);
            
            if (seleccionado) {
                mostrarVehiculoSeleccionado(newSelection);
            } else {
                limpiarCampos();
            }
        });
    }

    private void cargarVehiculos() {
        try {
            List<Vehiculo> list = vehiculoDAO.listar();
            vehiculosList.setAll(list);
            tablaVehiculos.setItems(vehiculosList);

            // Configurar la selección de la tabla para mostrar la imagen
            tablaVehiculos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    mostrarVehiculoSeleccionado(newSelection);
                } else {
                    limpiarCampos();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarError("Error de base de datos", "No se pudieron cargar los vehículos");
        }
    }
    
    @FXML
    private void buscarVehiculos() {
        String busqueda = tfBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarVehiculos();
            return;
        }
        
        try {
            List<Vehiculo> vehiculos = vehiculoDAO.buscarPorTexto(busqueda);
            vehiculosList.setAll(vehiculos);
            tablaVehiculos.setItems(vehiculosList);
            
            if (vehiculos.isEmpty()) {
                mostrarMensaje("Búsqueda", "No se encontraron vehículos que coincidan con la búsqueda");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarError("Error de búsqueda", "Ocurrió un error al buscar vehículos");
        }
    }
    
    @FXML
    private void mostrarTodosVehiculos() {
        tfBuscar.clear();
        cargarVehiculos();
    }

    private void agregarVehiculo() {
        try {
            // Validar campos numéricos
            int anio;
            java.math.BigDecimal precioDia;

            try {
                anio = Integer.parseInt(tfAnio.getText().trim());
                precioDia = new java.math.BigDecimal(tfPrecioDia.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("Error de formato", "El año y el precio deben ser números válidos");
                return;
            }

            // Validar campos obligatorios
            if (tfMatricula.getText().trim().isEmpty() ||
                    tfMarca.getText().trim().isEmpty() ||
                    cbEstado.getValue() == null) {
                mostrarError("Campos requeridos", "Por favor complete todos los campos obligatorios");
                return;
            }

            Vehiculo v = new Vehiculo(
                    tfMatricula.getText().trim(),
                    tfMarca.getText().trim(),
                    tfModelo.getText().trim(),
                    anio,
                    precioDia,
                    cbEstado.getValue(),
                    fotoUrl  // Incluir la URL de la imagen
            );

            int id = vehiculoDAO.insertar(v, conn);
            if (id != -1) {
                v.setIdVehiculo(id); // Asignar el ID generado
                vehiculosList.add(v);
                limpiarCampos();
                cargarVehiculos(); // Recargar para asegurar que se muestre la imagen
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarError("Error de base de datos", "No se pudo guardar el vehículo: " + ex.getMessage());
        }
    }

    private void actualizarVehiculo() {
        Vehiculo v = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (v == null) {
            mostrarError("Selección requerida", "Por favor seleccione un vehículo para actualizar");
            return;
        }

        try {
            // Validar campos numéricos
            int anio;
            java.math.BigDecimal precioDia;

            try {
                anio = Integer.parseInt(tfAnio.getText().trim());
                precioDia = new java.math.BigDecimal(tfPrecioDia.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("Error de formato", "El año y el precio deben ser números válidos");
                return;
            }

            // Validar campos obligatorios
            if (tfMatricula.getText().trim().isEmpty() ||
                    tfMarca.getText().trim().isEmpty() ||
                    cbEstado.getValue() == null) {
                mostrarError("Campos requeridos", "Por favor complete todos los campos obligatorios");
                return;
            }

            // Actualizar el vehículo con los nuevos valores
            v.setMatricula(tfMatricula.getText().trim());
            v.setMarca(tfMarca.getText().trim());
            v.setModelo(tfModelo.getText().trim());
            v.setAnio(anio);
            v.setPrecioDia(precioDia);
            v.setEstado(cbEstado.getValue());
            v.setFotoUrl(fotoUrl); // Actualizar la URL de la imagen

            if (vehiculoDAO.actualizar(v, conn)) {
                // Actualizar la tabla
                int selectedIndex = tablaVehiculos.getSelectionModel().getSelectedIndex();
                vehiculosList.set(selectedIndex, v);
                tablaVehiculos.refresh();
                limpiarCampos();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarError("Error de base de datos", "No se pudo actualizar el vehículo: " + ex.getMessage());
        }
    }

    private void eliminarVehiculo() {
        Vehiculo v = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (v == null) return;

        try {
            String sql = "DELETE FROM vehiculo WHERE id_vehiculo=?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, v.getIdVehiculo());
                if (ps.executeUpdate() > 0) {
                    vehiculosList.remove(v);
                    limpiarCampos();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        tfMatricula.clear();
        tfMarca.clear();
        tfModelo.clear();
        tfAnio.clear();
        tfPrecioDia.clear();
        cbEstado.getSelectionModel().clearSelection();
        imgVehiculo.setImage(null);
        fotoUrl = null;
    }
    
    private void mostrarDialogoMantenimiento() {
        Vehiculo vehiculo = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vehiculo == null) {
            mostrarError("Selección requerida", "Por favor seleccione un vehículo para agregar mantenimiento");
            return;
        }
        
        // Crear diálogo para ingresar los datos del mantenimiento
        Dialog<Mantenimiento> dialog = new Dialog<>();
        dialog.setTitle("Agregar Mantenimiento");
        dialog.setHeaderText("Ingrese los datos del mantenimiento para " + vehiculo.getMarca() + " " + vehiculo.getModelo());
        
        // Obtener el Stage y eliminar la barra de título
        dialog.initStyle(StageStyle.UNDECORATED);
        
        // Aplicar estilos al diálogo
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("dialog-pane");
        
        // Configurar botones
        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, agregarButtonType);
        
        // Aplicar estilos a los botones
        Node agregarButton = dialogPane.lookupButton(agregarButtonType);
        agregarButton.getStyleClass().add("main-button");
        Node cancelarButton = dialogPane.lookupButton(ButtonType.CANCEL);
        cancelarButton.getStyleClass().add("main-button");
        
        // Crear campos del formulario
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: rgba(40, 40, 40, 0.95);");
        
        // Estilo para las etiquetas
        Label lblTipo = new Label("Tipo:");
        Label lblCosto = new Label("Costo:");
        Label lblFecha = new Label("Fecha:");
        
        // Aplicar estilos a los campos
        TextField tfTipo = new TextField();
        tfTipo.setPromptText("Tipo de mantenimiento");
        tfTipo.setStyle("-fx-prompt-text-fill: #999;");
        
        TextField tfCosto = new TextField();
        tfCosto.setPromptText("Costo");
        tfCosto.setStyle("-fx-prompt-text-fill: #999;");
        
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setStyle("-fx-background-color: rgba(60, 60, 60, 0.9); -fx-text-fill: white;");
        
        // Agregar elementos al grid
        grid.add(lblTipo, 0, 0);
        grid.add(tfTipo, 1, 0);
        grid.add(lblCosto, 0, 1);
        grid.add(tfCosto, 1, 1);
        grid.add(lblFecha, 0, 2);
        grid.add(dpFecha, 1, 2);
        
        // Aplicar estilos a los campos de texto
        for (Node node : grid.getChildren()) {
            if (node instanceof TextField) {
                node.setStyle(
                    "-fx-background-color: rgba(60, 60, 60, 0.9); " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #ff7100; " +
                    "-fx-border-radius: 5; " +
                    "-fx-padding: 5 10;"
                );
            } else if (node instanceof Label) {
                node.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
        
        dialogPane.setContent(grid);
        
        // Asegurar que el diálogo tenga el foco
        Platform.runLater(() -> tfTipo.requestFocus());
        
        // Convertir el resultado a un objeto Mantenimiento cuando se presiona Agregar
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == agregarButtonType) {
                try {
                    Mantenimiento m = new Mantenimiento();
                    m.setIdVehiculo(vehiculo.getIdVehiculo());
                    m.setTipo(tfTipo.getText().trim());
                    m.setFecha(dpFecha.getValue());
                    m.setCoste(new BigDecimal(tfCosto.getText().trim()));
                    return m;
                } catch (NumberFormatException e) {
                    mostrarError("Error de formato", "El costo debe ser un número válido");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Mantenimiento> result = dialog.showAndWait();
        
        result.ifPresent(mantenimiento -> {
            if (mantenimientoDAO.create(mantenimiento)) {
                mostrarMensaje("Mantenimiento agregado", "El mantenimiento se ha registrado correctamente.");
                // Actualizar el estado del vehículo a MANTENIMIENTO
                try {
                    vehiculo.setEstado(EstadoVehiculo.MANTENIMIENTO);
                    vehiculoDAO.updateEstado(vehiculo.getIdVehiculo(), EstadoVehiculo.MANTENIMIENTO, conn);
                    cargarVehiculos(); // Actualizar la tabla
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarError("Error", "No se pudo actualizar el estado del vehículo");
                }
            } else {
                mostrarError("Error", "No se pudo registrar el mantenimiento");
            }
        });
    }
    
    private void mostrarMantenimientos() {
        Vehiculo vehiculo = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vehiculo == null) {
            mostrarError("Selección requerida", "Por favor seleccione un vehículo para ver sus mantenimientos");
            return;
        }
        
        List<Mantenimiento> mantenimientos = mantenimientoDAO.findByVehiculo(vehiculo.getIdVehiculo());
        
        if (mantenimientos.isEmpty()) {
            mostrarMensaje("Mantenimientos", "No hay registros de mantenimiento para este vehículo.");
            return;
        }
        
        // Crear diálogo para mostrar la lista de mantenimientos
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historial de Mantenimientos");
        dialog.setHeaderText("Mantenimientos de " + vehiculo.getMarca() + " " + vehiculo.getModelo() + " (" + vehiculo.getMatricula() + ")");
        
        // Obtener el Stage y eliminar la barra de título
        dialog.initStyle(StageStyle.UNDECORATED);
        
        // Aplicar estilos al diálogo
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("dialog-pane");
        
        // Crear tabla para mostrar los mantenimientos
        TableView<Mantenimiento> tablaMantenimientos = new TableView<>();
        tablaMantenimientos.getStyleClass().add("table-view");
        
        // Configurar columnas de la tabla
        TableColumn<Mantenimiento, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdMantenimiento()).asObject());
        colId.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Mantenimiento, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));
        
        TableColumn<Mantenimiento, LocalDate> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        
        TableColumn<Mantenimiento, String> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory(cellData -> {
            BigDecimal costo = cellData.getValue().getCoste();
            return new SimpleStringProperty(String.format("%.2f €", costo.doubleValue()));
        });
        colCosto.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        // Ajustar el ancho preferido de las columnas
        colId.setPrefWidth(50);
        colTipo.setPrefWidth(150);
        colFecha.setPrefWidth(150);
        colCosto.setPrefWidth(100);
        
        // Agregar las columnas a la tabla
        tablaMantenimientos.getColumns().addAll(colId, colTipo, colFecha, colCosto);
        
        // Hacer que la tabla ocupe todo el espacio disponible
        TableView.TableViewSelectionModel<Mantenimiento> selectionModel = tablaMantenimientos.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        
        // Agregar los datos a la tabla
        ObservableList<Mantenimiento> data = FXCollections.observableArrayList(mantenimientos);
        tablaMantenimientos.setItems(data);
        
        // Configurar el diseño de la tabla
        tablaMantenimientos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Crear un contenedor para la tabla con scroll
        VBox content = new VBox();
        content.setPadding(new Insets(10));
        content.setSpacing(10);
        content.getStyleClass().add("dialog-content");
        
        // Añadir la tabla al contenedor
        VBox.setVgrow(tablaMantenimientos, Priority.ALWAYS);
        content.getChildren().add(tablaMantenimientos);
        
        // Configurar el diálogo
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        
        // Aplicar estilos al botón de cierre
        Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.getStyleClass().add("main-button");
        
        // Ajustar el tamaño del diálogo
        dialogPane.setPrefSize(700, 500);
        
        // Mostrar el diálogo
        dialog.showAndWait();
    }
    
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del vehículo");

        // Configurar filtros de archivo
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Imágenes", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostrar el diálogo de selección de archivo
        File file = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());

        if (file != null) {
            try {
                // Guardar la imagen en el directorio de recursos
                String relativePath = ImageUtils.saveImage(file);
                if (relativePath != null) {
                    // Mostrar la imagen en el ImageView
                    Image image = new Image(new File("src/main/resources/" + relativePath).toURI().toString());
                    imgVehiculo.setImage(image);
                    fotoUrl = relativePath;
                } else {
                    mostrarError("Error", "No se pudo guardar la imagen");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al cargar la imagen", e.getMessage());
            }
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarVehiculoSeleccionado(Vehiculo vehiculo) {
        if (vehiculo == null) {
            limpiarCampos();
            return;
        }

        // Actualizar campos de texto
        tfMatricula.setText(vehiculo.getMatricula() != null ? vehiculo.getMatricula() : "");
        tfMarca.setText(vehiculo.getMarca() != null ? vehiculo.getMarca() : "");
        tfModelo.setText(vehiculo.getModelo() != null ? vehiculo.getModelo() : "");
        tfAnio.setText(vehiculo.getAnio() != null ? String.valueOf(vehiculo.getAnio()) : "");
        tfPrecioDia.setText(vehiculo.getPrecioDia() != null ? vehiculo.getPrecioDia().toString() : "");
        cbEstado.setValue(vehiculo.getEstado());

        // Cargar la imagen del vehículo si existe
        cargarImagenVehiculo(vehiculo.getFotoUrl());
    }
    
    private void cargarImagenVehiculo(String fotoUrl) {
        if (fotoUrl == null || fotoUrl.trim().isEmpty()) {
            imgVehiculo.setImage(null);
            this.fotoUrl = null;
            return;
        }
        
        try {
            // Normalizar la ruta de la imagen
            String imagePath = fotoUrl.startsWith("/") ? fotoUrl : "/" + fotoUrl;
            imagePath = imagePath.replace("\\", "/");
            
            // Primero intentar cargar como recurso del classpath (desde el JAR)
            try (java.io.InputStream is = getClass().getResourceAsStream(imagePath)) {
                if (is != null) {
                    Image image = new Image(is);
                    if (!image.isError() && image.getWidth() > 0) {
                        imgVehiculo.setImage(image);
                        this.fotoUrl = fotoUrl;
                        return;
                    }
                }
            }
            
            // Si no se pudo cargar como recurso, intentar cargar directamente del sistema de archivos
            // Primero intentar con la ruta relativa
            File file = new File("src/main/resources" + imagePath);
            if (!file.exists()) {
                // Si no existe, intentar con la ruta absoluta
                file = new File(fotoUrl);
            }
            
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                if (!image.isError() && image.getWidth() > 0) {
                    imgVehiculo.setImage(image);
                    this.fotoUrl = fotoUrl;
                    return;
                }
            }
            
            // Si llegamos aquí, no se pudo cargar la imagen
            System.err.println("No se pudo cargar la imagen: " + fotoUrl);
            imgVehiculo.setImage(null);
            this.fotoUrl = null;
            
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            e.printStackTrace();
            imgVehiculo.setImage(null);
            this.fotoUrl = null;
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
            mainStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            mainStage.setFullScreen(true);
            
            // Mostrar la ventana principal
            mainStage.show();
            
            // Cerrar la ventana actual después de que se muestre la nueva
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
