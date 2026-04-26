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
    
    private String sku;
    private String descripcion;
    private List<Insumo> insumos;

    public ProductoBasev(String sku, String descripcion, List<Insumo> insumos) {
        this.sku = sku;
        this.descripcion = descripcion;
        this.insumos = insumos;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Insumo> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<Insumo> insumos) {
        this.insumos = insumos;
    }
}
