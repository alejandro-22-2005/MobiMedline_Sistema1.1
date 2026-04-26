/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.List;

/**
 *Clase dedicada a guardar los productos cargados en el codigo
 * @author Mike
 */
public class CatalogoProductosBase {
    private static List<Producto> productosBase = new ArrayList<>();

    // Bloque estático para inicializar los productos base una sola vez
    static {
        // --- Producto Base: Vitrina Futuro ---
        Producto VitrinaFuturo = new Producto("Vitrina Modelo Futuro");
        VitrinaFuturo.agregarInsumo(new Insumo("Vidrio de 33x60 cm", 1));
        VitrinaFuturo.agregarInsumo(new Insumo("Cerradura", 1));
        VitrinaFuturo.agregarInsumo(new Insumo("Pija punta de broca de 1/4 de pulgada", 6));
        // Se crea con la plantilla
        productosBase.add(VitrinaFuturo);
        

        // --- Producto Base: Mesa de Exploracion Premium ---
        Producto MesaExpPremium = new Producto("Mesa de Exploración Premium");
        MesaExpPremium.agregarInsumo(new Insumo("Tapiz para Mesa de exploración", 1));
        MesaExpPremium.agregarInsumo(new Insumo("Tope de plástico", 4));
        MesaExpPremium.agregarInsumo(new Insumo("Agarradera de plástico", 2));
        MesaExpPremium.agregarInsumo(new Insumo("Columpio grande", 1));
        MesaExpPremium.agregarInsumo(new Insumo("Columpio chico", 1));
        MesaExpPremium.agregarInsumo(new Insumo("Pija punta de broca de 1/4 de pulgada", 4));
        MesaExpPremium.agregarInsumo(new Insumo("Tornillo", 4));
        MesaExpPremium.agregarInsumo(new Insumo("Tuerca", 4));
        productosBase.add(MesaExpPremium);
        
        
        // --- Producto Base: Escalerilla ---
        Producto Escalerilla = new Producto("Escalerilla");
        Escalerilla.agregarInsumo(new Insumo("Barril de plástico", 4));
        Escalerilla.agregarInsumo(new Insumo("Pija punta de broca de 1/4 de pulgada", 8));
        // Se crea con la plantilla
        productosBase.add(Escalerilla);
        
        
         // --- Producto Base: Silla Toma de Muestra ---
        Producto SillaTomadeMuestra = new Producto("Silla Toma de Muestra");
        SillaTomadeMuestra.agregarInsumo(new Insumo("Tapiz para Silla toma de muestra", 1));
        SillaTomadeMuestra.agregarInsumo(new Insumo("Cubierta de acero inoxidable de 30x30 cm", 1));
        SillaTomadeMuestra.agregarInsumo(new Insumo("Pija punta de broca de 1/4 de pulgada", 12));
        SillaTomadeMuestra.agregarInsumo(new Insumo("Tapón cuadrado de plástico de 1 1/4 de pulgada", 4));
        // Se crea con la plantilla
        productosBase.add(SillaTomadeMuestra);
        
        
         // --- Producto Base: Gabinete Hamilton ---
        Producto GabineteHamilton = new Producto("Gabinete Hamilton");
        GabineteHamilton.agregarInsumo(new Insumo("Vidrio de 30x45 cm", 1));
        GabineteHamilton.agregarInsumo(new Insumo("Cubierta de acero inoxidable de 30x65 cm", 1));
        GabineteHamilton.agregarInsumo(new Insumo("Pija punta de broca de 1/4 de pulgada", 6));
        GabineteHamilton.agregarInsumo(new Insumo("Barandal para Gabinete Hamilton", 1));
        // Se crea con la plantilla
        productosBase.add(GabineteHamilton);
    }

    public static List<Producto> getProductosBase() {
        return productosBase;
    }
    
    public static Producto buscarPorSku(String skuBuscado){ //función para buscar un producto por sku
        for(Producto p:productosBase){
            if(p.getSku().equalsIgnoreCase(skuBuscado)){
                Producto nuevoProducto = p;
                return nuevoProducto;
            }
        }
        return null;
    }
    
    //falta aplicar funsion de busqueda y aplicar a todos los nombres en mayuscula para eviatar problemas.
}
