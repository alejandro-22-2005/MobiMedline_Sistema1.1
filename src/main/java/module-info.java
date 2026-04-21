module interfaz.mobimedline_sistema {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens interfaz.mobimedline_sistema to javafx.fxml;
    exports interfaz.mobimedline_sistema;
}
