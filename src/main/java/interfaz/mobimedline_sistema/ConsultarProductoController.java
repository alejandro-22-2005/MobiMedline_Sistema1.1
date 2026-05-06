package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


// se modifica esta clase 

public class ConsultarProductoController implements Initializable {

    // --- Botones y Campos ---
    @FXML private Button BtnGuardar, btnBuscar, btnEliminar, btnLimpiar, btnModificar;
    @FXML private TextField txtBuscar, txtNombre;

    // --- Tabla de Productos (Izquierda) ---
    @FXML private TableView<Producto> tvProductos;
    @FXML private TableColumn<Producto, String> colSKU;          // Muestra el SKU del Producto
    @FXML private TableColumn<Producto, String> colDescripcion;  // Muestra el nombre/descripción del Producto

    // --- Tabla de Insumos (Abajo/Derecha) ---
    @FXML private TableView<Insumo> tvInsumos;
    @FXML private TableColumn<Insumo, String> colID;             // ID del Insumo
    @FXML private TableColumn<Insumo, String> colNombre;         // Nombre del Insumo
    @FXML private TableColumn<Insumo, Integer> colUnidad;        // Cantidad del Insumo

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Vinculación de la tabla de PRODUCTOS
        colSKU.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Vinculación de la tabla de INSUMOS
        // IMPORTANTE: Estos nombres deben coincidir con los atributos de la clase Insumo
        colID.setCellValueFactory(new PropertyValueFactory<>("idInsumo")); 
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        // Si en tu clase Insumo el atributo es 'cantidad', pon "cantidad" aquí:
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("cantidad")); 

        // Listener de selección
        tvProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getDescripcion());

                // Forzar el refresco de la tabla de insumos
                if (newSelection.getInsumos() != null) {
                    ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList(newSelection.getInsumos());
                    tvInsumos.setItems(listaInsumos);
                    tvInsumos.refresh(); // Línea extra para asegurar que se pinte
                } else {
                    tvInsumos.getItems().clear();
                }
            }
        });

        refrescarLista();
    }

    @FXML
    void buscar(ActionEvent event) {
        String texto = txtBuscar.getText();
        if (texto == null || texto.trim().isEmpty()) {
            refrescarLista();
            return;
        }

        texto = texto.toLowerCase();
        ObservableList<Producto> filtrados = FXCollections.observableArrayList();

        for (Producto p : CatalogoProductosBase.getProductosBase()) {
            if (p.getDescripcion().toLowerCase().contains(texto) || p.getSku().toLowerCase().contains(texto)) {
                filtrados.add(p);
            }
        }
        tvProductos.setItems(filtrados);
    }

    @FXML
    void limpiar(ActionEvent event) {
        txtBuscar.clear();
        txtNombre.clear();
        tvInsumos.getItems().clear();
        refrescarLista();
    }

    @FXML
    void eliminar(ActionEvent event) {
        Producto seleccionado = tvProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la tabla para eliminar.");
            return;
        }
        CatalogoProductosBase.getProductosBase().remove(seleccionado);
        limpiar(event);
        mostrarAlerta("Producto eliminado del catálogo.");
    }

    private void refrescarLista() {
        tvProductos.setItems(FXCollections.observableArrayList(CatalogoProductosBase.getProductosBase()));
    }

    @FXML
    void modificar(ActionEvent event) {
        Producto seleccionado = tvProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Por favor, selecciona un producto de la tabla para modificar.");
            return;
        }

        // Habilitamos el campo para que el usuario pueda escribir
        txtNombre.setEditable(true);
        txtNombre.requestFocus();

        // Opcional: Podrías cambiar el color del campo para indicar edición
        txtNombre.setStyle("-fx-border-color: #3498db; -fx-background-color: white;");
    }
    
    @FXML
    void guardar(ActionEvent event) {
        Producto seleccionado = tvProductos.getSelectionModel().getSelectedItem();
        String nuevoNombre = txtNombre.getText().trim();

        if (seleccionado == null) {
            mostrarAlerta("No hay ningún producto seleccionado para guardar cambios.");
            return;
        }

        if (nuevoNombre.isEmpty()) {
            mostrarAlerta("El nombre del producto no puede estar vacío.");
            return;
        }

        // 1. Actualizamos el atributo en el objeto real
        seleccionado.setDescripcion(nuevoNombre);

        // 2. Deshabilitamos la edición nuevamente
        txtNombre.setEditable(false);
        txtNombre.setStyle(null); // Restaurar estilo original

        // 3. Refrescamos la tabla para que se vea el cambio inmediatamente
        tvProductos.refresh();

        mostrarAlerta("Producto actualizado correctamente.");
    }
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}