module com.example.proyectofinalpoov2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.proyectofinalpoov2 to javafx.fxml;
    exports com.example.proyectofinalpoov2;
}