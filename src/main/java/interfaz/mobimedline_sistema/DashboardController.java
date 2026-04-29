/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class DashboardController implements Initializable {
    @FXML private AnchorPane contenedor;

    @FXML private Label lblPendientes;
    @FXML private Label lblProcesadas;
    @FXML private Label lblEspeciales;

    @FXML private TableView<Orden> tablaOrdenes;
    @FXML private TableColumn<Orden, String> colId;
    @FXML private TableColumn<Orden, String> colFecha;
    @FXML private TableColumn<Orden, String> colUsuario;
    @FXML private TableColumn<Orden, String> colEstado;

    private ObservableList<Orden> lista;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha()));
        colUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsuario()));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));

        lista = FXCollections.observableArrayList(
                new Orden("001", "2026-04-03", "Alexis", "Pendiente"),
                new Orden("002", "2026-04-02", "Carlos", "Procesada"),
                new Orden("003", "2026-04-01", "Ana", "Especial")
        );

        tablaOrdenes.setItems(lista);

        actualizarContadores();
    }

    private void actualizarContadores() {

        int pendientes = 0, procesadas = 0, especiales = 0;

        for (Orden o : lista) {
            switch (o.getEstado()) {
                case "Pendiente":
                    pendientes++;
                    break;
                case "Emitida":
                    procesadas++;
                    break;
                case "En Proceso":
                    especiales++;
                    break;
            }
        }

        lblPendientes.setText(String.valueOf(pendientes));
        lblProcesadas.setText(String.valueOf(procesadas));
        lblEspeciales.setText(String.valueOf(especiales));
    }
    public class Orden {
    private String id;
    private String fecha;
    private String usuario;
    private String estado;

    public Orden(String id, String fecha, String usuario, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getEstado() { return estado; }
}
    
}
