/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author engel
 */

public class UsuarioDAO {
    // Método para guardar
    public boolean registrar(Usuarios usr) {
        String sql = "INSERT INTO usuario (nombre, apellido, usuario, password, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
        
        // Al usar ConexionBaseDatos.getConexion(), ya herede las credenciales ocultas
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            //pst.setInt(1, usr.getIdUsuario()); //se va eliminar
            pst.setString(1, usr.getNombre());
            
            String apellidos = usr.getApellidoPaterno() + " " + usr.getApellidoMaterno();
            pst.setString(2, apellidos);
            
            pst.setString(3, usr.getUsuario());
            
            
            pst.setString(4, usr.getContraseña());
            
            String acceso = usr.getPermisos() ? "gerente" : "colaborador";
            pst.setString(5, acceso);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al registrar usuario en DAO: " + e.getMessage());
            return false;
        }
    }

    // Método para traer los datos desde Supabase y convertirlos en objetos de Java
    public List<Usuarios> listar() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre ASC";

        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Usuarios usr = new Usuarios();
                usr.setUsuario(rs.getString("usuario"));
                usr.setContraseña(rs.getString("password"));

                // Reconstruimos el nombre completo partiendo los apellidos de tu base si es necesario,
                // o mapeando directo a tu atributo 'nombre'.
                usr.setNombre(rs.getString("nombre"));

                // Si tu tabla maneja los campos separados, asígnalos:
                // usr.setApellidoPaterno(rs.getString("apellido_paterno"));

                // Mapeo del rol gerencial
                String tipo = rs.getString("tipo_usuario");
                usr.setPermisos("gerente".equalsIgnoreCase(tipo));

                lista.add(usr);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios desde la BD: " + e.getMessage());
        }
        return lista;
    }
    
    public Usuarios buscarUsuarioPorCredenciales(String nickUsuario, String passwordPlana) {
        // Buscamos un registro que coincida exactamente con ambos campos en la base de datos
        String sql = "SELECT nombre, apellido, usuario, password, tipo_usuario FROM usuario WHERE usuario = ? AND password = ?";

        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            // Pasamos los parámetros de forma segura para evitar Inyección SQL
            pst.setString(1, nickUsuario);
            pst.setString(2, passwordPlana);

            try (ResultSet rs = pst.executeQuery()) {
                // Si rs.next() es true, significa que las credenciales coinciden perfectamente con un usuario
                if (rs.next()) {
                    Usuarios usr = new Usuarios();
                    
                    usr.setNombre(rs.getString("nombre"));
                    usr.setUsuario(rs.getString("usuario"));
                    usr.setContraseña(rs.getString("password")); // Se recupera en texto plano
                    
                    // Tratamiento de los apellidos (reconstruimos paterno y materno)
                    String apellidosCompletos = rs.getString("apellido");
                    if (apellidosCompletos != null && !apellidosCompletos.trim().isEmpty()) {
                        String[] partes = apellidosCompletos.split(" ", 2);
                        if (partes.length >= 2) {
                            usr.setApellidoPaterno(partes[0]);
                            usr.setApellidoMaterno(partes[1]);
                        } else {
                            usr.setApellidoPaterno(partes[0]);
                            usr.setApellidoMaterno("");
                        }
                    }
                    
                    // Tratamiento del rol para el atributo booleano Permisos
                    String tipoUsuario = rs.getString("tipo_usuario");
                    usr.setPermisos("gerente".equalsIgnoreCase(tipoUsuario));
                    
                    return usr; // Retornamos el objeto usuario con sus datos completos de sesión
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al consultar las credenciales en DAO: " + e.getMessage());
        }
        
        return null; // Si no hubo coincidencias o ocurrió un error, el acceso se deniega devolviendo null
    }
    
    /**
    * Verifica si un nombre de usuario ya existe en Supabase.
    */
    public boolean existeUsuario(String usuario) {
       String sql = "SELECT COUNT(*) FROM usuario WHERE LOWER(usuario) = LOWER(?)";
       try (Connection con = ConexionBaseDatos.getConexion();
            PreparedStatement pst = con.prepareStatement(sql)) {

           pst.setString(1, usuario);
           try (ResultSet rs = pst.executeQuery()) {
               if (rs.next()) {
                   return rs.getInt(1) > 0; // Si el conteo es mayor a 0, ya está duplicado
               }
           }
       } catch (SQLException e) {
           System.err.println(" Error al verificar usuario único en la BD: " + e.getMessage());
       }
       return false;
    }

    /**
    * Busca en Supabase todos los usuarios que comiencen con las 4 letras base
    * y calcula el siguiente número secuencial disponible (+1).
    */
    public int obtenerSiguienteConsecutivoBD(String inicialesCuatroLetras) {
        int maxContador = 1;
        // Buscamos los usuarios que empiecen con las iniciales usando el operador LIKE
        String sql = "SELECT usuario FROM usuario WHERE LOWER(usuario) LIKE LOWER(?)";

        try (Connection con = ConexionBaseDatos.getConexion();
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, inicialesCuatroLetras + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String idExistente = rs.getString("usuario");

                    if (idExistente != null && idExistente.length() >= 4) {
                        try {
                           // Extraemos los dígitos desde la posición 4 en adelante
                           int numeroAsignado = Integer.parseInt(idExistente.substring(4));
                           if (numeroAsignado >= maxContador) {
                               maxContador = numeroAsignado + 1;
                           }
                        } catch (NumberFormatException e) {
                           // Omitimos registros que no terminen estrictamente en formato numérico
                        }
                    }
                }
            }
        } catch (SQLException e) {
           System.err.println(" Error al calcular el consecutivo en la BD: " + e.getMessage());
        }
        return maxContador;
    }
    
    public boolean actualizar(Usuarios usr) {
        String sql = "UPDATE usuario SET nombre = ?, password = ? WHERE usuario = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, usr.getNombre());
            pst.setString(2, usr.getContraseña());
            pst.setString(3, usr.getUsuario());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al actualizar usuario en la BD: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un usuario por completo de Supabase.
     */
    public boolean eliminar(String usuarioId) {
        String sql = "DELETE FROM usuario WHERE usuario = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, usuarioId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al eliminar usuario de la BD: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
    * Busca un usuario en la base de datos por su alias único (username).
    * @param nombreUsuario El alias/username a buscar.
    * @return Un objeto Usuarios si se encuentra, o null si no existe.
    */
    public Usuarios buscarPorUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM USUARIO WHERE usuario = ?";
        try (Connection con = ConexionBaseDatos.getConexion();
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nombreUsuario);
            try (ResultSet rs = pst.executeQuery()) {
                // Si rs.next() es true, significa que las credenciales coinciden perfectamente con un usuario
                if (rs.next()) {
                    Usuarios usr = new Usuarios();
                    
                    usr.setNombre(rs.getString("nombre"));
                    usr.setUsuario(rs.getString("usuario"));
                    usr.setContraseña(rs.getString("password")); // Se recupera en texto plano
                    
                    // Tratamiento de los apellidos (reconstruimos paterno y materno)
                    String apellidosCompletos = rs.getString("apellido");
                    if (apellidosCompletos != null && !apellidosCompletos.trim().isEmpty()) {
                        String[] partes = apellidosCompletos.split(" ", 2);
                        if (partes.length >= 2) {
                            usr.setApellidoPaterno(partes[0]);
                            usr.setApellidoMaterno(partes[1]);
                        } else {
                            usr.setApellidoPaterno(partes[0]);
                            usr.setApellidoMaterno("");
                        }
                    }
                    
                    // Tratamiento del rol para el atributo booleano Permisos
                    String tipoUsuario = rs.getString("tipo_usuario");
                    usr.setPermisos("gerente".equalsIgnoreCase(tipoUsuario));
                    
                    return usr; // Retornamos el objeto usuario con sus datos completos de sesión
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al autenticar usuario en la BD: " + e.getMessage());
        }
        return null;
    }
    
}
