package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.repository.PrescripcionRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PrescripcionRepositoryJdbc extends BaseRepository implements PrescripcionRepository {

    private static final String SELECT = "SELECT id, consulta_id, procedimiento_id, producto_id, cantidad, dosis, "
            + "frecuencia, duracion_dias, instrucciones FROM prescripciones ";

    private Prescripcion mapear(ResultSet rs) throws SQLException {
        return new Prescripcion(
                rs.getInt("id"),
                entero(rs, "consulta_id"),
                entero(rs, "procedimiento_id"),
                rs.getInt("producto_id"),
                rs.getInt("cantidad"),
                rs.getString("dosis"),
                rs.getString("frecuencia"),
                entero(rs, "duracion_dias"),
                rs.getString("instrucciones"));
    }

    @Override
    public void guardarEnTransaccion(Connection c, Prescripcion p) throws SQLException {
        insertar(c, "INSERT INTO prescripciones (consulta_id, procedimiento_id, producto_id, cantidad, dosis, "
                        + "frecuencia, duracion_dias, instrucciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                p.consultaId(), p.procedimientoId(), p.productoId(), p.cantidad(), p.dosis(), p.frecuencia(),
                p.duracionDias(), p.instrucciones());
    }

    @Override
    public List<Prescripcion> listarPorConsulta(int consultaId) {
        return consultar(SELECT + "WHERE consulta_id = ?", this::mapear, consultaId);
    }

    @Override
    public List<Prescripcion> listarPorProcedimiento(int procedimientoId) {
        return consultar(SELECT + "WHERE procedimiento_id = ?", this::mapear, procedimientoId);
    }
}
