//Trevor was here
//Hola amiguitos del bosque. Soy Trevor y les explicare que chingaos hahce mi codigo :)
package interfaz.mobimedline_sistema;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class IniciarSesController {

    @FXML
    private TextField tfnombreUs;

    @FXML
    private PasswordField passfiCon;

    @FXML
    private void handleLogin() {
        String user = tfnombreUs.getText().trim();
        String pass = passfiCon.getText().trim();
        
        // En esta parte de aquí, se hace una validacion para campos vacios
        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("No llenaste un campo", "Por favor, verifica que ingresaste bien tus datos.");
        } else {
            System.out.println("Intento de login: " + user);
        }
    
        //SE MANDA A LLAMAR LA "BASE" de usuarios
    List<Usuarios> listaUsuarios = AgendaUsuariosBase.getUsuariosBase();
    boolean loginExitoso = false;
    
    for(Usuarios u : listaUsuarios) {
    
    if (u.getUsuario().equals(user)&& u.getContraseña().equals(pass)){
    loginExitoso = true;
        }
 
    
    //Coneccion a Permisos
    
    try {
        if (u.getPermisos() == true ){
            System.out.println("Iniciando sesion como Gerente, Bienvenido :) :" + u.getNombre());
    }else {
        
            System.out.println("Iniciando sesion como Empleado, bienvenido: " + u.getNombre());
            App.setRoot("MenuVentas");

        }
        
        }catch (IOException e){
        e.printStackTrace();
        mostrarAlerta("ERROR DE SISTEMA", "No se pudo cargar la siguiente ventana :(");
        }
        break;

    }
    
    if (!loginExitoso) {
    mostrarAlerta ("Credencial incorrecta", "La contraseña o el usuario son incorrectos, verifica tu informacion");
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