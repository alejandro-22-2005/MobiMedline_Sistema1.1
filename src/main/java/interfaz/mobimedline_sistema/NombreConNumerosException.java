/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

/**
 * Excepción lanzada cuando los campos de texto de nombre o apellidos 
 * contienen caracteres numéricos inválidos.
 * @author engel
 */

public class NombreConNumerosException extends Exception {
    public NombreConNumerosException(String mensaje) {
        super(mensaje);
    }
}
