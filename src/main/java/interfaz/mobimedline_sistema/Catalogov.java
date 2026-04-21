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
                        new Insumov("Tapiz", 1),
                        new Insumov("Perilla", 2),
                        new Insumov("Tornillos", 4)
                )
        ));

        productos.put(102, new ProductoBasev(
                102,
                "Vitrina futuro",
                Arrays.asList(
                        new Insumov("Cerradura", 1),
                        new Insumov("Vidrio", 2),
                        new Insumov("Remache", 6)
                )
        ));

        productos.put(103, new ProductoBasev(
                103,
                "Banco",
                Arrays.asList(
                        new Insumov("Barriles", 4),
                        new Insumov("Tapon redondo", 4),
                            new Insumov("Plato paara banco", 1)
                )
        ));
    }

    public ProductoBasev buscarPorId(int id) {
        return productos.get(id);
    }
}
