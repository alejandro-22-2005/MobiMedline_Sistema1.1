package interfaz.mobimedline_sistema;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * clase para agregar porductos
 * @author Alejandro
 */
public class AgregarProductoController {

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
    
    // Instancias de acceso a datos distribuidas
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final InsumoDAO insumoDAO = new InsumoDAO();
    
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
        
        String nombreNuevo = txtNombreInsumo.getText().trim();
        
        // Validar campos de insumo
        if (txtNombreInsumo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío", "Por favor ingresa el nombre del insumo.");
            return;
        }
        
        // Nueva validación de primera letra en mayúscula
        try {
            validarPrimeraMayuscula(nombreNuevo);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato inválido", e.getMessage());
            return;
        }
        
        //String nombreFormateado = capitalizar(nombreNuevo);
        
        //Este se hizo para que cuando se captures un insumo pase por una condcional en el cual 
        //se evaluara si es existente o es nuevo
        boolean yaExiste = listaInsumos.stream()
            .anyMatch(i -> i.getNombre().equalsIgnoreCase(nombreNuevo));
    
        if (yaExiste) {
        mostrarAlerta(Alert.AlertType.WARNING, "Insumo duplicado", 
                "Este insumo ya ha sido agregado a la lista actual.");
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
        
        List<Insumo> todosLosInsumosBD = insumoDAO.listar();
        Insumo insumoExistenteBD = todosLosInsumosBD.stream()
            .filter(i -> i.getNombre().equalsIgnoreCase(nombreNuevo))
            .findFirst()
            .orElse(null);
            
        Insumo insumoParaTabla;
        if (insumoExistenteBD != null) {
            // Si ya existe en la BD, reciclamos su ID único real y le asociamos la nueva cantidad para esta receta
            insumoParaTabla = new Insumo(insumoExistenteBD.getIdInsumo(), insumoExistenteBD.getNombre(), cantidad);
        } else {
            // Si es un insumo completamente nuevo, creamos uno temporal sin ID asignado aún 
            // (Se insertará en catálogo o se creará en cascada)
            insumoParaTabla = new Insumo(nombreNuevo, cantidad);
            
            // Opcional: registrarlo inmediatamente en el catálogo global de insumos de la BD
            insumoDAO.insertar(insumoParaTabla);
            // Volvemos a consultar para recuperar el ID autogenerado de la identidad de Supabase
            insumoExistenteBD = insumoDAO.listar().stream()
                .filter(i -> i.getNombre().equalsIgnoreCase(nombreNuevo))
                .findFirst()
                .orElse(insumoParaTabla);
                
            insumoParaTabla.setIdInsumo(insumoExistenteBD.getIdInsumo());
        }
        
        // Agregar insumo a nuestra lista observable vinculada al TableView
        listaInsumos.add(insumoParaTabla);
        
        // Limpiar campos de insumo
        txtNombreInsumo.clear();
        txtCantidadInsumo.clear();
        txtNombreInsumo.requestFocus();
    }

    @FXML
    private void guardarProducto() {
        
        String nombreProd = txtNombre.getText().trim();
        
        // Validar campos del producto
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", 
                    "Por favor ingresa el nombre del producto.");
            return;
        }
        
        if (listaInsumos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin insumos", 
                    "Debes agregar al menos un insumo al producto.");
            return;
        }
        
        //Se modifico esto 
        try {
            validarPrimeraMayuscula(nombreProd);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato inválido", e.getMessage());
            return;
        }
        
        //Este se hizo para que cuando se captures un producto pase por una condcional en el cual 
        //se evaluara si es existente o es nuevo
        boolean productoRepetido = productoDAO.listar().stream()
            .anyMatch(p -> p.getDescripcion().equalsIgnoreCase(nombreProd));

        if (productoRepetido) {
            mostrarAlerta(Alert.AlertType.ERROR, "Producto existente", 
                    "Ya existe un producto registrado con el nombre: " + nombreProd);
            return;
        }
        
        // Crear el producto
        Producto producto = new Producto(txtNombre.getText().trim());
        
        // Agregar todos los insumos
        for (Insumo insumo : listaInsumos) {
            producto.agregarInsumo(insumo);
        }
        
        // === PERSISTENCIA REMOTA ===
        // Mandamos el objeto completo al ProductoDAO para que guarde en PRODUCTO y en PRODUCTO_INSUMO por lotes
        boolean guardadoExitoso = productoDAO.insertar(producto);
        
        if (guardadoExitoso) {
            // Recuperamos el listado actualizado para obtener el SKU autogenerado por Supabase
            List<Producto> listaActualizada = productoDAO.listar();
            Producto productoGuardadoReal = listaActualizada.stream()
                .filter(p -> p.getDescripcion().equalsIgnoreCase(nombreProd))
                .findFirst()
                .orElse(producto);

            // Construcción del resumen informativo para el usuario
            StringBuilder resumen = new StringBuilder();
            resumen.append("Producto guardado exitosamente en la Base de Datos:\n\n");
            resumen.append("SKU Asignado: ").append(productoGuardadoReal.getSku()).append("\n");
            resumen.append("Nombre: ").append(productoGuardadoReal.getDescripcion()).append("\n\n");
            resumen.append("Lista de insumos enlazados:\n");
            
            for (Insumo insumo : productoGuardadoReal.getInsumos()) {
                resumen.append("  • ").append(insumo.getNombre())
                       .append(" - ").append(insumo.getCantidadPorUnidad()).append(" unidades\n");
            }
        
            mostrarAlerta(Alert.AlertType.INFORMATION, "Producto guardado", resumen.toString());

            // Limpiar todo después de guardar
            limpiarTodo();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Persistencia", 
                    "Hubo un problema de conexión con el servidor de Supabase. El producto no fue guardado.");
        }
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
    
    //Este metodo sirve para que cheque o valide el nombre tiene el formato para la primera mayuscula
    private void validarPrimeraMayuscula(String texto) throws IllegalArgumentException {
        if (texto == null || texto.isEmpty()) return;

        if (!Character.isUpperCase(texto.charAt(0))) {
            throw new IllegalArgumentException("Solo la primer letra tiene deber ser MAYÚSCULA.");
        }
    }
}