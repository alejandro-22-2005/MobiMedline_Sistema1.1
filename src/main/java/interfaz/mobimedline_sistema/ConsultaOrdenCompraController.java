/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;


/**
 * FXML Controller class
 *
 * @author Mike
 */
public class ConsultaOrdenCompraController implements Initializable {
    
    @FXML
    private AnchorPane apContenedorCentro;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnVerdetalle;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private DatePicker dpFechaEmision;

    @FXML
    private Label lblAgregarUsuarioTitulo;

    @FXML
    private TextField tfIdOrden;

    @FXML
    private TableColumn<ODC, String> tlcEmision;

    @FXML
    private TableColumn<ODC, String> tlcEstatus;

    @FXML
    private TableColumn<ODC, String> tlcIdOrden;

    @FXML
    private TableColumn<ODC, String> tlcResponsable;

    @FXML
    private TableView<ODC> tlvLista;
    
    private ObservableList<ODC> listaOriginal = FXCollections.observableArrayList();
    private FilteredList<ODC> listaFiltrada;
   
    @FXML
    private void handleVerDetalles(ActionEvent event) {
        ODC seleccion = tlvLista.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            // ... alerta de selección vacía ...
            return;
        }

        StringBuilder detalle = new StringBuilder();
        Map<String, Integer> conteoInsumosTotal = new HashMap<>();

        // Usamos el método que ya programaste en ODC para no repetir lógica
        List<Insumo> totales = seleccion.obtenerTotalesPorId();

        detalle.append("--- RESUMEN DE INSUMOS TOTALES ---\n");
        for (Insumo i : totales) {
            detalle.append("- ").append(i.getNombre())
                   .append(": ").append(i.getCantidadPorUnidad()).append(" unidades\n");
        }

        detalle.append("\n--- PRODUCTOS EN ESTA ORDEN ---\n");
        for (Producto p : seleccion.getProductos()) {
            // Usamos getDescripcion() y getCantidad() que están en tu clase Producto
            detalle.append("• ").append(p.getDescripcion())
                   .append(" (Cant: ").append(p.getCantidad()).append(")\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de Orden: " + seleccion.getIdODC());
        // Acceso correcto al objeto Usuarios responsable
        alert.setHeaderText("Responsable: " + seleccion.getResponsable().getNombre() + 
                           " (" + seleccion.getResponsable().getUsuario() + ")");
        alert.setContentText(detalle.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handleBuscar(ActionEvent event) {
        String idBusqueda = tfIdOrden.getText().trim().toLowerCase();
        Object estadoObj = cbEstado.getValue();
        String estadoBusqueda = (estadoObj != null) ? estadoObj.toString() : "";

        // 1. Obtener la fecha del DatePicker
        LocalDate fechaSeleccionada = dpFechaEmision.getValue();

        // Validación de campos vacíos
        if (idBusqueda.isEmpty() && fechaSeleccionada == null && estadoBusqueda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Por favor, ingrese un criterio de búsqueda.");
            alert.showAndWait();
            return;
        }

        listaFiltrada.setPredicate(orden -> {
            // Filtro por ID y Estado
            boolean matchId = idBusqueda.isEmpty() || 
                             orden.getIdODC().toLowerCase().contains(idBusqueda);
            boolean matchEstado = estadoBusqueda.isEmpty() || 
                                 orden.getEstado().equalsIgnoreCase(estadoBusqueda);

            // 2. Filtro por Fecha con NORMALIZACIÓN
            boolean matchFecha = true;
            if (fechaSeleccionada != null) {
                /* Si tu ODC guarda la fecha como "2026-04-25", 
                   fechaSeleccionada.toString() devolverá exactamente "2026-04-25".
                   Usamos .trim() para evitar errores por espacios invisibles.
                */
                String fechaAComparar = fechaSeleccionada.toString(); 
                matchFecha = orden.getFechaODC().contains(fechaSeleccionada.toString());
            }

            return matchId && matchEstado && matchFecha;
        });

        // 3. Forzar actualización de la tabla (opcional pero recomendado)
        tlvLista.refresh();
    }
    
    @FXML
    private void handleLimpiar(ActionEvent event) {
        // 1. Limpiar componentes de entrada
        tfIdOrden.clear();
        dpFechaEmision.setValue(null);
        dpFechaEmision.getEditor().clear();
        cbEstado.getSelectionModel().clearSelection();

        // 2. Restaurar el predicado para mostrar todos los datos
        listaFiltrada.setPredicate(ODC -> true);

        // Opcional: Solicitar el foco en el primer campo de búsqueda
        tfIdOrden.requestFocus();
    }
    
    private void actualizarFiltro() {
        String idBusqueda = tfIdOrden.getText().trim().toLowerCase();
        String estadoBusqueda = (cbEstado.getValue() != null) ? cbEstado.getValue() : "";

        // Obtenemos el valor del calendario
        LocalDate fechaCalendario = dpFechaEmision.getValue();
        // Obtenemos lo que el usuario escribe manualmente (por si acaso)
        String textoFechaManual = dpFechaEmision.getEditor().getText().trim();

        listaFiltrada.setPredicate(orden -> {
            // 1. Lógica de ID
            boolean matchId = idBusqueda.isEmpty() || 
                             orden.getIdODC().toLowerCase().contains(idBusqueda);

            // 2. Lógica de Estado
            boolean matchEstado = estadoBusqueda.isEmpty() || 
                                 orden.getEstado().equalsIgnoreCase(estadoBusqueda);

            // 3. Lógica de Fecha (EL PUNTO CRÍTICO)
            boolean matchFecha = true;
            
            // Si el usuario seleccionó una fecha en el calendario
            if (fechaCalendario != null) {
                /* IMPORTANTE: Comparamos el String almacenado en la ODC 
                   con el String generado por el DatePicker (ISO_LOCAL_DATE: yyyy-MM-dd).
                */
                matchFecha = orden.getFechaODC().equals(fechaCalendario.toString());
            } 
            // Si no hay fecha en el calendario pero hay texto manual (ej. el usuario borró o escribió)
            else if (!textoFechaManual.isEmpty()) {
                matchFecha = orden.getFechaODC().contains(textoFechaManual);
            }

            return matchId && matchEstado && matchFecha;
        });
    }
    
    private void configurarColumnas() {
        // Configuración de celdas existente
        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idODC"));
        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("fechaODC"));
        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // --- AGREGAR ESTO PARA CENTRAR ---
        tlcIdOrden.setStyle("-fx-alignment: CENTER;");
        tlcEmision.setStyle("-fx-alignment: CENTER;");
        tlcEstatus.setStyle("-fx-alignment: CENTER;");
        tlcResponsable.setStyle("-fx-alignment: CENTER;");

        // Tu lógica existente para el responsable
        tlcResponsable.setCellValueFactory(cellData -> {
            Usuarios resp = cellData.getValue().getResponsable();
            return new javafx.beans.property.SimpleStringProperty(resp != null ? resp.getUsuario() : "N/A");
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Cargar datos
        listaOriginal.clear();
        List<ODC> datosCargados = ArchivoOdcBase.getOdcBase();
        if (datosCargados != null) {
            listaOriginal.addAll(datosCargados);
        }

        // 2. Configurar la tabla y el filtro inicial (mostrar todo)
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        tlvLista.setItems(listaFiltrada);

        // 3. Configurar columnas (Responsable, etc.)
        configurarColumnas();

        // 4. Configurar ComboBox
        cbEstado.setItems(FXCollections.observableArrayList("Emitida", "Pendiente"));

        // IMPORTANTE: Aquí NO agregues listeners a tfIdOrden ni a dpFechaEmision.
        // Al no tener listeners, la tabla no se moverá hasta que presiones el botón.
    } 
}
