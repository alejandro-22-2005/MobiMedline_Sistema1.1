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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione una orden de la lista.");
            alert.showAndWait();
            return;
        }

        StringBuilder detalle = new StringBuilder();
        Map<String, Integer> conteoInsumosTotal = new HashMap<>();
        
        List<ODC> copiaODC = ArchivoOdcBase.getOdcBase();
        List<Producto> copiaProductos = CatalogoProductosBase.getProductosBase();

        // Verificación de seguridad si la lista de productos es nula
        if (seleccion.getProductos() != null) {
            for (Producto producto : seleccion.getProductos()) {
                for (Insumo insumo : producto.getInsumos()) {
                    conteoInsumosTotal.put(insumo.getNombre(), 
                        conteoInsumosTotal.getOrDefault(insumo.getNombre(), 0) + insumo.getCantidadPorUnidad());
                }
            }
        }

        detalle.append("--- RESUMEN DE INSUMOS ---\n");
        conteoInsumosTotal.forEach((nombre, cantidad) -> 
            detalle.append("- ").append(nombre).append(": ").append(cantidad).append(" unidades\n"));

        detalle.append("\n--- PRODUCTOS ---\n");
        if (seleccion.getProductos() != null) {
            seleccion.getProductos().forEach(p -> detalle.append("• ").append(p.getDescripcion()).append("\n"));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles: " + seleccion.getIdODC());
        alert.setHeaderText("responsable: " + seleccion.getResponsable().getUsuario());
        alert.setContentText(detalle.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handleBuscar(ActionEvent event) {
        String idBusqueda = tfIdOrden.getText().trim();
        LocalDate fechaBusqueda = dpFechaEmision.getValue();
        Object estadoObj = cbEstado.getValue();
        String estadoBusqueda = (estadoObj != null) ? estadoObj.toString() : "";

        // Validar campos vacíos
        if (idBusqueda.isEmpty() && fechaBusqueda == null && estadoBusqueda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Por favor, llene al menos un campo para realizar la búsqueda.");
            alert.showAndWait();
            // Opcional: listaFiltrada.setPredicate(p -> true); // Mostrar todo si está vacío
            return;
        }

        // Aplicar filtro (Esto siempre sobreescribe el filtro anterior, no lo encadena)
        listaFiltrada.setPredicate(orden -> {
            boolean matchId = idBusqueda.isEmpty() || orden.getIdODC().toLowerCase().contains(idBusqueda.toLowerCase());

            boolean matchEstado = estadoBusqueda.isEmpty() || orden.getEstado().equalsIgnoreCase(estadoBusqueda);

            boolean matchFecha = (fechaBusqueda == null) || orden.getFechaODC().equals(fechaBusqueda.toString());

            return matchId && matchEstado && matchFecha;
        });
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

        LocalDate fechaCalendario = dpFechaEmision.getValue();
        String textoFechaManual = dpFechaEmision.getEditor().getText().trim();

        listaFiltrada.setPredicate(ODC -> {
            // Lógica de ID
            boolean matchId = idBusqueda.isEmpty() || ODC.getIdODC().toLowerCase().contains(idBusqueda);

            // Lógica de Estado
            boolean matchEstado = estadoBusqueda.isEmpty() || ODC.getEstado().equalsIgnoreCase(estadoBusqueda);

            // Lógica de Fecha (Prioriza calendario, luego texto manual)
            boolean matchFecha = true;
            if (fechaCalendario != null) {
                matchFecha = ODC.getFechaODC().equals(fechaCalendario.toString());
            } else if (!textoFechaManual.isEmpty()) {
                matchFecha = ODC.getFechaODC().contains(textoFechaManual);
            }

            return matchId && matchEstado && matchFecha;
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- 2. CARGA DE DATOS DE PRUEBA ---
        listaOriginal.clear();
        List<ODC> lista = ArchivoOdcBase.getOdcBase();
        for(ODC o: lista){
            listaOriginal.add(o);
        }

        // --- 3. CONFIGURACIÓN DE LA TABLA ---
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        tlvLista.setItems(listaFiltrada);

        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idODC"));
        tlcResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("fechaODC"));
        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        /*agregado*/
        
        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idODC"));
        tlcIdOrden.setReorderable(false);

        tlcResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        tlcResponsable.setReorderable(false);

        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("fechaODC"));
        tlcEmision.setReorderable(false);

        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tlcEstatus.setReorderable(false);
        

        // --- 4. CONFIGURACIÓN DE COMPONENTES DE BÚSQUEDA ---
        cbEstado.setItems(FXCollections.observableArrayList("Emitida", "Pendiente"));

        // Listener para el ID (Búsqueda en tiempo real mientras escribes)
        tfIdOrden.textProperty().addListener((obs, old, newValue) -> actualizarFiltro());

        // Listener para el ComboBox de Estado
        cbEstado.valueProperty().addListener((obs, old, newValue) -> actualizarFiltro());

        // Listener para el Calendario (Cuando eliges una fecha con el mouse)
        dpFechaEmision.valueProperty().addListener((obs, old, newValue) -> actualizarFiltro());

        // Listener para el Editor del DatePicker (Cuando escribes la fecha o la borras)
        dpFechaEmision.getEditor().textProperty().addListener((obs, old, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                dpFechaEmision.setValue(null); // Sincroniza el valor interno si se borra el texto
            }
            actualizarFiltro();
        });
    }  
    
    // Clase Orden actualizada
    /*public static class Orden {
        private String id;
        private String responsable;
        private String emision;
        private String estatus;
        private List<Producto> productos; // Relación con productos

        public Orden(String id, String responsable, String emision, String estatus, List<Producto> productos) {
            this.id = id;
            this.responsable = responsable;
            this.emision = emision;
            this.estatus = estatus;
            this.productos = productos;
        }
        // Getters...
        public List<Producto> getProductos() { return productos; }
        public String getId() { return id; }
        public String getResponsable() { return responsable; }
        public String getEmision() { return emision; }
        public String getEstatus() { return estatus; }
    }
    
    // Clase para los Insumos (ej. Tornillos, Cables, Pasta térmica)
    public static class Insumo {
        private String nombre;
        private int cantidad;

        public Insumo(String nombre, int cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }
        public String getNombre() { return nombre; }
        public int getCantidad() { return cantidad; }
    }

    // Clase para los Productos (ej. Servidor, PC Gamer, Tablero Eléctrico)
    public static class ProductoAsociado {
        private String nombre;
        private List<Insumo> insumosRequeridos;

        public ProductoAsociado(String nombre, List<Insumo> insumos) {
            this.nombre = nombre;
            this.insumosRequeridos = insumos;
        }
        public String getNombre() { return nombre; }
        public List<Insumo> getInsumosRequeridos() { return insumosRequeridos; }
    }*/
    
}
