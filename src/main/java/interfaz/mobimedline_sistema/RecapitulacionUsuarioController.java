package interfaz.mobimedline_sistema;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RecapitulacionUsuarioController {
    
    /* --- Elementos del sistema --- */
    @FXML
    private AnchorPane apVentana;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Label lblMostrarApellidos;

    @FXML
    private Label lblMostrarNombre;

    @FXML
    private Label lblMostrarUsuario;
    
    @FXML
    private Label lblNombre;

    // Estas variables guardan lo que escribiste manualmente en la ventana anterior Autor:Ale
    private String nomG, patG, matG, usuG, passG;
    
    /* --- Funciones generales --- */
    public void recibirDatos(String nombre, String pat, String mat, String usuario, String pass) {
        
        // 1. Guardamos en la "memoria" del controlador Autor:Ale
        this.nomG = nombre;
        this.patG = pat;
        this.matG = mat;
        this.usuG = usuario;
        this.passG = pass;
        
        // Aquí asignas los textos a tus Labels de la ventana secundaria
        // Por ejemplo:
        lblMostrarNombre.setText(nombre);
        lblMostrarApellidos.setText(pat + " " + mat);
        lblMostrarUsuario.setText(usuario);
    }
    
    public class ResumenController {
        // Variables temporales para guardar los datos recibidos
        private String nombreG, paternoG, maternoG, usuarioG, passG;

        // Método que recibe la "maleta" de datos
        public void prepararResumen(String nom, String pat, String mat, String usu, String pass) {
            // Guardamos en variables locales para el proceso final
            this.nombreG = nom;
            this.paternoG = pat;
            this.maternoG = mat;
            this.usuarioG = usu;
            this.passG = pass;

            // Mostramos en la interfaz para que el usuario verifique
            lblMostrarNombre.setText(nom);
            lblMostrarApellidos.setText(pat + " " + mat);
            lblMostrarUsuario.setText(usu);
        }

    }
    

    
    /* --- Eventos de Boton --- */
    @FXML
    private void AccionConfirmarGuardado(ActionEvent event) {

        // 1. Creamos el objeto Usuarios con los datos capturados
        Usuarios nuevo = new Usuarios(usuG, nomG, patG, matG, passG);

        // 2. Instanciamos tu DAO para interactuar con Supabase
        UsuarioDAO usuarioDao = new UsuarioDAO();

        // --- AQUÍ OCURRE EL GUARDADO REAL ---
        System.out.println("Insertando en la DB al usuario: " + lblMostrarUsuario.getText());

        // Llamamos al método registrar del DAO y evaluamos si la inserción fue exitosa
        boolean guardadoExitoso = usuarioDao.registrar(nuevo);

        if (guardadoExitoso) {
            // Si se guardó en Supabase, actualizamos la lista local de respaldo (opcional)
            AgendaUsuariosBase.getUsuariosBase().add(nuevo); 

            // Mostramos el mensaje de éxito real
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Registro Exitoso");
            exito.setHeaderText(null);
            exito.setContentText("El usuario " + lblMostrarUsuario.getText() + " ha sido guardado exitosamente en la base de datos.");
            exito.showAndWait();

            // Cerramos la ventana únicamente si se guardó con éxito
            Stage stage = (Stage) lblNombre.getScene().getWindow();
            stage.close();

        } else {
            // Si hubo un problema de conexión o error de SQL, avisamos al usuario sin cerrar la ventana
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error de Guardado");
            error.setHeaderText("No se pudo registrar el usuario");
            error.setContentText("Hubo un problema al conectar con Supabase. Verifica tu conexión de red o si el nombre de usuario ya existe.");
            error.showAndWait();
        }
    }
    
    
    @FXML
    private void AccionCancelarGuardado(ActionEvent event) {
        // Cerramos la ventana después de guardar
        Stage stage = (Stage) lblNombre.getScene().getWindow();
        stage.close();
    }
    
    
}