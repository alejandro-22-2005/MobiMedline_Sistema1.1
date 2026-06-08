/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author engel
 */
public class ODCDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * Inserta una ODC completa junto con su lista de productos asociados de forma transaccional.
     * @param odc Objeto ODC con su lista de productos cargada.
     * @return true si la orden completa se guardó con éxito; false en caso de error.
     */
    // Tu método principal de inserción transaccional
    public boolean insertar(ODC nuevaODC) {
        String sqlFicha = "INSERT INTO FICHA_ODC (fecha_emision, id_usuario, estado_odc, tipo_odc) " +
                          "VALUES (?, (SELECT id_usuario FROM USUARIO WHERE usuario = ?), ?, ?) " +
                          "RETURNING id_ficha_odc";
        
        String sqlOdc = "INSERT INTO ODC (id_ficha_odc) VALUES (?) RETURNING id_odc";
        String sqlOdcProducto = "INSERT INTO ODC_PRODUCTO (id_odc, sku) VALUES (?, ?)";
        String sqlOdcInsumo = "INSERT INTO ODC_INSUMO (id_odc, id_insumo, cantidad_total) VALUES (?, ?, ?)";

        Connection con = null;
        PreparedStatement pstFicha = null;
        PreparedStatement pstOdc = null;
        PreparedStatement pstOdcProducto = null;
        PreparedStatement pstOdcInsumo = null;
        ResultSet rs = null;

        try {
            con = ConexionBaseDatos.getConexion(); 
            con.setAutoCommit(false); 

            // --- PASO 1: Insertar en FICHA_ODC ---
            pstFicha = con.prepareStatement(sqlFicha);
            pstFicha.setDate(1, java.sql.Date.valueOf(nuevaODC.getFechaODC())); 
            pstFicha.setString(2, nuevaODC.getResponsable().getUsuario()); 
            pstFicha.setString(3, nuevaODC.getEstado()); 
            pstFicha.setString(4, nuevaODC.getTipoEspecial() ? "especial" : "normal");

            rs = pstFicha.executeQuery();
            int idFichaGenerado = 0;
            if (rs.next()) {
                idFichaGenerado = rs.getInt(1);
            }

            // --- PASO 2: Insertar en ODC ---
            pstOdc = con.prepareStatement(sqlOdc);
            pstOdc.setInt(1, idFichaGenerado);
            
            rs = pstOdc.executeQuery();
            int idOdcGenerado = 0;
            if (rs.next()) {
                idOdcGenerado = rs.getInt(1);
            }

            // --- PASO 3: Insertar los Productos ---
            pstOdcProducto = con.prepareStatement(sqlOdcProducto);
            for (Producto p : nuevaODC.getProductos()) {
                pstOdcProducto.setInt(1, idOdcGenerado);
                pstOdcProducto.setInt(2, Integer.parseInt(p.getSku())); 
                pstOdcProducto.addBatch();
            }
            pstOdcProducto.executeBatch(); 

            // --- PASO 4: Insertar los Insumos consolidados ---
            // AQUÍ LLAMAMOS AL MÉTODO NUEVO PASÁNDOLE LA CONEXIÓN ACTIVA Y LA ODC
            List<Insumo> insumosConsolidados = obtenerTotalesInsumos(con, nuevaODC);
            
            pstOdcInsumo = con.prepareStatement(sqlOdcInsumo);
            for (Insumo insumoConsolidado : insumosConsolidados) {
                pstOdcInsumo.setInt(1, idOdcGenerado);
                pstOdcInsumo.setInt(2, Integer.parseInt(insumoConsolidado.getIdInsumo()));
                pstOdcInsumo.setInt(3, insumoConsolidado.getCantidadPorUnidad()); 
                pstOdcInsumo.addBatch();
            }
            pstOdcInsumo.executeBatch(); 

            con.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Error al insertar la ODC transaccional: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {}
            }
            return false;
        } finally {
            // Cierre de recursos habitual...
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstFicha != null) pstFicha.close(); } catch (Exception e) {}
            try { if (pstOdc != null) pstOdc.close(); } catch (Exception e) {}
            try { if (pstOdcProducto != null) pstOdcProducto.close(); } catch (Exception e) {}
            try { if (pstOdcInsumo != null) pstOdcInsumo.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    // EL MÉTODO COLOCADO EN EL DAO
    // Recibe la conexión "con" existente para reutilizarla y no abrir múltiples conexiones hilos
    private List<Insumo> obtenerTotalesInsumos(Connection con, ODC nuevaODC) throws Exception {
        Map<String, Insumo> mapaConsolidado = new HashMap<>();

        String sqlReceta = "SELECT pi.id_insumo, i.nombre, pi.cantidad_requerida " +
                           "FROM PRODUCTO_INSUMO pi " +
                           "JOIN INSUMO i ON pi.id_insumo = i.id_insumo " +
                           "WHERE pi.sku = ?";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            for (Producto p : nuevaODC.getProductos()) {
                int cantODC = p.getCantidad(); 
                
                pst = con.prepareStatement(sqlReceta);
                pst.setInt(1, Integer.parseInt(p.getSku())); 
                rs = pst.executeQuery();

                while (rs.next()) {
                    String idActual = String.valueOf(rs.getInt("id_insumo")); 
                    String nombreInsumo = rs.getString("nombre");
                    int cantPorUnidad = rs.getInt("cantidad_requerida");

                    int totalNecesario = cantPorUnidad * cantODC;

                    if (mapaConsolidado.containsKey(idActual)) {
                        Insumo existente = mapaConsolidado.get(idActual);
                        existente.setCantidadPorUnidad(existente.getCantidadPorUnidad() + totalNecesario);
                    } else {
                        Insumo copia = new Insumo(nombreInsumo, totalNecesario);
                        copia.setIdInsumo(idActual); 
                        mapaConsolidado.put(idActual, copia);
                    }
                }
                rs.close();
                pst.close();
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            // NOTA: No cerramos 'con' aquí porque la ocupa el método insertar para el commit/rollback
        }

        return new ArrayList<>(mapaConsolidado.values());
    }

    /**
     * Recupera el histórico de Órdenes de Compra, reconstruyendo sus usuarios y productos asociados.
     * @return Lista de objetos ODC con sus respectivas colecciones internas mapeadas.
     */
    public List<ODC> listar() {
        List<ODC> listaOrdenes = new ArrayList<>();
        // Hacemos un JOIN para jalar directamente el texto del nombre del usuario de la tabla vinculada
        String sql = "SELECT o.id_odc, u.nombre_usuario, o.fecha_odc, o.estado, o.tipo_especial " +
                     "FROM ODC o " +
                     "LEFT JOIN USUARIOS u ON o.id_usuario = u.id_usuario " +
                     "ORDER BY o.id_odc DESC;";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBaseDatos.getConexion();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            List<Usuarios> todosLosUsuarios = usuarioDAO.listar();
            List<Producto> todosLosProductos = productoDAO.listar();

            while (rs.next()) {
                String idOdc = rs.getString("id_odc");
                String nomUsuarioBD = rs.getString("nombre_usuario"); // Obtenemos el texto
                String fecha = rs.getString("fecha_odc");
                String estado = rs.getString("estado");
                boolean esEspecial = rs.getBoolean("tipo_especial");

                // Buscamos el objeto Usuario en tu lista usando el nombre de usuario de Java
                Usuarios responsable = todosLosUsuarios.stream()
                        .filter(u -> u.getUsuario().equalsIgnoreCase(nomUsuarioBD))
                        .findFirst()
                        .orElse(null);

                ODC odc = new ODC(responsable, fecha, estado);

                // Forzar ID con reflexión (Tu lógica actual)
                try {
                    java.lang.reflect.Field campoId = ODC.class.getDeclaredField("idODC");
                    campoId.setAccessible(true);
                    campoId.set(odc, idOdc);
                } catch (Exception ex) {}

                odc.setTipoEspecial(esEspecial);

                // Cargar los productos de esta orden
                cargarProductosDeLaOrden(idOdc, odc, todosLosProductos);

                listaOrdenes.add(odc);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ODC: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pst != null) pst.close(); } catch (SQLException e) {}
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
        return listaOrdenes;
    }

    /**
     * Método auxiliar encargado de consultar la tabla intermedia e inyectar los productos con sus respectivas cantidades en la ODC.
     */
    private void cargarProductosDeLaOrden(String idOdc, ODC odc, List<Producto> catalogoProductos) {
        String sqlDetalle = "SELECT sku, cantidad_requerida FROM ODC_PRODUCTO WHERE id_odc = ?;";
        
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sqlDetalle)) {
            
            pst.setString(1, idOdc);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String sku = rs.getString("sku");
                    int cantidad = rs.getInt("cantidad_requerida");

                    // Buscamos el producto base del catálogo de la BD para jalar sus insumos estructurados
                    Producto productoBase = catalogoProductos.stream()
                            .filter(p -> p.getSku().equalsIgnoreCase(sku))
                            .findFirst()
                            .orElse(null);

                    if (productoBase != null) {
                        // Creamos una copia nueva para esta orden para evitar alterar el catálogo en memoria
                        Producto prodParaOrden = new Producto(productoBase.getDescripcion());
                        
                        // Copiamos el SKU real
                        java.lang.reflect.Field fieldSku = Producto.class.getDeclaredField("sku");
                        fieldSku.setAccessible(true);
                        fieldSku.set(prodParaOrden, productoBase.getSku());

                        // Traspasamos sus insumos heredados
                        if (productoBase.getInsumos() != null) {
                            for (Insumo ins : productoBase.getInsumos()) {
                                prodParaOrden.agregarInsumo(ins);
                            }
                        }
                        
                        // Inyectamos la cantidad y lo anexamos a la ODC usando tus métodos de negocio
                        odc.actualizarOAgregarProducto(prodParaOrden, cantidad);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al enlazar productos a la ODC " + idOdc + ": " + e.getMessage());
        }
    }

    /**
     * Modifica el estado o los metadatos generales de una Orden de Compra.
     * @param odc Objeto ODC modificado.
     * @return true si la actualización en Supabase fue exitosa.
     */
    public boolean actualizar(ODC odc) {
        String sql = "UPDATE ODC SET estado = ?, tipo_especial = ? WHERE id_odc = ?;";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, odc.getEstado());
            pst.setBoolean(2, odc.getTipoEspecial());
            pst.setString(3, odc.getIdODC());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la ODC: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una ODC de la base de datos junto con su detalle relacional.
     * @param idOdc Identificador alfanumérico (ej: '00001-ODC')
     * @return true si se borró correctamente.
     */
    public boolean eliminar(String idOdc) {
        // Primero removemos el desglose de productos por restricciones de integridad referencial (FK)
        String sqlDetalle = "DELETE FROM ODC_PRODUCTO WHERE id_odc = ?;";
        String sqlCabecera = "DELETE FROM ODC WHERE id_odc = ?;";

        try (Connection con = ConexionBaseDatos.getConexion()) {
            con.setAutoCommit(false);

            try (PreparedStatement pstDet = con.prepareStatement(sqlDetalle);
                 PreparedStatement pstCab = con.prepareStatement(sqlCabecera)) {
                
                pstDet.setString(1, idOdc);
                pstDet.executeUpdate();

                pstCab.setString(1, idOdc);
                int filasAfectadas = pstCab.executeUpdate();

                con.commit();
                return filasAfectadas > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la ODC e historial: " + e.getMessage());
            return false;
        }
    }
    
    public List<ODC> obtenerTodas() {
        List<ODC> lista = new ArrayList<>();

        // 1. Query principal: Junta la orden con su ficha y los datos legibles del usuario
        String sql = "SELECT o.id_odc, f.fecha_emision, f.estado_odc, u.usuario, u.nombre " +
                     "FROM odc o " +
                     "JOIN ficha_odc f ON o.id_ficha_odc = f.id_ficha_odc " +
                     "JOIN usuario u ON f.id_usuario = u.id_usuario " +
                     "ORDER BY o.id_odc DESC";

        // 2. Query secundaria: Busca los productos asociados al id_odc real de la tabla intermedia
        String sqlProductos = "SELECT p.sku, p.nombre " +
                        "FROM odc_producto op " +
                        "JOIN producto p ON op.sku = p.sku " +
                        "WHERE op.id_odc = ?";

        Connection con = null;
        PreparedStatement pst = null;
        PreparedStatement pstProd = null;
        ResultSet rs = null;
        ResultSet rsProd = null;

        try {
            // Usamos tu clase de conexión configurada
            con = ConexionBaseDatos.getConexion();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                // Reconstruimos el objeto Usuarios usando solo los Strings (SIN id_usuario)
                Usuarios responsable = new Usuarios(rs.getString("usuario"), rs.getString("nombre"));

                // Extraemos los identificadores y propiedades de la ODC
                String idOdcStr = String.valueOf(rs.getInt("id_odc"));
                String fecha = rs.getDate("fecha_emision") != null ? rs.getDate("fecha_emision").toString() : "";
                String estado = rs.getString("estado_odc");

                // Instanciamos el objeto ODC con los datos limpios
                ODC orden = new ODC(responsable, fecha, estado);
                orden.setIdODC(idOdcStr); // Seteamos el ID mutable de la orden para las tablas de la interfaz

                // 3. Cargamos los productos vinculados a esta ODC específica
                pstProd = con.prepareStatement(sqlProductos);
                pstProd.setInt(1, rs.getInt("id_odc"));
                rsProd = pstProd.executeQuery();

                while (rsProd.next()) {
                    Producto prod = new Producto(
                        String.valueOf(rsProd.getInt("sku")),
                        rsProd.getString("nombre"),
                        rsProd.getInt(1),
                        new ArrayList<>() // Lista de insumos vacía para el constructor base
                    );
                    orden.getProductos().add(prod);
                }
                rsProd.close();
                pstProd.close();

                // Añadimos la orden procesada a la lista final
                lista.add(orden);
            }
        } catch (Exception e) {
            System.err.println("Error al consultar todas las ODC: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cierre seguro de todos los canales de comunicación con Supabase
            try { if (rsProd != null) rsProd.close(); } catch (Exception e) {}
            try { if (pstProd != null) pstProd.close(); } catch (Exception e) {}
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pst != null) pst.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return lista;
    }

    // --- MÉTODO PARA ACTUALIZAR EL ESTADO (COMBOBOX DE LA TABLA) ---
    public boolean actualizarEstado(String idOdc, String nuevoEstado) {
        String sql = "UPDATE FICHA_ODC SET estado_odc = ? WHERE id_ficha_odc = ?";
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = ConexionBaseDatos.getConexion();
            pst = con.prepareStatement(sql);
            pst.setString(1, nuevoEstado);
            pst.setInt(2, Integer.parseInt(idOdc));

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar estado de la ODC: " + e.getMessage());
            return false;
        } finally {
            try { if (pst != null) pst.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
    
    public List<Insumo> obtenerInsumosDeOrden(String idOdc) {
        List<Insumo> listaInsumos = new ArrayList<>();

        if (idOdc == null || idOdc.trim().isEmpty()) {
            return listaInsumos;
        }

        // CORRECCIÓN TOTAL DEL JOIN: i.id_insumo es INT, oi.id_insumo es INT. No usar ::varchar
        String sql = "SELECT i.nombre, oi.cantidad_total " +
                     "FROM ODC_INSUMO oi " +
                     "JOIN INSUMO i ON oi.id_insumo = i.id_insumo " +
                     "WHERE oi.id_odc = ?";

        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(idOdc.trim()));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Insumo insumo = new Insumo(
                        rs.getString("nombre"),
                        rs.getInt("cantidad_total")
                    );
                    listaInsumos.add(insumo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar la tabla ODC_INSUMO en Supabase: " + e.getMessage());
            e.printStackTrace();
        }
        return listaInsumos;
    }
}
