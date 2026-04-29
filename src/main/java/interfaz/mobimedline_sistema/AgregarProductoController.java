package interfaz.mobimedline_sistema;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import interfaz.mobimedline_sistema.Producto;

public class AgregarProductoController {//clase para agregar porductos

    @FXML 
    private TextField txtCodigo;
    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtNombreInsumo;
    @FXML 
    private TextField txtCantidadInsumo;
    @FXML 
    private TableView<Insumo> tablaInsumos;
    @FXML 
    private TableColumn<Insumo, String> colNombreInsumo;
    @FXML 
    private TableColumn<Insumo, Integer> colCantidadInsumo;
    @FXML 
    private TableColumn<Insumo, Void> colAcciones;
    
    private ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList();
    

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colNombreInsumo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidadInsumo.setCellValueFactory(new PropertyValueFactory<>("cantidadPorUnidad"));
        
        // Configurar columna de acciones (botón eliminar)
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            
            {
                btnEliminar.setOnAction(event -> {
                    Insumo insumo = getTableView().getItems().get(getIndex());
                    listaInsumos.remove(insumo);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
        
        tablaInsumos.setItems(listaInsumos);
    }

    @FXML
    private void agregarInsumo() {
        // Validar campos de insumo
        if (txtNombreInsumo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa el nombre del insumo.");
            return;
        }
        
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidadInsumo.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cantidad inválida", 
                    "La cantidad debe ser un número entero positivo.\nEjemplo: 10");
            return;
        }
        
        // Crear y agregar insumo
        Insumo insumo = new Insumo(txtNombreInsumo.getText().trim(), cantidad);
        listaInsumos.add(insumo);
        
        // Limpiar campos de insumo
        txtNombreInsumo.clear();
        txtCantidadInsumo.clear();
        txtNombreInsumo.requestFocus();
    }

    @FXML
    private void guardarProducto() {
        // Validar campos del producto
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", 
                    "Por favor completa el código y nombre del producto.");
            return;
        }
        
        if (listaInsumos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin insumos", 
                    "Debes agregar al menos un insumo al producto.");
            return;
        }
        
        // Crear el producto
        Producto producto = new Producto(txtNombre.getText().trim());
        
        // Agregar todos los insumos
        for (Insumo insumo : listaInsumos) {
            producto.agregarInsumo(insumo);
        }
        
        //Con esto se manda los recibido de agregar a producto a catalogobasdeproductos y de catalogo se muestre a el procuto nuevo
        CatalogoProductosBase.setProductosBase(producto);//Para mostrar en catalogo se agregaa al base de catalogo de productos// Autor:Alejandro
        
        // Mostrar resumen
        StringBuilder resumen = new StringBuilder();
        resumen.append("Producto guardado exitosamente:\n\n");
        resumen.append("SKU: ").append(producto.getSku()).append("\n");
        resumen.append("Nombre: ").append(producto.getDescripcion()).append("\n");
        //resumen.append("Total de insumos: ").append(producto.getTotalInsumos()).append("\n\n");
        resumen.append("Lista de insumos:\n");
        
        for (Insumo insumo : producto.getInsumos()) {
            resumen.append("  • ").append(insumo.getNombre())
                   .append(" - ").append(insumo.getCantidadPorUnidad()).append(" unidades\n");
        }
        
        mostrarAlerta(Alert.AlertType.INFORMATION, "Producto guardado", resumen.toString());
        
        // Limpiar todo después de guardar
        limpiarTodo();
    }

    @FXML
    private void limpiar() {
        // Mostrar confirmación antes de limpiar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar formulario");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas limpiar todos los datos?\nLos datos ingresados se perderán.");
        
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                limpiarTodo();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Formulario limpiado", 
                        "Todos los datos han sido eliminados correctamente.");
            }
        });
    }

    private void limpiarTodo() {
        
        txtNombre.clear();
        txtNombreInsumo.clear();
        txtCantidadInsumo.clear();
        listaInsumos.clear();
        
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}