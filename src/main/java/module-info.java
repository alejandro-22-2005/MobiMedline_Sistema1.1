module interfaz.mobimedline_sistema {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql; // Agrege esto para la base de datos

    opens interfaz.mobimedline_sistema to javafx.fxml;
    exports interfaz.mobimedline_sistema;
}
