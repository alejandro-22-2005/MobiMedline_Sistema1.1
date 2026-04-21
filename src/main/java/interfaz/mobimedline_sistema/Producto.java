package interfaz.mobimedline_sistema;

import java.util.ArrayList;
import java.util.List;

public class Producto {
    private String codigo;
    private String nombre;
    private List<Insumo> insumos;

    public Producto(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.insumos = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Insumo> getInsumos() {
        return insumos;
    }

    public void agregarInsumo(Insumo insumo) {
        this.insumos.add(insumo);
    }

    public void eliminarInsumo(int index) {
        if (index >= 0 && index < insumos.size()) {
            this.insumos.remove(index);
        }
    }

    public int getTotalInsumos() {
        return insumos.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto{codigo='").append(codigo).append("', nombre='").append(nombre).append("', insumos=[");
        for (int i = 0; i < insumos.size(); i++) {
            sb.append(insumos.get(i).toString());
            if (i < insumos.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}