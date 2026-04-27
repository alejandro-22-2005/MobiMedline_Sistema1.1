
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class Producto {
    // --- Atributos --- 
    private static int contadorSiguiente = 1;
    
    private String sku;
    private String descripcion;
    private int cantidad;
    private List<Insumo> insumos;
    
    // --- Constructor ---
    public Producto(String descripcion, int cantidad) {
        this.sku = String.format("%05d", contadorSiguiente++);
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.insumos = new ArrayList<>();
    }

    public Producto(String sku, String descripcion, int cantidad, List<Insumo> insumos) {
        this.sku = sku;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.insumos = insumos;
    }
    
    
    
    public Producto(String nombre) {
        this.sku = String.format("%05d", contadorSiguiente++);
        this.descripcion = nombre;
        this.cantidad = 0;
        this.insumos = new ArrayList<>();
    }
    
    
    /**
     * Agrega una instancia de Insumov a nuestra lista List<Insumo> insumos
     * @param insumo Insumo a agregar en nuestra lista de insumos
     * @autor Ale, Javi, Vero
     */
    public void agregarInsumo(Insumo insumo){
        this.insumos.add(insumo);
    }
    
    /**
     * Elimina un una instancia de tipo Insumov de nuestra List<Insumo> insumos;
     * @param index Indice de Insumo a remover
     * @autor Ale, Javi, Vero
     */
    public void eliminarInsumo(int index) {
        if (index >= 0 && index < insumos.size()) {
            this.insumos.remove(index);
        }
    }
    
    // --- Getters
    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    
    public String getSku() {
        return this.sku;
    }
    
    public List<Insumo> getInsumos() {
        return this.insumos;
    }
    
    // --- Setters   
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public void setDescripcion(String nombre){
        this.descripcion = nombre;
    }
    
    
    
    //Clase de agregar producto
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto{sku='").append(sku).append("', nombre='").append(descripcion).append("', insumos=[");
        for (int i = 0; i < insumos.size(); i++) {
            sb.append(insumos.get(i).toString());
            if (i < insumos.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
    
}
