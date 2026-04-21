/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.util.List;

/**
 *
 * @author Lenovo
 */
public class ProductoBasev {
    
    private int id;
    private String descripcion;
    private List<Insumov> insumos;

    public ProductoBasev(int id, String descripcion, List<Insumov> insumos) {
        this.id = id;
        this.descripcion = descripcion;
        this.insumos = insumos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Insumov> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<Insumov> insumos) {
        this.insumos = insumos;
    }
}
