/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

public class Insumov {
    
    private String nombre;
    private int cantidadPorUnidad;

    public Insumov(String nombre, int cantidadPorUnidad) {
        this.nombre = nombre;
        this.cantidadPorUnidad = cantidadPorUnidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadPorUnidad() {
        return cantidadPorUnidad;
    }

    public void setCantidadPorUnidad(int cantidadPorUnidad) {
        this.cantidadPorUnidad = cantidadPorUnidad;
    }
    
    @Override
    public String toString() {
        return nombre + " - " + cantidadPorUnidad + " unidades";
    }
}