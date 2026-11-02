package com.happyfeet.repository;

import com.happyfeet.model.entities.Prescripcion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PrescripcionRepository {

    void guardarEnTransaccion(Connection conexion, Prescripcion prescripcion) throws SQLException;

    List<Prescripcion> listarPorConsulta(int consultaId);

    List<Prescripcion> listarPorProcedimiento(int procedimientoId);
}
