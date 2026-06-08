package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Popup;

public class NuevaODCController implements Initializable {

    @FXML
    private TextField txtFieldID;

    @FXML
    private TextField txtFieldProducto;

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

    private static final int MAX_PRODUCTOS = 10;

    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private FilteredList<Producto> filteredProducts;
    private ListView<Producto> suggestionList;
    private Popup suggestionPopup;
    
    // Instanciamos el DAO encargado de la conexión con Supabase
    private final ODCDAO odcDAO = new ODCDAO();
    
    private boolean esEmpleado() {
        return !IniciarSesController.usuarioActual.getPermisos();
    }

    @FXML
    void eliminarProducto(ActionEvent event) {
        Producto productoSeleccionado = tablevTabla.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta("Sin selección","Debes seleccionar un producto de la tabla para eliminarlo."
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Deseas eliminar el producto con ID " + productoSeleccionado.getSku()+ "?");

        if (confirmacion.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            listaProductos.remove(productoSeleccionado);
        }
    }

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
            mostrarAlerta("Campos vacíos",
                    "Debes ingresar el ID y la cantidad.");
            return;
        }
        int cantidad;
        try {
            max_unidades_try:
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            mostrarAlerta("Dato inválido",
                    "La cantidad debe ser un número entero.");
            return;
        }
        if (cantidad <= 0) {
            mostrarAlerta("Cantidad inválida","La cantidad debe ser mayor que cero.");
            return;
        }
        
        for (Producto p : listaProductos) {
            if (p.getSku().equals(textoId)) {
                if (esEmpleado()&& p.getCantidad() + cantidad > MAX_PRODUCTOS) {
                    mostrarAlerta("Límite alcanzado","No puedes tener más de "+ MAX_PRODUCTOS+ 
                            " unidades de este producto.");
                    return;
                }
                p.setCantidad(p.getCantidad() + cantidad);
                tablevTabla.refresh();
                return;
            }
        }
        
        Producto productoCopia = CatalogoProductosBase.buscarPorSku(textoId);
        if (productoCopia == null) {
            mostrarAlerta("Producto no encontrado","No existe un producto con el ID: "+ textoId);
            return;
        }
        if (esEmpleado() && cantidad > MAX_PRODUCTOS) {
            mostrarAlerta("Límite alcanzado","No puedes agregar más de "+ MAX_PRODUCTOS+ " unidades.");
            return;
        }
        
        Producto productoODC = new Producto(productoCopia.getSku(),productoCopia.getDescripcion(),cantidad,
                new ArrayList<>(productoCopia.getInsumos()));
        listaProductos.add(productoODC);

        txtFieldID.clear();
        txtFieldProducto.clear();
        txtFieldCantidad.clear();
        txtFieldProducto.requestFocus();
    }

    private void guardarODC() {
        if (listaProductos.isEmpty()) {
            mostrarAlerta("Lista vacía","No hay productos en la lista.");
            return;
        }

        ODC nuevaODC = new ODC(IniciarSesController.usuarioActual, java.time.LocalDate.now().toString(),"Pendiente");
        for (Producto producto : listaProductos) {
            nuevaODC.actualizarOAgregarProducto(producto,producto.getCantidad()
            );
        }
        
        // 2. Intentamos realizar la inserción transaccional por lotes en la base de datos
        boolean exitoBD = odcDAO.insertar(nuevaODC);
        
        if (exitoBD) {
            // 3. Si la base de datos procesó todo correctamente, actualizamos el Dashboard visual
            /*
            DashboardController.lista.add(new DashboardController.Orden(
                    nuevaODC.getIdODC(),
                    nuevaODC.getFechaODC(),
                    nuevaODC.getResponsable().getUsuario(),
                    nuevaODC.getEstado()
            ));
            */
            mostrarInformacion("ODC registrada con éxito", "Se guardó correctamente la orden: " + nuevaODC.getIdODC());
            
            // Limpiamos la memoria interna del formulario únicamente al confirmar persistencia
            listaProductos.clear();
        } else {
            // En caso de corte de red o error de BD, alertamos y mantenemos los datos intactos en la tabla para no perder el progreso
            mostrarAlerta("Error de persistencia", "No se pudo registrar la ODC en la base de datos. Verifique su conexión.");
        }
    }

    private void configurarBusquedaProducto() {
        ObservableList<Producto> productosBase = FXCollections.observableArrayList(CatalogoProductosBase.getProductosBase());
        filteredProducts = new FilteredList<>(productosBase, p -> true);
        suggestionList = new ListView<>(filteredProducts);
        suggestionList.setCellFactory(lv -> new ListCell<Producto>() {

            @Override
            protected void updateItem(Producto item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion()
                    );
                }
            }
        });
        suggestionList.setPrefWidth(
                txtFieldProducto.getPrefWidth()
        );
        suggestionList.setMaxHeight(150);
        suggestionPopup = new Popup();
        suggestionPopup.getContent().add(suggestionList);
        suggestionPopup.setAutoHide(true);
        txtFieldProducto.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower =(newVal == null)? "": newVal.toLowerCase();
            filteredProducts.setPredicate(p -> {
                if (lower.isEmpty()) {
                    return true;
                }
                String descripcion = p.getDescripcion() != null
                        ? p.getDescripcion().toLowerCase(): "";
                String sku = p.getSku() != null? p.getSku().toLowerCase(): "";
                return descripcion.contains(lower) || sku.contains(lower);
            });
            if (!filteredProducts.isEmpty()&& !newVal.isEmpty()) {
                        Bounds bounds = txtFieldProducto.localToScreen(
                                txtFieldProducto.getBoundsInLocal());
                        if (bounds != null) {
                            suggestionPopup.show(
                                    txtFieldProducto,bounds.getMinX(),bounds.getMaxY()
                            );
                        }
            }else{
                suggestionPopup.hide();
            }
        });
        txtFieldProducto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                suggestionPopup.hide();
            }
        });
        suggestionList.setOnMouseClicked(e -> {
            Producto selected =suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtFieldProducto.setText(
                        selected.getDescripcion()
                );
                txtFieldID.setText(selected.getSku()
                );
                suggestionPopup.hide();
                txtFieldCantidad.requestFocus();
            }
        });
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
    public void initialize(URL url,ResourceBundle resourceBundle) {
        configurarTabla();
        configurarEventos();
        configurarBusquedaProducto();
    }
}