package com.happyfeet.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Bloque de codigo que se ejecuta dentro de una transaccion.
 */
@FunctionalInterface
public interface TrabajoTransaccional<T> {

    T ejecutar(Connection conexion) throws SQLException;
}
