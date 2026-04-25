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
public class CatalogoProductosBase {
    private static List<Producto> productosBase = new ArrayList<>();

    // Bloque estático para inicializar los productos base una sola vez
    static {
        // --- Producto Base: Mesa ---
        Producto mesa = new Producto("Mesa Estándar");
        mesa.agregarInsumo(new Insumo("Tornillo", 8));
        mesa.agregarInsumo(new Insumo("Tabla Madera", 1));
        mesa.agregarInsumo(new Insumo("Pata Metálica", 4));
        // Se crea con la plantilla
        productosBase.add(mesa);
        

        // --- Producto Base: Silla ---
        Producto silla = new Producto("Silla");
        silla.agregarInsumo(new Insumo("Respaldo", 1));
        silla.agregarInsumo(new Insumo("Asiento", 1));
        silla.agregarInsumo(new Insumo("Tornillo", 4));
        productosBase.add(silla);
    }

    public static List<Producto> getProductosBase() {
        return productosBase;
    }
    
    //falta aplicar funsion de busqueda y aplicar a todos los nombres en mayuscula para eviatar problemas.
}
