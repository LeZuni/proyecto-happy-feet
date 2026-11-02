package com.happyfeet.repository;

import com.happyfeet.model.entities.ConsultaMedica;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ConsultaRepository extends Repository<ConsultaMedica> {

    /** Guarda la consulta dentro de una transaccion y devuelve el id generado. */
    int guardarEnTransaccion(Connection conexion, ConsultaMedica consulta) throws SQLException;

    List<ConsultaMedica> listarPorMascota(int mascotaId);
}
