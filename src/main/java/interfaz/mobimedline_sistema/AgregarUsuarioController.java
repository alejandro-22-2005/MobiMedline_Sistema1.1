/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package interfaz.mobimedline_sistema;

//import interfaz.agregarusuario.ReporteUsuarioController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Mike
 */
public class AgregarUsuarioController implements Initializable {
    
    /* --- Elementos del sistema --- */
    @FXML
    private AnchorPane apContenedorCentro;

    @FXML
    private Button btnAceptar;

    @FXML
    private Label lblAMaterno;

    @FXML
    private Label lblAPaterno;

    @FXML
    private Label lblAgregarUsuarioTitulo;

    @FXML
    private Label lblCContraseña;

    @FXML
    private Label lblContraseña;

    @FXML
    private Label lblMostrarUsuario;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblUsuario;

    @FXML
    private TextField tfAMaterno;

    @FXML
    private TextField tfAPaterno;

    @FXML
    private PasswordField tfCContrasena;

    @FXML
    private PasswordField tfContrasena;

    @FXML
    private TextField tfNombre;

    /* --- Eventos de Boton --- */
    @FXML
    private void AccionAceptar() {
        String pass1 = tfContrasena.getText();
        String pass2 = tfCContrasena.getText();

        // Validar que los campos no estén vacíos
        if (tfNombre.getText().isEmpty() || tfAPaterno.getText().isEmpty() || tfAMaterno.getText().isEmpty() || 
            tfContrasena.getText().isEmpty() || tfCContrasena.getText().isEmpty()){
            // Lanza alerta si es el caso.
            mostrarAlerta("Campos Incompletos", "Por favor, llena todos los campos obligatorios.", Alert.AlertType.WARNING);
            return;
            
        }

        // Comprobar que las contraseñas coinciden
        if (!tfContrasena.getText().equals(tfCContrasena.getText())) {
            
            mostrarAlerta("Error de Contraseña", "Las contraseñas no coinciden. Inténtalo de nuevo.", Alert.AlertType.ERROR);
            tfContrasena.clear();
            tfCContrasena.clear();
            return;
            
        }
        
        // --- NUEVO: SI TODO ESTÁ BIEN, ABRIMOS LA VENTANA SECUNDARIA ---
        try {
            // Cargamos el FXML de la ventana de resumen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RecapitulacionUsuario.fxml"));
            Parent root = loader.load();

            // Obtenemos el controlador de la ventana secundaria para pasarle los datos
            // Asegúrate de que tu clase se llame ReporteUsuarioController
            RecapitulacionUsuarioController secundaryCtrl = loader.getController();

            // Enviamos los datos (Nombre, Apellidos combinados, Usuario generado y Contraseña)
            secundaryCtrl.recibirDatos(
                tfNombre.getText(),
                tfAPaterno.getText(),// + " " + 
                tfAMaterno.getText(),
                lblMostrarUsuario.getText(),
                tfContrasena.getText() 
            );

            // Configuramos el Stage (La ventana)
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // Para tus bordes redondeados
            stage.setResizable(false);               // Bloqueamos maximizar

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);        // Fondo transparente para el radio de 10

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.showAndWait();
            
            limpiarCampos();

        } catch (IOException e) {
            mostrarAlerta("Error de Sistema", "No se pudo cargar la ventana de resumen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
    }
    
    
    /* --- Funciones generales --- */
    // Funcion para limpia los campos
    private void limpiarCampos() {
        
        tfNombre.clear();
        tfAPaterno.clear();
        tfAMaterno.clear();
        tfContrasena.clear();
        tfCContrasena.clear();

    }
    
    // Funcion para la ventana emergente
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        
    }
    
    // Funcion para resumir los datos ingresados
    private String obtenerUsuario() {
        String nom = tfNombre.getText().trim().toUpperCase();
        String pat = tfAPaterno.getText().trim().toUpperCase();
        String mat = tfAMaterno.getText().trim().toUpperCase();

        if (nom.isEmpty() || pat.length() < 2) return "";

        // Inicial del primer apellido
        char p1 = pat.charAt(0);

        // Primera vocal interna del primer apellido (buscamos desde la posición 1)
        char p2 = 'X'; 
        String interna = pat.substring(1);
        for (char c : interna.toCharArray()) {
            if ("AEIOU".indexOf(c) != -1) {
                p2 = c;
                break;
            }
        }

        // Inicial del segundo apellido (si no hay, usamos X)
        char p3 = mat.isEmpty() ? 'X' : mat.charAt(0);

        // 4. Inicial del nombre
        char p4 = nom.charAt(0);

        return "" + p1 + p2 + p3 + p4;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Creamos una función que actualice la etiqueta
        Runnable actualizarEtiqueta = () -> {
            String base = obtenerUsuario();
            if (!base.isEmpty()) {
                // Aquí simulamos que el contador de la DB es 01 por ahora
                lblMostrarUsuario.setText(base + "01"); 
            } else {
                lblMostrarUsuario.setText("Esperando datos...");
            }
        };

        // Agregamos los "Listeners" a cada campo
        tfNombre.textProperty().addListener((obs, viejo, nuevo) -> actualizarEtiqueta.run());
        tfAPaterno.textProperty().addListener((obs, viejo, nuevo) -> actualizarEtiqueta.run());
        tfAMaterno.textProperty().addListener((obs, viejo, nuevo) -> actualizarEtiqueta.run());
    }    
}
