/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

/**
 *  Excepción lanzada cuando hay campos obligatorios vacíos en el formulario.
 * @author engel
 */

public class CamposIncompletosException extends Exception{
    public CamposIncompletosException(String mensaje) {
        super(mensaje);
    }
}
