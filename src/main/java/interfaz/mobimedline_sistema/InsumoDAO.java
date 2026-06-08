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
import java.util.List;

/**
 *
 * @author engel
 */
public class InsumoDAO {
    /**
     * Obtiene todos los insumos de la base de datos.
     */
    public List<Insumo> listar() {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT id_insumo, nombre FROM INSUMO ORDER BY id_insumo ASC";
        
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                // Convertimos el id numérico al String formateado que espera tu clase (ej: "00001")
                String idFormateado = String.format("%05d", rs.getInt("id_insumo"));
                String nombre = rs.getString("nombre");
                
                // Inicializamos la cantidad por unidad en 0 o un valor por defecto (se mapea real en la relación)
                Insumo insumo = new Insumo(idFormateado, nombre, 0);
                lista.add(insumo);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar insumos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inserta un nuevo insumo en la base de datos.
     */
    public boolean insertar(Insumo insumo) {
        String sql = "INSERT INTO INSUMO (nombre) VALUES (?)";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, insumo.getNombre());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al insertar insumo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el nombre de un insumo basándose en su ID.
     */
    public boolean actualizar(Insumo insumo) {
        String sql = "UPDATE INSUMO SET nombre = ? WHERE id_insumo = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, insumo.getNombre());
            // Convertimos el String "00005" a un int real (5) para la BD
            pst.setInt(2, Integer.parseInt(insumo.getIdInsumo()));
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al actualizar insumo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un insumo de la base de datos por su ID.
     */
    public boolean eliminar(String idInsumo) {
        String sql = "DELETE FROM INSUMO WHERE id_insumo = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, Integer.parseInt(idInsumo));
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al eliminar insumo: " + e.getMessage());
            return false;
        }
    }
}
