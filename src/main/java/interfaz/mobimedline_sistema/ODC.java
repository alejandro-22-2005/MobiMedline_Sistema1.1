/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author engel
 */
public class ODC {
    // --- Atributos --- 
    private static int contadorSiguiente = 1;
    
    private final String idODC;
    private final List<Productov> productos;
    private String tipoEspecial;
    
    // --- Constructor ---
    public ODC() {
        // Genera el ID con formato #####-ODC (ej. 00001-ODC)
        this.idODC = String.format("%05d-ODC", contadorSiguiente++);
        this.productos = new ArrayList<>();
        this.tipoEspecial = "Normal";
    }
    
    /**
     * 
     * @param producto Producto a Agregar
     */
    public void agregarProducto(Productov producto) {
        // --- REGLA DE NEGOCIO: Si un producto tiene cantidad >= 10, es Especial
        // Usamos el método getCantidad() de tu clase Productov
        this.productos.add(producto);
        if (producto.getCantidad() >= 10) {
            setTipoODC(true);
        }
    }
    
    // --- Getters
    public String getIdODC(){ 
        return idODC; 
    }
    
    public String getTipoODC(){ 
        return tipoEspecial; 
    }
    
    public List<Productov> getProductos(){ 
        return productos; 
    }
    
    // --- Setters
    /**
     * 
     * @param tipo true = Especial; false = Normal 
     */
    public void setTipoODC(boolean tipo){
        if(tipo == true)
            this.tipoEspecial = "Especial";
        else
            this.tipoEspecial = "Normal";
    }
}
