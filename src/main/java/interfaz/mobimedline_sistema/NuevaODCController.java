package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class NuevaODCController implements Initializable {
    
    @FXML
    private TextField txtFieldID;

    @FXML
    private TextField txtFieldCantidad;

    @FXML
    private TableView<Producto> tablevTabla;

    @FXML
    private TableColumn<Producto, String> tcId;

    @FXML
    private TableColumn<Producto, String> tcDescripcion;

    @FXML
    private TableColumn<Producto, Integer> tcCantidad;

    @FXML
    private Button btnMas1;

    @FXML
    private Button btnSave;

   
   @FXML
    void eliminarProducto(ActionEvent event) {

        Producto productoSeleccionado = tablevTabla.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
        mostrarAlerta("Sin selección", "Debes seleccionar un producto de la tabla para eliminarlo.");
        return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Deseas eliminar el producto con ID " 
            + productoSeleccionado.getSku() + "?");

        if (confirmacion.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            listaProductos.remove(productoSeleccionado);
            }
    }

    

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    //private final Catalogov Catalogo = new Catalogov();

    private void configurarTabla() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("sku"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tcCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tablevTabla.setItems(listaProductos);
    }

    private void configurarEventos() {
        btnMas1.setOnAction(e -> agregarProducto());
        btnSave.setOnAction(e -> guardarODC());
    }

    private void agregarProducto() {
        String textoId = txtFieldID.getText().trim();
        String textoCantidad = txtFieldCantidad.getText().trim();

        if (textoId.isEmpty() || textoCantidad.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debes ingresar el ID y la cantidad.");
            return;
        }

        String id; //cambiar etiqueta
        int cantidad;

        try {//excepciones
            id = textoId;
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            mostrarAlerta("Dato inválido", "El ID y la cantidad deben ser números enteros.");
            return;
        }

        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }

        Producto productoCopia = CatalogoProductosBase.buscarPorSku(textoId);

        if (productoCopia == null) {
            mostrarAlerta("Producto no encontrado", "No existe un producto con el ID: " + id);
            return;
        }

        Producto productoODC = new Producto(
                productoCopia.getSku(),
                productoCopia.getDescripcion(),
                cantidad,
                new ArrayList<>(productoCopia.getInsumos())
        );

        listaProductos.add(productoODC);

        txtFieldID.clear();
        txtFieldCantidad.clear();
        txtFieldID.requestFocus();
    }

    /*private void guardarODC() {
    if (listaProductos.isEmpty()) {
        mostrarAlerta("Lista vacía", "No hay productos en la lista.");
        return;
    }

    StringBuilder contenido = new StringBuilder();
    contenido.append("PRODUCTOS REGISTRADOS EN LA LISTA\n\n");

    for (Producto producto : listaProductos) {
        contenido.append("SKU: ").append(producto.getSku()).append("\n");
        contenido.append("Descripción: ").append(producto.getDescripcion()).append("\n");
        contenido.append("Cantidad: ").append(producto.getCantidad()).append("\n");
        contenido.append("-----------------------------\n");
    }

    TextArea areaTexto = new TextArea(contenido.toString());
    areaTexto.setEditable(false);
    areaTexto.setWrapText(true);

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Lista guardada");
    alert.setHeaderText("Productos agregados correctamente");
    alert.getDialogPane().setContent(areaTexto);
    alert.showAndWait();
    
    listaProductos.clear();
    
}*/
    
 private void guardarODC() {
    if (listaProductos.isEmpty()) {
        mostrarAlerta("Lista vacía", "No hay productos en la lista.");
        return;
    }

    //Obtener usuarios (para asignar responsable)
    List<Usuarios> usuarios = AgendaUsuariosBase.getUsuariosBase();

    //Crear nueva ODC
    ODC nuevaODC = new ODC(
        usuarios.get(0), // responsable
        java.time.LocalDate.now().toString(),
        "Pendiente"
    );

    //Agregar productos a la ODC
    for (Producto producto : listaProductos) {
        nuevaODC.actualizarOAgregarProducto(producto, producto.getCantidad());
    }

    // Guardado en la base
    ArchivoOdcBase.agregarODC(nuevaODC);

    // Confirmación
    mostrarInformacion(
        "ODC guardada",
        "Se guardó la orden: " + nuevaODC.getIdODC()
    );

    // Limpiar tabla
    listaProductos.clear();
}

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarEventos();
    }
}