package com.happyfeet.repository;

import com.happyfeet.model.entities.Factura;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaRepository extends Repository<Factura> {

    /** Guarda la factura y sus items dentro de una transaccion. Devuelve la factura con su id. */
    Factura guardarEnTransaccion(Connection conexion, Factura factura) throws SQLException;

    /** Genera el siguiente numero, por ejemplo FAC-2026-000007. */
    String siguienteNumero();

    List<Factura> listarPorPeriodo(LocalDateTime desde, LocalDateTime hasta);
}
