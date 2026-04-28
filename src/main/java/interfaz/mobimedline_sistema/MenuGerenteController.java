package interfaz.mobimedline_sistema;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuGerenteController implements Initializable{
    
    /* --- Elementos del sistema --- */
    @FXML
    private AnchorPane apContenedorCentro;

    @FXML
    private AnchorPane apMenu;

    @FXML
    private AnchorPane apPadre;

    @FXML
    private Label lblAgregarUsuario;

    @FXML
    private Label lblCerrarSesion;

    @FXML
    private Label lblConsulatrUsuario;

    @FXML
    private Label lblDashboard;

    @FXML
    private Label lblGestionOrden;

    @FXML
    private Label lblHistorialOrden;

    @FXML
    private Label lblMenu;

    @FXML
    private Label lblModificaProducto;

    @FXML
    private Label lblNuevaOdc;

    @FXML
    private Label lblOrdenCompraCategoria;

    @FXML
    private Label lblProductoCategoria;

    @FXML
    private Label lblRegistroProducto;

    @FXML
    private Label lblUsuarioCategoria;

    @FXML
    private VBox vbMenuLateral;

    @FXML
    private VBox vbSubOrden;

    @FXML
    private VBox vbSubProducto;

    @FXML
    private VBox vbSubUsuario;
    
    
    /* --- Eventos de las opciones del menu --- */
    @FXML
    private void irAgregarUsuario(MouseEvent event) {
        mostrarVista("AgregarUsuario"); 
    }
    
    @FXML
    private void irConsultaOrden(MouseEvent event) {
        mostrarVista("ConsultaOrdenCompra"); 
    }
    
    @FXML
    private void irAgregarProducto(MouseEvent event) {
        mostrarVista("AgregarProducto"); 
    }
    @FXML
    private void irCatalogo(MouseEvent event) {
        mostrarVista("ConsultarProducto"); 
    }
     @FXML
    private void irDashboard(MouseEvent event) {
        mostrarVista("MenuPrincipal"); 
    }
    
       @FXML
    private void irConsultarEmpleado(MouseEvent event) {
        mostrarVista("ConsultarEmpleado"); 
    }
    
       @FXML
    private void irNuevaODC(MouseEvent event) {
        mostrarVista("NuevaODC"); 
    }
    
//HOLIIIIIIIII, Aqui Trevorcito, les dejo lo de el volver al inicio de sesion al pulsar cerrar sesion :). Besitos
@FXML
private void cerrarSesion(MouseEvent event) {
    try {
        App.setRoot("IniciarSes");
    } catch (IOException e) {
        System.err.println("Error al cerrar sesión: " + e.getMessage());
        e.printStackTrace();
    }
}
   
/* --- Estado de Menu --- */
    // cierra el menu por defaul
    private boolean menuAbierto = false;
    
    //Animacion del menu
    @FXML
    private void toggleMenuAnimado() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(apMenu);

        if (!menuAbierto) {
            // --- ABRIR MENÚ ---
            apMenu.setVisible(true);
            apMenu.setManaged(true); // Empuja el formulario hacia la derecha

            slide.setToX(0);
            slide.play();
            menuAbierto = true;
        } else {
            // --- CERRAR MENÚ ---
            slide.setToX(-210);
            slide.play();

            // Esperamos a que termine la animación para quitar el espacio
            slide.setOnFinished(e -> {
                apMenu.setVisible(false);
                apMenu.setManaged(false); // El formulario vuelve a estirarse a la izquierda
            });
            menuAbierto = false;
            
        }
    }
    
    //Animacion de seccion
    private void toggleSeccion(VBox contenedorSubMenu) {
        boolean estaVisible = contenedorSubMenu.isVisible();

        // Cambia la visibilidad
        contenedorSubMenu.setVisible(!estaVisible);

        // IMPORTANTE: setManaged(false) hace que el elemento no ocupe espacio cuando está oculto
        contenedorSubMenu.setManaged(!estaVisible);
    }
    
    //Redireje al archivo fxml
    private void mostrarVista(String nombreArchivo) {
        try {
            String ruta = "/interfaz/mobimedline_sistema/" + nombreArchivo + ".fxml";
            URL url = getClass().getResource(ruta);

            if (url == null) {
                System.err.println("No se encontró el archivo en: " + ruta);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            apContenedorCentro.getChildren().setAll(vista);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración inicial: Invisible y no ocupa espacio
        apMenu.setVisible(false);
        apMenu.setManaged(false);

        // Lo posicionamos fuera de la pantalla (suponiendo ancho de 210)
        apMenu.setTranslateX(-210); 

        // Configuramos el evento del clic en la etiqueta "MENU"
        lblMenu.setOnMouseClicked(event -> {
            toggleMenuAnimado();
        });
        
        //Iniciar submenús contraídos si lo deseas
        vbSubOrden.setVisible(false);
        vbSubOrden.setManaged(false);
        
        vbSubProducto.setVisible(false);
        vbSubProducto.setManaged(false);
        
        vbSubUsuario.setVisible(false);
        vbSubUsuario.setManaged(false);

        //Asignar eventos a los títulos
        lblOrdenCompraCategoria.setOnMouseClicked(event -> toggleSeccion(vbSubOrden));
        lblProductoCategoria.setOnMouseClicked(event -> toggleSeccion(vbSubProducto));
        lblUsuarioCategoria.setOnMouseClicked(event -> toggleSeccion(vbSubUsuario)); 
        
        //Trevor estuvo aquí
        //Se esta usando para el evento de cerrar sesion
        lblCerrarSesion.setOnMouseClicked(this::cerrarSesion);
    }
}
