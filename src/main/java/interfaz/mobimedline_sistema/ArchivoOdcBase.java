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
    }

    public static List<ODC> getOdcBase() {
        return odcBase;
    }
}
