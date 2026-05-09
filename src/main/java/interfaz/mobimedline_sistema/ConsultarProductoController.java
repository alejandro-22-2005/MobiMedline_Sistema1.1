package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

// se modifica esta clase 

public class ConsultarProductoController implements Initializable {

    // --- Botones y Campos ---
    @FXML private Button BtnGuardar, btnBuscar, btnEliminar, btnLimpiar, btnModificar;

    @FXML private TextField txtBuscar;

    // --- Tabla de Productos (Izquierda) ---
    @FXML private TableView<Producto> tvProductos;

    @FXML private TableColumn<Producto, String> colSKU;

    @FXML private TableColumn<Producto, String> colDescripcion;

    // --- Tabla de Insumos (Derecha) ---
    @FXML private TableView<Insumo> tvInsumos;

    @FXML private TableColumn<Insumo, String> colID;

    @FXML private TableColumn<Insumo, String> colNombre;

    @FXML private TableColumn<Insumo, Integer> colUnidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // PRODUCTOS
        colSKU.setCellValueFactory(
                new PropertyValueFactory<>("sku")
        );

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        // INSUMOS
        colID.setCellValueFactory(
                new PropertyValueFactory<>("idInsumo")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        // Muestra la cantidad en la tabla derecha
        colUnidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        // Habilitar edición de tablas
        tvProductos.setEditable(true);

        tvInsumos.setEditable(true);

        // EDITAR DESCRIPCIÓN PRODUCTO
        colDescripcion.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        colDescripcion.setOnEditCommit(event -> {

            Producto producto = event.getRowValue();

            producto.setDescripcion(
                    event.getNewValue()
            );

            tvProductos.refresh();

            mostrarAlerta(
                    "Descripción del producto actualizada."
            );
        });

        // EDITAR NOMBRE INSUMO
        colNombre.setCellFactory(
                TextFieldTableCell.forTableColumn()
        );

        colNombre.setOnEditCommit(event -> {

            Insumo insumo = event.getRowValue();

            insumo.setNombre(
                    event.getNewValue()
            );

            tvInsumos.refresh();

            mostrarAlerta(
                    "Nombre del insumo actualizado."
            );
        });

        // EDITAR CANTIDAD
        colUnidad.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new IntegerStringConverter()
                )
        );

        colUnidad.setOnEditCommit(event -> {

            Insumo insumo = event.getRowValue();

            insumo.setCantidad(
                    event.getNewValue()
            );

            tvInsumos.refresh();

            mostrarAlerta(
                    "Cantidad actualizada."
            );
        });

        // Listener de selección
        tvProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if (newSelection != null) {

                if (newSelection.getInsumos() != null) {

                    ObservableList<Insumo> listaInsumos =
                            FXCollections.observableArrayList(
                                    newSelection.getInsumos()
                            );

                    tvInsumos.setItems(listaInsumos);

                    tvInsumos.refresh();

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

        ObservableList<Producto> filtrados =
                FXCollections.observableArrayList();

        for (Producto p : CatalogoProductosBase.getProductosBase()) {

            if (p.getDescripcion().toLowerCase().contains(texto)
                    || p.getSku().toLowerCase().contains(texto)) {

                filtrados.add(p);
            }
        }

        tvProductos.setItems(filtrados);
    }

    @FXML
    void limpiar(ActionEvent event) {

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar limpieza");

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Estás seguro de limpiar los campos?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            txtBuscar.clear();

            tvInsumos.getItems().clear();

            tvProductos.getSelectionModel().clearSelection();

            tvProductos.refresh();

            refrescarLista();

            mostrarAlerta(
                    "Campos limpiados correctamente."
            );
        }
    }

    @FXML
    void eliminar(ActionEvent event) {

        Producto seleccionado =
                tvProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    "Selecciona un producto de la tabla para eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Eliminar Producto");

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Estás seguro de eliminar el producto:\n\n"
                + seleccionado.getDescripcion() + "?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            CatalogoProductosBase
                    .getProductosBase()
                    .remove(seleccionado);

            txtBuscar.clear();

            tvInsumos.getItems().clear();

            tvProductos.getSelectionModel().clearSelection();

            refrescarLista();

            mostrarAlerta(
                    "Producto eliminado del catálogo."
            );
        }
    }

    private void refrescarLista() {

        tvProductos.setItems(
                FXCollections.observableArrayList(
                        CatalogoProductosBase.getProductosBase()
                )
        );

        tvProductos.refresh();
    }

    @FXML
    void modificar(ActionEvent event) {

        Producto seleccionado =
                tvProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    "Selecciona un producto para modificar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Modificar Producto");

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "Ahora podrás editar:\n\n"
                + "- Descripción del producto\n"
                + "- Nombre del insumo\n"
                + "- Cantidad\n\n"
                + "Haz doble clic sobre una celda."
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            tvProductos.setEditable(true);

            tvInsumos.setEditable(true);

            mostrarAlerta(
                    "Modo edición activado."
            );
        }
    }

    @FXML
    void guardar(ActionEvent event) {

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Guardar Cambios");

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Deseas guardar los cambios realizados?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            tvProductos.refresh();

            tvInsumos.refresh();

            mostrarAlerta(
                    "Cambios guardados correctamente."
            );
        }
    }

    private void mostrarAlerta(String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Información");

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}