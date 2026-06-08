
package interfaz.mobimedline_sistema;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;


/**
 * FXML "Inicio de sesion" Controller
 * @author Trevor
 */
public class IniciarSesController {
    public static Usuarios usuarioActual; //saber quién inició sesión

    @FXML
    private TextField tfnombreUs;

    @FXML
    private PasswordField passfiCon;

    @FXML
    private void handleLogin(){
        String user = tfnombreUs.getText().trim();
        String pass = passfiCon.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("No llenaste un campo", "Por favor, verifica tus datos.");
            return; 
        }

        // === CAMBIO AQUÍ: Consulta directa a la Base de Datos con el DAO ===
        UsuarioDAO usuarioDao = new UsuarioDAO();
        Usuarios u = usuarioDao.buscarPorUsuario(user);
        
        boolean loginExitoso = false;

        // Validamos si el usuario existe y si la contraseña coincide exactamente
        if (u != null && u.getContraseña().equals(pass)) {
            loginExitoso = true;
            usuarioActual = u; // Guardamos el usuario que inició sesión globalmente

            try {
                // Validación del tipo de usuario para redirección usando tu método original
                if (u.getPermisos()) { // Es true ('gerente' en la BD)
                    System.out.println("Iniciando sesión como Gerente: " + u.getNombre());
                    App.setRoot("MenuGerente"); 
                } else { // Es false ('colaborador' en la BD)
                    System.out.println("Iniciando sesión como Empleado: " + u.getNombre());
                    App.setRoot("MenuVentas");
                }
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("ERROR DE SISTEMA", "No se pudo cargar la ventana del menú.");
            }
        }
    
        if (!loginExitoso){
            mostrarAlerta("Credencial incorrecta", "Usuario o contraseña no válidos.");
            passfiCon.clear();
        }
    }
    
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}