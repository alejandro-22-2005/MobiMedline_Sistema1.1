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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML "Agregar Usuario" Controller
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
        try {
            // Validar campos vacíos, contraseñas y que no contengan números
            validarFormulario();

            // Validar si el usuario está duplicado en la lista estática
            String usuarioGenerado = lblMostrarUsuario.getText();
            validarUsuarioUnico(usuarioGenerado);

            // 3. Si todo está perfecto, procedemos a abrir la ventana secundaria
            procesarAperturaVentana(usuarioGenerado);

        } catch (CamposIncompletosException e) {
            mostrarAlerta("Campos Incompletos", e.getMessage(), Alert.AlertType.WARNING);
            
        } catch (ContrasenasNoCoincidenException e) {
            mostrarAlerta("Error de Contraseña", e.getMessage(), Alert.AlertType.ERROR);
            tfContrasena.clear();
            tfCContrasena.clear();
            
        } catch (NombreConNumerosException e) {
            // Atrapamos si escribieron números en el nombre/apellidos
            mostrarAlerta("Formato Inválido", e.getMessage(), Alert.AlertType.WARNING);
            
        } catch (UsuarioDuplicadoException e) {
            // Si está repetido, calculamos el siguiente número
            String usuarioFijo = obtenerUsuario(); // Obtiene las 4 letras (ej: RAAA)
            int siguienteConsecutivo = calcularSiguienteContador(usuarioFijo);
            
            // Formateamos con dos dígitos ("02", "03")
            String nuevoUsuarioCorregido = usuarioFijo + String.format("%02d", siguienteConsecutivo);
            
            // Le avisamos al usuario mediante una alerta informativa
            mostrarAlerta("Usuario Ajustado", 
                "El identificador '" + lblMostrarUsuario.getText() + "' ya existe.\n" +
                "Se ha asignado automáticamente el consecutivo disponible: " + nuevoUsuarioCorregido, 
                Alert.AlertType.INFORMATION);
            
            // Actualizamos la etiqueta visual y procedemos con el registro corregido
            lblMostrarUsuario.setText(nuevoUsuarioCorregido);
            try {
                procesarAperturaVentana(nuevoUsuarioCorregido);
            } catch (IOException ioException) {
                mostrarAlerta("Error de Sistema", "No se pudo cargar la ventana de resumen al reintentar: " + ioException.getMessage(), Alert.AlertType.ERROR);
            }
            
        } catch (IOException e) {
            mostrarAlerta("Error de Sistema", "No se pudo cargar la ventana de resumen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
  
    /**
     * Lógica aislada para abrir la ventana de recapitulación
     * @param usuarioFinal Usuario a vincular a los datos proporcionados en el formulario
     * @author Mike
     */
    private void procesarAperturaVentana(String usuarioFinal) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RecapitulacionUsuario.fxml"));
        Parent root = loader.load();

        RecapitulacionUsuarioController secundaryCtrl = loader.getController();

        secundaryCtrl.recibirDatos(
            tfNombre.getText(),
            tfAPaterno.getText(),
            tfAMaterno.getText(),
            usuarioFinal, // Enviamos el usuario final (ya sea el original o el auto-incrementado)
            tfContrasena.getText() 
        );

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);               

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);        

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.showAndWait();
        
        limpiarCampos();
    }
    
    
    /* --- Funciones de validacion --- */
    
    /**
     * Validaciones del formulario incluyendo formato de texto
     * @Mike
     */
    private void validarFormulario() throws CamposIncompletosException, ContrasenasNoCoincidenException, NombreConNumerosException {
        // Validar que los campos no estén vacíos
        if (tfNombre.getText().isEmpty() || tfAPaterno.getText().isEmpty() || tfAMaterno.getText().isEmpty() || 
            tfContrasena.getText().isEmpty() || tfCContrasena.getText().isEmpty()) {
            
            throw new CamposIncompletosException("Por favor, llena todos los campos obligatorios.");
        }

        // NUEVO: Validar que no existan dígitos numéricos en los nombres/apellidos
        if (tfNombre.getText().matches(".*\\d.*") || tfAPaterno.getText().matches(".*\\d.*") || tfAMaterno.getText().matches(".*\\d.*")) {
            throw new NombreConNumerosException("Los campos de Nombre y Apellidos no pueden contener números.");
        }

        // Comprobar que las contraseñas coinciden
        if (!tfContrasena.getText().equals(tfCContrasena.getText())) {
            throw new ContrasenasNoCoincidenException("Las contraseñas no coinciden. Inténtalo de nuevo.");
        }
    }
    
    
    /**
     * Verifica si el ID ya existe en la Base de Datos
     * @param usuarioAChecar Usuario a validar
     * @author Mike
     */
    private void validarUsuarioUnico(String usuarioAChecar) throws UsuarioDuplicadoException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
    
        // Si el DAO nos confirma que el usuario ya existe en Supabase, disparamos la excepción
        if (usuarioDao.existeUsuario(usuarioAChecar)) {
            throw new UsuarioDuplicadoException("El usuario '" + usuarioAChecar + "' ya se encuentra registrado.");
        }
    }

    
    /**
     * Busca en la lista base cuántos usuarios comparten las mismas 4 letras de usuario
     * y determina el siguiente número consecutivo disponible (+1)
     * @param inicialesCuatroLetras Usuario a buscar en la agenda usuario
     * @author Mike
     */
    private int calcularSiguienteContador(String inicialesCuatroLetras) {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        // Devolvemos el cálculo exacto hecho desde la base de datos
        return usuarioDao.obtenerSiguienteConsecutivoBD(inicialesCuatroLetras);
    }
    
    
    /**
     * Limpia los campos del formulario
     * @author Mike
     */
    private void limpiarCampos() {
        tfNombre.clear();
        tfAPaterno.clear();
        tfAMaterno.clear();
        tfContrasena.clear();
        tfCContrasena.clear();
    }
    
    
    /**
     * Funcion para la ventana emergente
     * @param titulo Se titulo de la ventana
     * @param mensaje Mensaje en el cuerpo de la ventana
     * @param tipo tipo de mensaje(icono)
     * @author Mike
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    
    /**
     * Funcion para generar al usario(logica CURP)
     * @return Usuario de 4 letras 2 numeros
     */
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

        // Inicial del nombre
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
