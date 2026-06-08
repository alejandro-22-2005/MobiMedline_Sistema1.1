package interfaz.mobimedline_sistema;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author engel
 */
public class ProductoDAO {
    /**
     * Obtiene la lista completa de productos junto con sus insumos asociados y cantidades correspondientes.
     */
    public List<Producto> listar() {
        List<Producto> listaProductos = new ArrayList<>();
        
        // Consulta relacional para traernos el producto y todos sus insumos en un único viaje
        String sql = "SELECT p.sku, p.nombre AS producto_nombre, i.id_insumo, i.nombre AS insumo_nombre, pi.cantidad_requerida " +
                     "FROM PRODUCTO p " +
                     "LEFT JOIN PRODUCTO_INSUMO pi ON p.sku = pi.sku " +
                     "LEFT JOIN INSUMO i ON pi.id_insumo = i.id_insumo " +
                     "ORDER BY p.sku ASC";

        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            Producto productoActual = null;

            while (rs.next()) {
                String skuFormateado = String.format("%05d", rs.getInt("sku"));
                
                // Si cambiamos de producto o es el primero, lo instanciamos
                if (productoActual == null || !productoActual.getSku().equals(skuFormateado)) {
                    String descripcion = rs.getString("producto_nombre");
                    
                    // La cantidad global del stock del producto se inicializa en 0 (puedes alterarla según requieras)
                    productoActual = new Producto(skuFormateado, descripcion, 0, new ArrayList<>());
                    listaProductos.add(productoActual);
                }

                // Si la fila contiene un insumo asociado (debido al LEFT JOIN), lo agregamos
                int idInsumoRaw = rs.getInt("id_insumo");
                if (!rs.wasNull()) {
                    String idInsumoFormateado = String.format("%05d", idInsumoRaw);
                    String insumoNombre = rs.getString("insumo_nombre");
                    int cantidadRequerida = rs.getInt("cantidad_requerida");

                    Insumo insumoAsociado = new Insumo(idInsumoFormateado, insumoNombre, cantidadRequerida);
                    productoActual.agregarInsumo(insumoAsociado);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar productos con insumos: " + e.getMessage());
        }
        return listaProductos;
    }

    /**
     * Guarda un producto en la base de datos y registra su lista de insumos asociados en la tabla relacional.
     */
    public boolean insertar(Producto producto) {
        String sqlProducto = "INSERT INTO PRODUCTO (nombre) VALUES (?) RETURNING sku;";
        String sqlReceta = "INSERT INTO PRODUCTO_INSUMO (sku, id_insumo, cantidad_requerida) VALUES (?, ?, ?);";

        Connection con = null;
        PreparedStatement pstProd = null;
        PreparedStatement pstReceta = null;
        ResultSet rs = null;

        try {
            con = ConexionBaseDatos.getConexion();
            // 1. APAGAMOS EL AUTOCOMMIT (Inicia la transacción)
            con.setAutoCommit(false);

            // 2. Insertar el Producto y obtener el SKU autogenerado
            pstProd = con.prepareStatement(sqlProducto);
            pstProd.setString(1, producto.getDescripcion());
            rs = pstProd.executeQuery();

            int generatedSku = -1;
            if (rs.next()) {
                generatedSku = rs.getInt("sku");
            }

            // 3. Insertar la relación en la tabla intermedia (PRODUCTO_INSUMO)
            if (generatedSku != -1) {
                pstReceta = con.prepareStatement(sqlReceta);

                for (Insumo insumo : producto.getInsumos()) {
                    pstReceta.setInt(1, generatedSku);
                    int idInsumoEntero = Integer.parseInt(String.valueOf(insumo.getIdInsumo()).trim());
                    pstReceta.setInt(2, idInsumoEntero);
                    pstReceta.setInt(3, insumo.getCantidadPorUnidad());

                    // Usamos addBatch para agrupar de forma eficiente en lugar de ejecutar uno por uno
                    pstReceta.addBatch();
                }

                // Ejecutamos todo el bloque de insumos juntos
                pstReceta.executeBatch();
            }

            // 4. SI TODO SALIÓ BIEN, SE GUARDA EN LA BD GENERAL
            con.commit();
            return true;

        } catch (SQLException e) {
            // SI ALGO FALLA, HACEMOS ROLLBACK (No se guarda nada a medias en la BD)
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al insertar producto y su receta de insumos: " + e.getMessage());
            return false;
        } finally {
            // 5. CERRAMOS TODO EN ORDEN (Esto destruye los statements "S_1" y "S_2" de forma segura)
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstProd != null) pstProd.close(); } catch (SQLException e) {}
            try { if (pstReceta != null) pstReceta.close(); } catch (SQLException e) {}
            try { 
                if (con != null) {
                    con.setAutoCommit(true); // Devolvemos la conexión a su estado normal
                    con.close(); 
                } 
            } catch (SQLException e) {}
        }
    }

    /**
     * Actualiza el nombre de un producto y refresca su catálogo de insumos asignados.
     */
    public boolean actualizar(Producto producto) {
        String sqlUpdateProducto = "UPDATE PRODUCTO SET nombre = ? WHERE sku = ?";
        String sqlDeleteRelaciones = "DELETE FROM PRODUCTO_INSUMO WHERE sku = ?";
        String sqlInsertRelaciones = "INSERT INTO PRODUCTO_INSUMO (sku, id_insumo, cantidad_requerida) VALUES (?, ?, ?)";
        
        int skuReal = Integer.parseInt(producto.getSku());
        Connection con = null;
        
        try {
            con = ConexionBaseDatos.getConexion();
            con.setAutoCommit(false);

            // 1. Modificar datos de la entidad principal
            try (PreparedStatement pstProd = con.prepareStatement(sqlUpdateProducto)) {
                pstProd.setString(1, producto.getDescripcion());
                pstProd.setInt(2, skuReal);
                pstProd.executeUpdate();
            }

            // 2. Limpiar la receta vieja de insumos para este SKU
            try (PreparedStatement pstDel = con.prepareStatement(sqlDeleteRelaciones)) {
                pstDel.setInt(1, skuReal);
                pstDel.executeUpdate();
            }

            // 3. Volver a meter los insumos actualizados de la lista
            if (producto.getInsumos() != null && !producto.getInsumos().isEmpty()) {
                try (PreparedStatement pstIns = con.prepareStatement(sqlInsertRelaciones)) {
                    for (Insumo insumo : producto.getInsumos()) {
                        pstIns.setInt(1, skuReal);
                        pstIns.setInt(2, Integer.parseInt(insumo.getIdInsumo()));
                        pstIns.setInt(3, insumo.getCantidadPorUnidad());
                        pstIns.addBatch();
                    }
                    pstIns.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println(" Error al modificar el producto en la BD: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Elimina un producto. Por cascada (ON DELETE CASCADE), se borrarán sus registros en PRODUCTO_INSUMO.
     */
    public boolean eliminar(String sku) {
        String sql = "DELETE FROM PRODUCTO WHERE sku = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, Integer.parseInt(sku));
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }
}
