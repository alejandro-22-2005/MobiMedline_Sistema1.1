/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

/**
 *  Excepción lanzada internamente cuando el código de usuario generado 
 * ya se encuentra registrado en la base de datos estática.
 * @author engel
 */

public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
