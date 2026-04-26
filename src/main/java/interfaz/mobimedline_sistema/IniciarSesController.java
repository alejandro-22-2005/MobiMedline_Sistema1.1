package interfaz.mobimedline_sistema;

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
        String user = tfnombreUs.getText();
        String pass = passfiCon.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("No llenaste un campo", "Por favor, verifica que ingresaste bien tus datos.");
        } else {
            System.out.println("Intento de login: " + user);
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