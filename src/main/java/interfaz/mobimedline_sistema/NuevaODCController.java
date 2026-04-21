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

public class NuevaODCController implements Initializable {
    
    @FXML
    private TextField txtFieldID;

    @FXML
    private TextField txtFieldCantidad;

    @FXML
    private TableView<Productov> tablevTabla;

    @FXML
    private TableColumn<Productov, Integer> tcId;

    @FXML
    private TableColumn<Productov, String> tcDescripcion;

    @FXML
    private TableColumn<Productov, Integer> tcCantidad;

    @FXML
    private Button btnMas1;

    @FXML
    private Button btnSave;

   
   @FXML
    void eliminarProducto(ActionEvent event) {

        Productov productoSeleccionado = tablevTabla.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
        mostrarAlerta("Sin selección", "Debes seleccionar un producto de la tabla para eliminarlo.");
        return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Deseas eliminar el producto con ID " 
            + productoSeleccionado.getId() + "?");

        if (confirmacion.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            listaProductos.remove(productoSeleccionado);
            }
    }

    

    private final ObservableList<Productov> listaProductos = FXCollections.observableArrayList();
    private final Catalogov Catalogo = new Catalogov();

    private void configurarTabla() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
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

        int id;
        int cantidad;

        try {
            id = Integer.parseInt(textoId);
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            mostrarAlerta("Dato inválido", "El ID y la cantidad deben ser números enteros.");
            return;
        }

        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }

        ProductoBasev productoBase = Catalogo.buscarPorId(id);

        if (productoBase == null) {
            mostrarAlerta("Producto no encontrado", "No existe un producto con el ID: " + id);
            return;
        }

        Productov productoODC = new Productov(
                productoBase.getId(),
                productoBase.getDescripcion(),
                cantidad,
                new ArrayList<>(productoBase.getInsumos())
        );

        listaProductos.add(productoODC);

        txtFieldID.clear();
        txtFieldCantidad.clear();
        txtFieldID.requestFocus();
    }

    private void guardarODC() {
    if (listaProductos.isEmpty()) {
        mostrarAlerta("Lista vacía", "No hay productos en la lista.");
        return;
    }

    StringBuilder contenido = new StringBuilder();
    contenido.append("PRODUCTOS REGISTRADOS EN LA LISTA\n\n");

    for (Productov producto : listaProductos) {
        contenido.append("ID: ").append(producto.getId()).append("\n");
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