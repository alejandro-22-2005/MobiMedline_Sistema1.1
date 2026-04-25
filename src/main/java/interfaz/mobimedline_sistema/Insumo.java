/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

public class Insumo {
    // --- Atributos --- 
    private static int contadorSiguiente = 1;
    
    private String idInsumo;
    private String nombre;
    private int cantidad;
    
    // --- Constructor ---
    public Insumo(String nombre, int cantidadPorUnidad){
        this.idInsumo = String.format("%05d", contadorSiguiente++);
        this.nombre = nombre;
        this.cantidad = cantidadPorUnidad;
    }
    
    // **Reservado para la copia**
    public Insumo(String id, String nombre, int cantidadPorUnidad){
        this.idInsumo = id;
        this.nombre = nombre;
        this.cantidad = cantidadPorUnidad;
    }
    
    public String getIdInsumo(){
        return idInsumo;
    }
    
    public String getNombre(){
        return nombre;
    }

    public int getCantidadPorUnidad(){
        return cantidad;
    }
    
    public void setIdInsumo(String id){
        idInsumo = id;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setCantidadPorUnidad(int cantidadPorUnidad){
        this.cantidad = cantidadPorUnidad;
    }
    
    @Override
    public String toString(){
        return nombre + " - " + cantidad + " unidades";
    }
}