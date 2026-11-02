package com.happyfeet.repository.jdbc;

import com.happyfeet.exception.AccesoDatosException;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase con los metodos de JDBC que se repiten en todos los repositorios
 * (preparar la consulta, asignar parametros, leer resultados y cerrar recursos).
 */
public abstract class BaseRepository {

    protected final DatabaseConnection db = DatabaseConnection.getInstancia();
    protected final AppLogger logger = AppLogger.getInstancia();

    /** Convierte una fila del ResultSet en un objeto. Se usa con lambdas o referencias a metodos. */
    @FunctionalInterface
    protected interface RowMapper<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    // ---------- Consultas (SELECT) ----------

    protected <T> List<T> consultar(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection c = db.getConexion()) {
            return consultar(c, sql, mapper, params);
        } catch (SQLException e) {
            throw error(sql, e);
        }
    }

    protected <T> List<T> consultar(Connection c, String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapper.mapear(rs));
                }
                return lista;
            }
        }
    }

    protected <T> Optional<T> consultarUno(String sql, RowMapper<T> mapper, Object... params) {
        return consultar(sql, mapper, params).stream().findFirst();
    }

    // ---------- INSERT / UPDATE / DELETE ----------

    protected int actualizar(String sql, Object... params) {
        try (Connection c = db.getConexion()) {
            return actualizar(c, sql, params);
        } catch (SQLException e) {
            throw error(sql, e);
        }
    }

    protected int actualizar(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            return ps.executeUpdate();
        }
    }

    /** Hace un INSERT y devuelve el id que genero MySQL (AUTO_INCREMENT). */
    protected int insertar(String sql, Object... params) {
        try (Connection c = db.getConexion()) {
            return insertar(c, sql, params);
        } catch (SQLException e) {
            throw error(sql, e);
        }
    }

    protected int insertar(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    // ---------- Ayudas ----------

    private void asignar(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object valor = params[i];
            int pos = i + 1;
            if (valor == null) {
                ps.setNull(pos, Types.NULL);
            } else if (valor instanceof LocalDate fecha) {
                ps.setDate(pos, Date.valueOf(fecha));
            } else if (valor instanceof LocalDateTime fechaHora) {
                ps.setTimestamp(pos, Timestamp.valueOf(fechaHora));
            } else if (valor instanceof LocalTime hora) {
                ps.setTime(pos, Time.valueOf(hora));
            } else {
                ps.setObject(pos, valor);
            }
        }
    }

    private AccesoDatosException error(String sql, SQLException e) {
        logger.error("Error de base de datos ejecutando: " + sql, e);
        return new AccesoDatosException("Error de base de datos: " + e.getMessage(), e);
    }

    protected static LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        Date d = rs.getDate(columna);
        return d == null ? null : d.toLocalDate();
    }

    protected static LocalDateTime fechaHora(ResultSet rs, String columna) throws SQLException {
        Timestamp t = rs.getTimestamp(columna);
        return t == null ? null : t.toLocalDateTime();
    }

    protected static LocalTime hora(ResultSet rs, String columna) throws SQLException {
        Time t = rs.getTime(columna);
        return t == null ? null : t.toLocalTime();
    }

    /** Lee un INT que puede ser NULL en la base de datos. */
    protected static Integer entero(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }
}
