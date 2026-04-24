/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

/**
 *
 * @author Mike
 */
public class Usuarios {
    // --- Atributos --- 
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String contraseña;
    private boolean Permisos; 
    
    // --- Constructor ---
    public Usuarios(String nombre, String apellidoP, String apellidoM, String contraseña){
        this.nombre = nombre;
        apellidoPaterno = apellidoP;
        apellidoMaterno = apellidoM;
        this.contraseña = contraseña;
        Permisos = false;
    }
    
    // --- Getters
    public String getNombre() {
        return nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public String getContraseña() {
        return contraseña;
    }

    public boolean getPermisos() {
        return Permisos;
    }

    // --- Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setPermisos(boolean tipoUsuario) {
        this.Permisos = tipoUsuario;
    }   
}
