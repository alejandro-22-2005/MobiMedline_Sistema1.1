/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mike
 */
public class ODC {
    // --- Atributos --- 
    private static int contadorSiguiente = 1;
    
    private final String idODC;
    private final List<Producto> productos;
    private boolean tipoEspecial;
    private Usuarios responsable;
    
    // --- Constructor ---
    public ODC() {
        // Genera el ID con formato #####-ODC (ej. 00001-ODC)
        this.idODC = String.format("%05d-ODC", contadorSiguiente++);
        this.productos = new ArrayList<>();
        this.tipoEspecial = false;
        this.responsable = null;
    }
    
    public ODC(Usuarios responsable) {
        // Genera el ID con formato #####-ODC (ej. 00001-ODC)
        this.idODC = String.format("%05d-ODC", contadorSiguiente++);
        this.productos = new ArrayList<>();
        this.tipoEspecial = false;
        this.responsable = responsable;
    }
    
    /**
     * Verifica si el producto existe en nuetra List<Producto> productos
     * 1° Caso: Existe por lo que actualiza ese producto con la nueva cantidad
     * 2° Caso: No existe por lo que creamos la instancia de Porducto con su cantidad
     *          y usamos agregarProductoNuevo() para agregar en List<Producto> productos 
     * @param producto Producto a agregar a la ODC
     * @param cantidad Cantidad de ese producto
     * @autor Mike
     */
    public void actualizarOAgregarProducto(Producto producto, int cantidad) {
        boolean encontrado = false;
        
        // 1°caso. Existe el producto en nuestra ODC y vamos solo a actualizar
        for (Producto p : productos) {
            if (p.getSku().equals(producto.getSku())) {
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
     * Agrega una Intancia de Productov que no existe en List<Producto> productos 
     * y toma en cuenta la REGLA DE NEGOCIO: ODC Normal/Especial
     * @param producto Producto a agregar a la ODC
     * @autor Mike
     */
    public void agregarProductoNuevo(Producto producto) {
        // --- REGLA DE NEGOCIO: Si un producto tiene cantidad >= 10, es Especial
        this.productos.add(producto);
        if (producto.getCantidad() >= 10) {
            setTipoEspecial(true);
        }
    }
    
    /**
     * Elimina por nombre una instancia de Producto enlistada en nuestra 
     * List<Producto> productos
     * @param producto Elimina este producto de nuestra Lista de productos (nombre)
     * @autor Mike
     */
    public void eliminarProducto(Producto producto) {
        this.productos.remove(producto);
        recalcularTipoEspecial();
    }
    
    /**
     * Elimina por indice una instancia de Producto enlistada en nuestra 
     * List<Producto> productos, pensado para las tablas
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
     * Recorre nuestro List<Producto> productos para validar 
     * la  REGLA DE NEGOCIO: ODC Normal/Especial
     * @autor Mike
     */
    private void recalcularTipoEspecial() {
        for (Producto p : productos) {
            if (p.getCantidad() >= 10) {
                setTipoEspecial(true);
                break;
            }
        }
    }
    /**
     * Vacia la List<Producto> productos
     * @autor Mike
     */
    public void vaciarOrden() {
        this.productos.clear();
        setTipoEspecial(false);
    }
    
    /**
     * Realiza un recorrido con el Foreach para extraer cantidades, 
     * comparar insumos y agruparlos
     * @return Devuelve una ArryList<> con la suma total de los Insumos en toda la ODC
     * @autor Mike
     */
    public List<Insumo> obtenerTotalesPorId() {
        // El Map usará el ID (String o int) como llave para agrupar
        Map<String, Insumo> mapaConsolidado = new HashMap<>();

        // 1. Recorremos los productos de la ODC
        for (Producto p : productos) {
            int cantODC = p.getCantidad(); // Cantidad de este producto en la orden

            // 2. Recorremos los insumos de cada producto
            for (Insumo i : p.getInsumos()) {
                String idActual = i.getIdInsumo(); // Usamos el ID único
                int totalNecesario = i.getCantidadPorUnidad() * cantODC;

                if (mapaConsolidado.containsKey(idActual)) {
                    // Si el ID ya existe, extraemos el objeto y sumamos la cantidad
                    Insumo existente = mapaConsolidado.get(idActual);
                    existente.setCantidadPorUnidad(existente.getCantidadPorUnidad() + totalNecesario);
                } else {
                    // Si es la primera vez que vemos este ID, creamos una copia para la lista final
                    // Clonamos para no modificar los valores originales del catálogo
                    Insumo copia = new Insumo(i.getNombre(), totalNecesario);
                    copia.setIdInsumo(idActual); 
                    mapaConsolidado.put(idActual, copia);
                }
            }
        }
        // 3. Devolvemos los valores del mapa como una lista limpia
        return new ArrayList<>(mapaConsolidado.values());
    }
    
    // --- Getters
    public String getIdODC(){ 
        return idODC; 
    }
    
    public List<Producto> getProductos(){ 
        return productos; 
    }
    
    public boolean getTipoEspecial(){ 
        return tipoEspecial; 
    }

    public Usuarios getResponsable() {
        return responsable;
    }
    
    // --- Setters    
    public void setTipoEspecial(boolean tipo){
        this.tipoEspecial = tipo;
    }

    public void setResponsable(Usuarios responsable) {
        this.responsable = responsable;
    }
    
}
