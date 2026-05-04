/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.List;

/**
 *Clase dedicada a guardar las ODC cargados en el codigo y las que se generen
 * @author Mike
 */
public class ArchivoOdcBase {
    private static List<ODC> odcBase = new ArrayList<>();
    
    // Bloque estático para inicializar las ODC base una sola vez
    
    static {
        //aqui va las ODC base a mostrar en la expo
        List<Producto> productos = CatalogoProductosBase.getProductosBase();
        List<Usuarios> directorioUsuarios = AgendaUsuariosBase.getUsuariosBase();
        //aquí se hicieron los cambios
        //  ODC 1 
        ODC odc1 = new ODC(directorioUsuarios.get(0), "2026-04-25", "Pendiente");
        odc1.actualizarOAgregarProducto(productos.get(0),3); // Vitrina Futuro
        odc1.actualizarOAgregarProducto(productos.get(2),1); // Escalerilla
        odcBase.add(odc1);

        //  ODC 2 
        ODC odc2 = new ODC(directorioUsuarios.get(1), "2026-04-25", "Emitida");
        odc2.actualizarOAgregarProducto(productos.get(1),4); // Mesa Premium
        odc2.actualizarOAgregarProducto(productos.get(3),2); // Silla
        odcBase.add(odc2);

        //  ODC 3 
        ODC odc3 = new ODC(directorioUsuarios.get(2), "2026-04-25", "Pendiente");
        odc3.actualizarOAgregarProducto(productos.get(0),1); // Vitrina
        odc3.actualizarOAgregarProducto(productos.get(1),5); // Mesa
        odc3.actualizarOAgregarProducto(productos.get(2),10); // Escalerilla
        odcBase.add(odc3);
    }

    public static List<ODC> getOdcBase() {
        return odcBase;
    }
    
    public static void agregarODC(ODC odc) {
    odcBase.add(odc);
}
}
