package interfaz.mobimedline_sistema;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ConsultarProductoController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;

    @FXML private ListView<Detalle> listaProductos;

    @FXML private TableView<Detalle> tablaDetalle;
    @FXML private TableColumn<Detalle, String> colNombre;
    @FXML private TableColumn<Detalle, String> colCantidad;
    @FXML private TableColumn<Detalle, String> colUnidad;

    private ObservableList<Detalle> listaCompleta = FXCollections.observableArrayList(

        new Detalle("Mesa Pasteur con Cajón",
                "• Lámina\n• Tubo\n• Casquillo\n• Rueda",
                "1\n1\n4\n4",
                "pieza\npieza\npiezas\npiezas",
                "/imagenes/mesa.jpg"),

        new Detalle("Banco giratorio",
                "• Tapón circular\n• Tapón barril",
                "4\n4",
                "piezas\npiezas",
                "/imagenes/banco.png"),

        new Detalle("Vitrina Premium",
                "• Lámina\n• Vidrio\n• Cerradura",
                "2\n2\n1",
                "piezas\npiezas\npieza",
                "/imagenes/vitrina.jpg")
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCantidad()));

        colUnidad.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUnidad()));

        aplicarWrap(colNombre);
        aplicarWrap(colCantidad);
        aplicarWrap(colUnidad);

     
        listaProductos.setCellFactory(param -> new ListCell<>() {

            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox hbox = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Detalle item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item.getMaterial());

                    try {
                        Image img = new Image(
                                getClass().getResource(item.getImagen()).toExternalForm()
                        );
                        imageView.setImage(img);
                    } catch (Exception e) {
                        imageView.setImage(null);
                    }

                    setGraphic(hbox);
                }
            }
        });

        listaProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tablaDetalle.setItems(FXCollections.observableArrayList(sel));
                txtSku.setText(sel.getMaterial());
                txtNombre.setText(sel.getNombre());
            }
        });

        tablaDetalle.setItems(FXCollections.observableArrayList());
        listaProductos.setItems(FXCollections.observableArrayList());
    }

    private void aplicarWrap(TableColumn<Detalle, String> columna) {
        columna.setCellFactory(tc -> {
            TableCell<Detalle, String> cell = new TableCell<>() {

                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(tc.widthProperty());
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
    }

    @FXML
    private void buscar() {
        buscarTiempoReal(txtBuscar.getText());
    }

    @FXML
    private void limpiar() {
        txtBuscar.clear();
        txtSku.clear();
        txtNombre.clear();
        tablaDetalle.getItems().clear();
        listaProductos.getItems().clear();
    }

    private void buscarTiempoReal(String texto) {

        ObservableList<Detalle> resultados = FXCollections.observableArrayList();
        ObservableList<Detalle> productos = FXCollections.observableArrayList();

        if (texto == null || texto.trim().isEmpty()) return;

        texto = texto.toLowerCase();

        for (Detalle d : listaCompleta) {

            if (d.getMaterial().toLowerCase().contains(texto)
                    || d.getNombre().toLowerCase().contains(texto)) {

                resultados.add(d);

                if (!productos.contains(d)) {
                    productos.add(d);
                }
            }
        }

        tablaDetalle.setItems(resultados);
        listaProductos.setItems(productos);
    }

    @FXML
    private void modificar() {

        Detalle seleccionado = listaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto.");
            return;
        }

        seleccionado.setMaterial(txtSku.getText());
        seleccionado.setNombre(txtNombre.getText());

        listaProductos.refresh();
        tablaDetalle.refresh();

        mostrarAlerta("Modificado correctamente.");
    }

    @FXML
    private void guardar() {

        if (txtSku.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            mostrarAlerta("Llena los campos.");
            return;
        }

        Detalle nuevo = new Detalle(
                txtSku.getText(),
                txtNombre.getText(),
                "1",
                "pieza",
                "/imagenes/mesa.jpg"
        );

        listaCompleta.add(nuevo);

        mostrarAlerta("Guardado correctamente.");

        limpiar();
    }

    @FXML
    private void eliminar() {

        Detalle seleccionado = listaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto.");
            return;
        }

        listaCompleta.remove(seleccionado);
        listaProductos.getItems().remove(seleccionado);
        tablaDetalle.getItems().remove(seleccionado);

        mostrarAlerta("Eliminado correctamente.");
    }
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public static class Detalle {

        private String material;
        private String nombre;
        private String cantidad;
        private String unidad;
        private String imagen;

        public Detalle(String material, String nombre, String cantidad, String unidad, String imagen) {
            this.material = material;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.unidad = unidad;
            this.imagen = imagen;
        }

        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getCantidad() { return cantidad; }
        public String getUnidad() { return unidad; }
        public String getImagen() { return imagen; }
    }
}