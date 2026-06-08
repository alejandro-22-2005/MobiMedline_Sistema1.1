package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

public class ConsultarEmpleadoController implements Initializable {
    
        
      // BOTONES PARA MANIPULAR LA TABLA
    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnRegresar;
    
    //TABLA Y COLUMNAS

    @FXML
    private TableColumn<Usuarios, String> colNombre;

    @FXML
    private TableColumn<Usuarios, String> colPassword;

    @FXML
    private TableColumn<Usuarios, String> colUsuario;

    @FXML
    private TableView<Usuarios> tblEmpleados;
    
    //LISTA DE EMPLEADOS QUE SE MUESTRAN EN LA TABLE: Usamos ObservableList para que se actualice con las acciones que hacemos
    
    private ObservableList<Usuarios> listaEmpleados = FXCollections.observableArrayList();
    
    // Almacena qué usuario tiene permiso de mostrar contraseña
    private Usuarios usuarioRevelado = null; 

   //BOTONES DE ACCIÓN
    @FXML
    void Editar(ActionEvent event) {
        
        //Objeto del tipo empleados, el cual será seleccionado para efectuar una acción sobre él
        Usuarios seleccionado = tblEmpleados.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            System.out.println("Selecciona un registro primero");
            return;
        }
        //Variable de control que nos da true o false para validar que la contraseña sea la correcta y poder realizar las acciones
        boolean accesoPermitido = validarGerente();

        if (accesoPermitido) {
            // si la contraseña es correcta, entonces la tabla se vuelve editable
            tblEmpleados.setEditable(true);
            
            // valor de la fila seleccionada del empleado
            int fila = tblEmpleados.getSelectionModel().getSelectedIndex();
            
            //Para que ejecute las siguientes instrucciones después de que haya procesado la instrucción anterios
            Platform.runLater(() -> {
                tblEmpleados.requestFocus(); //resalta la selección
                tblEmpleados.getSelectionModel().select(fila); //selecciona la fila
                tblEmpleados.getFocusModel().focus(fila, colNombre); //se enfoca en una columna y fila en especifico 
                tblEmpleados.edit(fila, colNombre); //habilita la edición en esa celda por default
            });

            System.out.println("Edición habilitada");
        } 
        else {
            Alert incorrecta = new Alert(Alert.AlertType.ERROR);
            incorrecta.setTitle("ERROR");
            incorrecta.setHeaderText(null);
            incorrecta.setContentText("Contraseña Incorrecta: Intentalo de nuevo");
            incorrecta.showAndWait();
            System.out.println("Contraseña incorrecta");
            System.out.println("Contraseña incorrecta");
        }
    }
    
    

    @FXML
    void Eliminar(ActionEvent event) {
        
        Usuarios seleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        
        if(seleccionado == null){
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Aviso");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecciona un registro para eliminar");
            alerta.showAndWait();
            return;
        }
        
        boolean accesoPermitido = validarGerente();
        
        if(accesoPermitido){
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminacion");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de eliminar este registro?");
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            
            if(resultado.isPresent() && resultado.get() == ButtonType.OK){
                listaEmpleados.remove(seleccionado);
                
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Eliminado");
                exito.setHeaderText(null);
                exito.setContentText("Registro eliminado correctamente");
                exito.showAndWait();
            }
        }
        else{
            Alert incorrecta = new Alert(Alert.AlertType.ERROR);
            incorrecta.setTitle("ERROR");
            incorrecta.setHeaderText(null);
            incorrecta.setContentText("Contraseña Incorrecta: Intentalo de nuevo");
            incorrecta.showAndWait();
            System.out.println("Contraseña incorrecta");
        }

    }

    @FXML
    void Guardar(ActionEvent event) {
        
        //Obtener la lista de usuarios que están actualmente en la tabla (ya editados)
        List<Usuarios> listaEditada = new ArrayList<>(tblEmpleados.getItems());
        
        //ESTE ACTUALIZARA LA LISTA UNA VEZ QUE SE HAGA LAS MODIFICACIONES PERTINENTESD
        AgendaUsuariosBase.setUsuariosBase(listaEditada);
        
        //Resetea permisos de visualización por seguridad
        usuarioRevelado = null;
        
        tblEmpleados.refresh();
        tblEmpleados.setEditable(false);
        
        Alert guardado = new Alert(Alert.AlertType.CONFIRMATION);
        guardado.setTitle("GUARDADO");
        guardado.setHeaderText(null);
        guardado.setContentText("Cambios guardados exitosamente");
        guardado.showAndWait();
        System.out.println("Cambios guardados");

    }

    @FXML
    void Regresar(ActionEvent event) {

    }
    
    @FXML
    void verPasswordSeleccionado(ActionEvent event) {
    //Obtener quién está seleccionado en la tabla
    Usuarios seleccionado = tblEmpleados.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText("Por favor, selecciona un empleado de la tabla.");
        alerta.showAndWait();
        return;
    }

    // Si el usuario ya está revelado y pulsas de nuevo, lo ocultamos
    if (usuarioRevelado == seleccionado) {
        usuarioRevelado = null;
        tblEmpleados.refresh();
        return;
    }

    //Pedir autorización al gerente
    if (validarGerente()) {
        usuarioRevelado = seleccionado; 
        tblEmpleados.refresh(); 
    } else {
        Alert incorrecta = new Alert(Alert.AlertType.ERROR);
        incorrecta.setTitle("ERROR");
        incorrecta.setHeaderText(null);
        incorrecta.setContentText("Contraseña del gerente incorrecta.");
        incorrecta.showAndWait();
    }
}
    
    //METODO PARA VALIDAR EL USUARIO DEL GERENTE PARA MODIFICAR LAS CREDENCIALES DE LOS EMPLEADOS
    
    private boolean validarGerente(){
        //creamos una ventana emergente para solicitar la contraseña del gerente
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Autorización");
        dialog.setHeaderText("Ingrese la contraseña del gerente");
        
        //usamos un campo para la contraseña, el cual no muestra los caracteres, sino puntos, para mayor confidencialidad
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        
        //Creamos la caja vertical para acomodar la etiqueta y el campo para la contrasela
        VBox contenido = new VBox(10);
        contenido.getChildren().addAll(new Label("Contraseña:"), passwordField);
        
        //Asignamos a la caja vertical el contenido que escribimos y posteriormente agregamos los botones de ok y cancelar
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //capturamos el valor del botón que fue seleccionado
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
        
        Optional<String> resultado = dialog.showAndWait();
        
        // === VALIDACIÓN DINÁMICA CONTRA LOS GERENTES REALES ===
        if (resultado.isPresent()) {
            String passwordIngresada = resultado.get();

            // Recorremos la lista base para buscar al usuario Gerente
            for (Usuarios u : AgendaUsuariosBase.getUsuariosBase()) {
                // Nota: Como en tu clase Usuarios manejas 'Permisos' (boolean) o el DAO maneja 'gerente',
                // validamos que sea administrador/gerente y que su contraseña coincida.
                // Si en tu clase Usuarios mantienes el atributo 'Permisos' como indicador de Gerente:
                if (u.getPermisos() && u.getContraseña().equals(passwordIngresada)) {
                    return true; // Contraseña correcta de un gerente real
                }
            }
        }

        return false;
    }
    
    //INICIALIZADOR
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {    //Tabla inicializada sin poder modificarse
        tblEmpleados.setEditable(false);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
        
        //Conectar columnas con datos
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("contraseña"));
        
        //Columnas editables
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colUsuario.setCellFactory(TextFieldTableCell.forTableColumn());
        //colPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        
        colPassword.setCellFactory(column -> new TextFieldTableCell<Usuarios, String>(new DefaultStringConverter()) {
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Si la celda está siendo editada, no forzamos los asteriscos
            if (isEditing()) {
                setText(item); 
            } else {
                // Obtenemos el usuario de la fila actual
                Usuarios usuarioDeFila = getTableRow() != null ? getTableRow().getItem() : null;
                
                // Si el gerente autorizó este usuario específico, mostramos la clave[cite: 3, 4]
                if (usuarioDeFila != null && usuarioDeFila == usuarioRevelado) {
                    setText(item); 
                } else {
                    setText("********"); // De lo contrario, ocultamos
                }
            }
        }
    }
    });
        
        //Guardar la info ingresada de la modificación
        colNombre.setOnEditCommit(event -> {
            Usuarios emp = event.getRowValue();
            emp.setNombre(event.getNewValue());
            tblEmpleados.refresh();
        });
        
        colUsuario.setOnEditCommit(event -> {
            Usuarios emp = event.getRowValue();
            emp.setUsuario(event.getNewValue());
            tblEmpleados.refresh();
        });
        
        colPassword.setOnEditCommit(event -> {
            Usuarios emp = event.getRowValue();
            emp.setContraseña(event.getNewValue());
            tblEmpleados.refresh();
        });

        //Le asignamos la lista a la tabla
        tblEmpleados.setItems(listaEmpleados);
          
        // Cargar la lista global (Prueba + Manuales)
        listaEmpleados.clear(); // Limpiamos para evitar duplicados
        listaEmpleados.addAll(AgendaUsuariosBase.getUsuariosBase());//Autor:Ale
        
        tblEmpleados.setItems(listaEmpleados);
        
        // Datos de prueba
        //listaEmpleados.add(new Empleadosj("Juan", "juan123", "1234"));
        //listaEmpleados.add(new Empleadosj("Ana", "ana456", "abcd"));
    }
    
}