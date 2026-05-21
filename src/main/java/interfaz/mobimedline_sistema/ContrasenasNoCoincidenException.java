/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

/**
 *  Excepción lanzada cuando las contraseñas ingresadas no son idénticas.
 * @author engel
 */
public class ContrasenasNoCoincidenException extends Exception {
    public ContrasenasNoCoincidenException(String mensaje) {
        super(mensaje);
    }
}

