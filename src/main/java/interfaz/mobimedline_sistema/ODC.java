/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mike
 */
public class ODC {
    // --- Atributos --- 
    private static int contadorSiguiente = 1;
    
    private final String idODC;
    private final List<Productov> productos;
    private boolean tipoEspecial;
    
    // --- Constructor ---
    public ODC() {
        // Genera el ID con formato #####-ODC (ej. 00001-ODC)
        this.idODC = String.format("%05d-ODC", contadorSiguiente++);
        this.productos = new ArrayList<>();
        this.tipoEspecial = false;
    }
    
    /**
     * Verifica si el producto existe en nuetra List<Productov> productos
     * 1° Caso: Existe por lo que actualiza ese producto con la nueva cantidad
     * 2° Caso: No existe por lo que creamos la instancia de Porductov con su cantidad
     *          y usamos agregarProductoNuevo() para agregar en List<Productov> productos 
     * @param producto Producto a agregar a la ODC
     * @param cantidad Cantidad de ese producto
     * @autor Mike
     */
    public void actualizarOAgregarProducto(Productov producto, int cantidad) {
        boolean encontrado = false;
        
        // 1°caso. Existe el producto en nuestra ODC y vamos solo a actualizar
        for (Productov p : productos) {
            if (p.getCodigo().equals(producto.getCodigo())) {
                p.setCantidad(cantidad);
                encontrado = true;
                break;
            }
        }
        
        // 2°caso. No existe el producto en nuestra ODC y vamos a agregar
        if (!encontrado) {
            producto.setCantidad(cantidad);
            agregarProductoNuevo(producto);
        }
        recalcularTipoEspecial();
    }
    
    /**
     * Agrega una Intancia de Productov que no existe en List<Productov> productos 
     * y toma en cuenta la REGLA DE NEGOCIO: ODC Normal/Especial
     * @param producto Producto a agregar a la ODC
     * @autor Mike
     */
    public void agregarProductoNuevo(Productov producto) {
        // --- REGLA DE NEGOCIO: Si un producto tiene cantidad >= 10, es Especial
        this.productos.add(producto);
        if (producto.getCantidad() >= 10) {
            setTipoEspecial(true);
        }
    }
    
    /**
     * Elimina por nombre una instancia de Productov enlistada en nuestra 
     * List<Productov> productos
     * @param producto Elimina este producto de nuestra Lista de productos (nombre)
     * @autor Mike
     */
    public void eliminarProducto(Productov producto) {
        this.productos.remove(producto);
        recalcularTipoEspecial();
    }
    
    /**
     * Elimina por indice una instancia de Productov enlistada en nuestra 
     * List<Productov> productos, pensado para las tablas
     * @param indice Eliminamos este producto de nuestra Lista de productos (indice)
     * @autor Mike
     */
    public void eliminarProducto(int indice) {
        if (indice >= 0 && indice < this.productos.size()) {
            this.productos.remove(indice);
            recalcularTipoEspecial();
        }
    }
    
    /**
     * Recorre nuestro List<Productov> productos para validar 
     * la  REGLA DE NEGOCIO: ODC Normal/Especial
     * @autor Mike
     */
    private void recalcularTipoEspecial() {
        for (Productov p : productos) {
            if (p.getCantidad() >= 10) {
                setTipoEspecial(true);
                break;
            }
        }
    }
    /**
     * Vacia la List<Productov> productos
     * @autor Mike
     */
    public void vaciarOrden() {
        this.productos.clear();
        setTipoEspecial(false);
    }
    
    // --- Getters
    public String getIdODC(){ 
        return idODC; 
    }
    
    public List<Productov> getProductos(){ 
        return productos; 
    }
    
    public boolean getTipoEspecial(){ 
        return tipoEspecial; 
    }
    
    // --- Setters    
    public void setTipoEspecial(boolean tipo){
        this.tipoEspecial = tipo;
    }
}
