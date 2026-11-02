package com.happyfeet.util;

import com.happyfeet.exception.AccesoDatosException;
import com.happyfeet.exception.HappyFeetException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Maneja la conexion a MySQL con JDBC (patron Singleton).
 * Los datos de conexion se leen del archivo db.properties.
 */
public class DatabaseConnection {

    private static DatabaseConnection instancia;

    private final String url;
    private final String usuario;
    private final String password;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new HappyFeetException("No se encontro el archivo db.properties en src/main/resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new HappyFeetException("No se pudo leer db.properties", e);
        }
        this.url = props.getProperty("db.url");
        this.usuario = props.getProperty("db.user");
        String passwordEntorno = System.getenv("HAPPYFEET_DB_PASSWORD");
        this.password = passwordEntorno != null ? passwordEntorno : props.getProperty("db.password", "");
    }

    public static synchronized DatabaseConnection getInstancia() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    /** Abre una conexion nueva. Quien la pida debe cerrarla (try-with-resources). */
    public Connection getConexion() {
        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo conectar a la base de datos. Revisa db.properties", e);
        }
    }

    /** Sirve para revisar al iniciar el programa que la base de datos responde. */
    public void probarConexion() {
        try (Connection c = getConexion()) {
            c.isValid(2);
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo probar la conexion", e);
        }
    }

    /**
     * Ejecuta varias operaciones juntas: si todo sale bien hace commit,
     * si algo falla hace rollback y no queda nada a medias.
     */
    public <T> T enTransaccion(TrabajoTransaccional<T> trabajo) {
        try (Connection c = getConexion()) {
            try {
                c.setAutoCommit(false);
                T resultado = trabajo.ejecutar(c);
                c.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new AccesoDatosException("Error en la transaccion: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error cerrando la transaccion: " + e.getMessage(), e);
        }
    }
}
