
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class Productov {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty descripcion;
    private final SimpleIntegerProperty cantidad; // piezas pedidas del producto
    
    //Clase de producot para la interfas de agregar_producto
    private String codigo;
    private String nombre;
    private List<Insumov> insumos;

    public Productov(int id, String descripcion, int cantidad, List<Insumov> insumos) {
        this.id = new SimpleIntegerProperty(id);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.insumos = insumos;
    }
    
    // Constructor adicional (sin modificar los que ya tienes)
    public Productov(String codigo, String nombre) {
        this.id = new SimpleIntegerProperty(0);
        this.descripcion = new SimpleStringProperty("");
        this.cantidad = new SimpleIntegerProperty(0);
        this.codigo = codigo;
        this.nombre = nombre;
        this.insumos = new ArrayList<>();  // Necesitas importar ArrayList
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
    
    //Interfas de agregar producto las variables de codigo y nombre
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void agregarInsumo(Insumov insumo) {
        this.insumos.add(insumo);
    }

    public void eliminarInsumo(int index) {
        if (index >= 0 && index < insumos.size()) {
            this.insumos.remove(index);
        }
    }

    public int getTotalInsumos() {
        return insumos.size();
    }
    
    
    //Clase de agregar producto
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto{codigo='").append(codigo).append("', nombre='").append(nombre).append("', insumos=[");
        for (int i = 0; i < insumos.size(); i++) {
            sb.append(insumos.get(i).toString());
            if (i < insumos.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}