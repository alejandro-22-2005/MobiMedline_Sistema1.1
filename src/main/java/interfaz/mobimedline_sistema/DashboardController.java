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

    @FXML private TableView<ODC> tablaOrdenes;
    @FXML private TableColumn<ODC, String> colId;
    @FXML private TableColumn<ODC, String> colFecha;
    @FXML private TableColumn<ODC, String> colUsuario;
    @FXML private TableColumn<ODC, String> colEstado;

    public static ObservableList<ODC> lista = FXCollections.observableArrayList();
    
    // Instanciamos el DAO de persistencia que conecta con Supabase
    private final ODCDAO odcDAO = new ODCDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIdODC()));

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFechaODC()));

        colUsuario.setCellValueFactory(data -> {
            var responsable = data.getValue().getResponsable();
            return new javafx.beans.property.SimpleStringProperty(responsable != null ? responsable.getUsuario() : "N/A");
        }); //

        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEstado()));
        /*
        if (lista.isEmpty()) {

            lista.addAll(
                new Orden("0001-ODC", "2026-04-25", "DOSJ01", "Pendiente"),
                new Orden("0002-ODC", "2026-04-25", "OIPM01", "Emitida"),
                new Orden("0003-ODC", "2026-04-25", "RAAA01", "Pendiente")
            );
        }
        */
        cargarDatosDesdeSupabase();
        tablaOrdenes.setItems(lista);

        // =========================================
        // QUITAR FILAS VACÍAS Y CRECER AUTOMÁTICAMENTE
        // =========================================

        tablaOrdenes.setFixedCellSize(45);

        tablaOrdenes.prefHeightProperty().bind(
            tablaOrdenes.fixedCellSizeProperty().multiply(
                javafx.beans.binding.Bindings.size(
                    tablaOrdenes.getItems()
                ).add(1)
            )
        );

        // =========================================

        lista.addListener((javafx.collections.ListChangeListener.Change<? extends ODC> c) -> {
            actualizarContadores();
            tablaOrdenes.refresh();
        });

        actualizarContadores();
    }
    
    private void cargarDatosDesdeSupabase() {
        try {
            lista.clear();
            // Traemos las órdenes reales mapeadas con sus productos y usuarios
            lista.addAll(odcDAO.obtenerTodas());
        } catch (Exception e) {
            System.err.println("Error al sincronizar el Dashboard con Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarContadores() {

        int pendientes = 0;
        int procesadas = 0; // Se mapea al estado "Emitida" de la base de datos
        int especiales = 0;  // Cuenta cuántas órdenes vigentes son de tipo especial

        for (ODC o : lista) {
            // Contadores por estado de la Ficha ODC
            if (o.getEstado() != null) {
                switch (o.getEstado()) {
                    case "Pendiente":
                        pendientes++;
                        break;
                    case "Emitida":
                        procesadas++;
                        break;
                }
            }
            
            // Contador de órdenes con requerimientos especiales
            if (o.getTipoEspecial()) {
                especiales++;
            }
        }

        lblPendientes.setText(String.valueOf(pendientes));
        lblProcesadas.setText(String.valueOf(procesadas));
        lblEspeciales.setText(String.valueOf(especiales));
    }

}