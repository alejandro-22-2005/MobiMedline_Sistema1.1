package interfaz.mobimedline_sistema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Catalogov {

    private final Map<Integer, ProductoBasev> productos;

    public Catalogov () {
        productos = new HashMap<>();
        cargarProductos();
    }

    private void cargarProductos() {
        productos.put(101, new ProductoBasev(
                101,
                "Mesa de exploracion",
                Arrays.asList(
                        new Insumo("Tapiz", 1),
                        new Insumo("Perilla", 2),
                        new Insumo("Tornillos", 4)
                )
        ));

        productos.put(102, new ProductoBasev(
                102,
                "Vitrina futuro",
                Arrays.asList(
                        new Insumo("Cerradura", 1),
                        new Insumo("Vidrio", 2),
                        new Insumo("Remache", 6)
                )
        ));

        productos.put(103, new ProductoBasev(
                103,
                "Banco",
                Arrays.asList(
                        new Insumo("Barriles", 4),
                        new Insumo("Tapon redondo", 4),
                            new Insumo("Plato paara banco", 1)
                )
        ));
    }

    public ProductoBasev buscarPorId(int id) {
        return productos.get(id);
    }
}
