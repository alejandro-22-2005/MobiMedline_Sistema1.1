
package interfaz.mobimedline_sistema;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class Productov {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty descripcion;
    private final SimpleIntegerProperty cantidad; // piezas pedidas del producto

    private List<Insumov> insumos;

    public Productov(int id, String descripcion, int cantidad, List<Insumov> insumos) {
        this.id = new SimpleIntegerProperty(id);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.insumos = insumos;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public SimpleStringProperty descripcionProperty() {
        return descripcion;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }

    public SimpleIntegerProperty cantidadProperty() {
        return cantidad;
    }

    public List<Insumov> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<Insumov> insumos) {
        this.insumos = insumos;
    }
    public int calcularTotalInsumo(Insumov insumo) {
        return this.getCantidad() * insumo.getCantidadPorUnidad();
    }
}