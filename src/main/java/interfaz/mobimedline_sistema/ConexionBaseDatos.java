/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.mobimedline_sistema;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author engel
 */
public class ConexionBaseDatos {
    // Almacenamos las propiedades en memoria una sola vez al cargar la clase
    private static final Properties props = new Properties();
    private static String urlNube = null;

    // Bloque estático: Se ejecuta UNA SOLA VEZ cuando el sistema arranca
    static {
        try (InputStream input = ConexionBaseDatos.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println(" CRÍTICO: No se encontró 'config.properties' en los recursos.");
            } else {
                Properties config = new Properties();
                config.load(input);
                
                urlNube = config.getProperty("db.url");
                
                // Configurar credenciales y parámetros de seguridad SSL para Supabase
                props.setProperty("user", config.getProperty("db.user"));
                props.setProperty("password", config.getProperty("db.password"));
                props.setProperty("ssl", "true");
                props.setProperty("sslmode", "require");
                
                System.out.println(" Credenciales de Supabase cargadas en memoria correctamente.");
            }
        } catch (IOException e) {
            System.err.println(" Error al inicializar las configuraciones de la base de datos.");
            e.printStackTrace();
        }
    }

    // El único método que llamará tu código para pedir una conexión limpia
    public static Connection getConexion() throws SQLException {
        if (urlNube == null) {
            throw new SQLException("La URL de la base de datos no está configurada.");
        }
        return DriverManager.getConnection(urlNube, props);
    }
}
