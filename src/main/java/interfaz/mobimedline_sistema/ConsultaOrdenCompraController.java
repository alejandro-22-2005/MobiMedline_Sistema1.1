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
    private TableColumn<Orden, String> tlcEmision;

    @FXML
    private TableColumn<Orden, String> tlcEstatus;

    @FXML
    private TableColumn<Orden, String> tlcIdOrden;

    @FXML
    private TableColumn<Orden, String> tlcResponsable;

    @FXML
    private TableView<Orden> tlvLista;
    
    private ObservableList<Orden> listaOriginal = FXCollections.observableArrayList();
    private FilteredList<Orden> listaFiltrada;
   
    @FXML
    private void handleVerDetalles(ActionEvent event) {
        Orden seleccion = tlvLista.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleccione una orden de la lista.");
            alert.showAndWait();
            return;
        }

        StringBuilder detalle = new StringBuilder();
        Map<String, Integer> conteoInsumosTotal = new HashMap<>();

        // Verificación de seguridad si la lista de productos es nula
        if (seleccion.getProductos() != null) {
            for (ProductoAsociado prod : seleccion.getProductos()) {
                for (Insumo insumo : prod.getInsumosRequeridos()) {
                    conteoInsumosTotal.put(insumo.getNombre(), 
                        conteoInsumosTotal.getOrDefault(insumo.getNombre(), 0) + insumo.getCantidad());
                }
            }
        }

        detalle.append("--- RESUMEN DE INSUMOS ---\n");
        conteoInsumosTotal.forEach((nombre, cantidad) -> 
            detalle.append("- ").append(nombre).append(": ").append(cantidad).append(" unidades\n"));

        detalle.append("\n--- PRODUCTOS ---\n");
        if (seleccion.getProductos() != null) {
            seleccion.getProductos().forEach(p -> detalle.append("• ").append(p.getNombre()).append("\n"));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles: " + seleccion.getId());
        alert.setHeaderText("Responsable: " + seleccion.getResponsable());
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
            boolean matchId = idBusqueda.isEmpty() || orden.getId().toLowerCase().contains(idBusqueda.toLowerCase());

            boolean matchEstado = estadoBusqueda.isEmpty() || orden.getEstatus().equalsIgnoreCase(estadoBusqueda);

            boolean matchFecha = (fechaBusqueda == null) || orden.getEmision().equals(fechaBusqueda.toString());

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
        listaFiltrada.setPredicate(orden -> true);

        // Opcional: Solicitar el foco en el primer campo de búsqueda
        tfIdOrden.requestFocus();
    }
    
    private void actualizarFiltro() {
        String idBusqueda = tfIdOrden.getText().trim().toLowerCase();
        String estadoBusqueda = (cbEstado.getValue() != null) ? cbEstado.getValue() : "";

        LocalDate fechaCalendario = dpFechaEmision.getValue();
        String textoFechaManual = dpFechaEmision.getEditor().getText().trim();

        listaFiltrada.setPredicate(orden -> {
            // Lógica de ID
            boolean matchId = idBusqueda.isEmpty() || orden.getId().toLowerCase().contains(idBusqueda);

            // Lógica de Estado
            boolean matchEstado = estadoBusqueda.isEmpty() || orden.getEstatus().equalsIgnoreCase(estadoBusqueda);

            // Lógica de Fecha (Prioriza calendario, luego texto manual)
            boolean matchFecha = true;
            if (fechaCalendario != null) {
                matchFecha = orden.getEmision().equals(fechaCalendario.toString());
            } else if (!textoFechaManual.isEmpty()) {
                matchFecha = orden.getEmision().contains(textoFechaManual);
            }

            return matchId && matchEstado && matchFecha;
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- 2. CARGA DE DATOS DE PRUEBA ---
        List<Insumo> packRed = Arrays.asList(new Insumo("Cable UTP Cat6", 50), new Insumo("Conectores RJ45", 100));
        List<Insumo> packServer = Arrays.asList(new Insumo("Memoria RAM 32GB", 4), new Insumo("Disco SSD 1TB", 2));

        ProductoAsociado rack = new ProductoAsociado("Rack de Comunicaciones", packRed);
        ProductoAsociado servidor = new ProductoAsociado("Servidor BD", packServer);

        listaOriginal.clear();
        listaOriginal.addAll(
            new Orden("ODC-001", "Juan Pérez", "2026-06-23", "Completada", Arrays.asList(rack, servidor)),
            new Orden("ODC-002", "Ana López", "2026-07-03", "Pendiente", Arrays.asList(servidor)),
            new Orden("ODC-003", "Carlos Ruíz", "2026-07-12", "Aprobada", new ArrayList<>())
        );

        // --- 3. CONFIGURACIÓN DE LA TABLA ---
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        tlvLista.setItems(listaFiltrada);

        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlcResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("emision"));
        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        
        /*agregado*/
        
        tlcIdOrden.setCellValueFactory(new PropertyValueFactory<>("id"));
        tlcIdOrden.setReorderable(false);

        tlcResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        tlcResponsable.setReorderable(false);

        tlcEmision.setCellValueFactory(new PropertyValueFactory<>("emision"));
        tlcEmision.setReorderable(false);

        tlcEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        tlcEstatus.setReorderable(false);
        

        // --- 4. CONFIGURACIÓN DE COMPONENTES DE BÚSQUEDA ---
        cbEstado.setItems(FXCollections.observableArrayList("Completada", "Pendiente", "Aprobada", "Cancelada"));

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
    public static class Orden {
        private String id;
        private String responsable;
        private String emision;
        private String estatus;
        private List<ProductoAsociado> productos; // Relación con productos

        public Orden(String id, String responsable, String emision, String estatus, List<ProductoAsociado> productos) {
            this.id = id;
            this.responsable = responsable;
            this.emision = emision;
            this.estatus = estatus;
            this.productos = productos;
        }
        // Getters...
        public List<ProductoAsociado> getProductos() { return productos; }
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
    }
    
}
