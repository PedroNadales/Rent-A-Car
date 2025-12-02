module org.pedro.rentacar {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.pedro.rentacar.controller to javafx.fxml; // <-- abrir paquete de controladores
    opens org.pedro.rentacar.model to javafx.base;      // <-- opcional, para bindings en FXML
    exports org.pedro.rentacar;
}
