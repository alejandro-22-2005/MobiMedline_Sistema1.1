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
import javafx.scene.control.cell.ComboBoxTableCell;


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
    
    // Instancia de persistencia real hacia Supabase
    private final ODCDAO odcDAO = new ODCDAO();
   
    @FXML
    private void handleVerDetalles(ActionEvent event) {
        ODC seleccion = tlvLista.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione una orden de la tabla para ver su detalle.");
            alert.showAndWait();
            return;
        }

        StringBuilder detalle = new StringBuilder();
        List<Insumo> totales = odcDAO.obtenerInsumosDeOrden(seleccion.getIdODC());
        
        // 1. Mostrar resumen de insumos totales consolidados
        detalle.append("--- RESUMEN DE INSUMOS TOTALES ---\n");
        if (totales.isEmpty()) {
            detalle.append("No se encontraron registros de insumos para esta orden en Supabase.\n");
        } else {
            for (Insumo i : totales) {
                // Imprime el nombre y la cantidad total de la tabla odc_insumo
                detalle.append("- ").append(i.getNombre())
                       .append(": ").append(i.getCantidadPorUnidad()).append(" unidades\n");
            }
        }
        /*
        List<Insumo> totales = seleccion.obtenerTotalesPorId();
        if (totales == null || totales.isEmpty()) {
            detalle.append("No hay insumos calculados.\n");
        } else {
            for (Insumo i : totales) {
                detalle.append("- ").append(i.getNombre())
                       .append(": ").append(i.getCantidadPorUnidad()).append(" unidades\n");
            }
        }
        */
        // 2. Mostrar los productos que componen la orden tal como se mapearon desde la BD
        detalle.append("\n--- PRODUCTOS EN ESTA ORDEN ---\n");
        if (seleccion.getProductos() == null || seleccion.getProductos().isEmpty()) {
        detalle.append("No hay productos registrados en esta orden.\n");
        } else {
            for (Producto p : seleccion.getProductos()) {
                // Mostramos únicamente el nombre o la descripción del producto
                detalle.append("• ").append(p.getDescripcion()).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de Orden: " + seleccion.getIdODC());
        
        if (seleccion.getResponsable() != null) {
            alert.setHeaderText("Responsable: " + seleccion.getResponsable().getNombre() + 
                               " (" + seleccion.getResponsable().getUsuario() + ")");
        } else {
            alert.setHeaderText("Responsable: No asignado");
        }
        
        alert.setContentText(detalle.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handleBuscar(ActionEvent event) {
        // Ejecutamos la lógica centralizada de filtrado al hacer clic en Buscar
        actualizarFiltro();
        tlvLista.refresh();
    }
    
    @FXML
    private void handleLimpiar(ActionEvent event) {
        // 1. Limpiar componentes de entrada de la interfaz gráfica
        tfIdOrden.clear();
        dpFechaEmision.setValue(null);
        dpFechaEmision.getEditor().clear();
        cbEstado.getSelectionModel().clearSelection();

        // 2. Restaurar el filtro para volver a ver todas las órdenes descargadas
        listaFiltrada.setPredicate(orden -> true);
        tfIdOrden.requestFocus();
    }
    
    private void actualizarFiltro() {
        String idBusqueda = tfIdOrden.getText().trim().toLowerCase();
        Object estadoObj = cbEstado.getValue();
        String estadoBusqueda = (estadoObj != null) ? estadoObj.toString() : "";
        LocalDate fechaCalendario = dpFechaEmision.getValue();

        // Validación si todos los criterios están vacíos
        if (idBusqueda.isEmpty() && fechaCalendario == null && estadoBusqueda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Filtros Vacíos");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese un criterio de búsqueda.");
            alert.showAndWait();
            return;
        }

        // Aplicación del predicado dinámico sobre los datos persistidos
        listaFiltrada.setPredicate(orden -> {
            // 1. Filtro por ID de Orden
            boolean matchId = idBusqueda.isEmpty() || 
                             (orden.getIdODC() != null && orden.getIdODC().toLowerCase().contains(idBusqueda));

            // 2. Filtro por Estado de la orden (Emitida / Pendiente)
            boolean matchEstado = estadoBusqueda.isEmpty() || 
                                 (orden.getEstado() != null && orden.getEstado().equalsIgnoreCase(estadoBusqueda));

            // 3. Filtro por Fecha de emisión obtenida del DatePicker
            boolean matchFecha = true;
            if (fechaCalendario != null) {
                matchFecha = orden.getFechaODC() != null && orden.getFechaODC().contains(fechaCalendario.toString());
            }

            return matchId && matchEstado && matchFecha;
        });
    }
    
    private void configurarColumnas() {
        // Vinculación clásica de atributos con PropertyValueFactory
        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idODC"));
        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("fechaODC"));
        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Convertimos la columna de estatus en un ComboBox interactivo en la tabla
        tlcEstatus.setCellFactory(ComboBoxTableCell.forTableColumn("Emitida", "Pendiente"));
        tlcEstatus.setEditable(true);
        
        // PERSISTENCIA EN CALIENTE EN LA NUBE: Al cambiar la celda, guardamos en Supabase
        tlcEstatus.setOnEditCommit(event -> {
            ODC orden = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            
            // Mandamos los cambios al método encargado del UPDATE en Supabase
            boolean exito = odcDAO.actualizarEstado(orden.getIdODC(), nuevoEstado);
            
            if (exito) {
                orden.setEstado(nuevoEstado);
            } else {
                // Si hay un fallo de conexión o base de datos, mostramos una alerta y mantenemos el valor previo
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Conexión");
                alert.setHeaderText("No se pudo actualizar el estado");
                alert.setContentText("Ocurrió un inconveniente al conectar con Supabase. Inténtelo más tarde.");
                alert.showAndWait();
            }
            tlvLista.refresh(); 
        });

        // Estilos de centrado para que las celdas luzcan ordenadas
        tlcIdOrden.setStyle("-fx-alignment: CENTER;");
        tlcEmision.setStyle("-fx-alignment: CENTER;");
        tlcEstatus.setStyle("-fx-alignment: CENTER;");
        tlcResponsable.setStyle("-fx-alignment: CENTER;");

        // Vinculación personalizada para mostrar el alias String del Responsable
        tlcResponsable.setCellValueFactory(cellData -> {
            Usuarios resp = cellData.getValue().getResponsable();
            return new javafx.beans.property.SimpleStringProperty(resp != null ? resp.getUsuario() : "N/A");
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar las opciones estáticas dentro del ComboBox de búsqueda superior
        cbEstado.setItems(FXCollections.observableArrayList("Emitida", "Pendiente"));

        // 1. DESCARGA INICIAL DESDE SUPABASE: Limpiamos y traemos los datos reales
        listaOriginal.clear();
        listaOriginal.addAll(odcDAO.obtenerTodas());
        
        // 2. Envolver los datos reales descargados en la lista de filtrado dinámico
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        
        // 3. Asignar los elementos e indicar que la tabla permite modificaciones directas
        tlvLista.setItems(listaFiltrada);
        tlvLista.setEditable(true);
        
        configurarColumnas();
    }
}
