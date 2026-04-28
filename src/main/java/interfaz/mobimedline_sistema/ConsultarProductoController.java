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

public class ConsultarProductoController implements Initializable {

    // --- Botones y Campos ---
    @FXML private Button BtnGuardar, btnBuscar, btnEliminar, btnLimpiar, btnModificar;
    @FXML private TextField txtBuscar, txtNombre, txtSku;

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
                txtSku.setText(newSelection.getSku());
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
        txtSku.clear();
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}