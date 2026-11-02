package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.repository.ConsultaRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ConsultaRepositoryJdbc extends BaseRepository implements ConsultaRepository {

    private static final String SELECT = "SELECT id, mascota_id, veterinario_id, cita_id, fecha_hora, motivo, "
            + "sintomas, diagnostico, recomendaciones, observaciones, peso_registrado, temperatura "
            + "FROM consultas_medicas ";

    private ConsultaMedica mapear(ResultSet rs) throws SQLException {
        return new ConsultaMedica(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                rs.getInt("veterinario_id"),
                entero(rs, "cita_id"),
                fechaHora(rs, "fecha_hora"),
                rs.getString("motivo"),
                rs.getString("sintomas"),
                rs.getString("diagnostico"),
                rs.getString("recomendaciones"),
                rs.getString("observaciones"),
                rs.getBigDecimal("peso_registrado"),
                rs.getBigDecimal("temperatura"));
    }

    @Override
    public int guardarEnTransaccion(Connection c, ConsultaMedica consulta) throws SQLException {
        return insertar(c, "INSERT INTO consultas_medicas (mascota_id, veterinario_id, cita_id, fecha_hora, motivo, "
                        + "sintomas, diagnostico, recomendaciones, observaciones, peso_registrado, temperatura) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                consulta.mascotaId(), consulta.veterinarioId(), consulta.citaId(), consulta.fechaHora(),
                consulta.motivo(), consulta.sintomas(), consulta.diagnostico(), consulta.recomendaciones(),
                consulta.observaciones(), consulta.pesoRegistrado(), consulta.temperatura());
    }

    @Override
    public ConsultaMedica guardar(ConsultaMedica consulta) {
        int id = DatabaseConnection.getInstancia().enTransaccion(c -> guardarEnTransaccion(c, consulta));
        return consulta.conId(id);
    }

    @Override
    public Optional<ConsultaMedica> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<ConsultaMedica> listar() {
        return consultar(SELECT + "ORDER BY fecha_hora DESC", this::mapear);
    }

    @Override
    public List<ConsultaMedica> listarPorMascota(int mascotaId) {
        return consultar(SELECT + "WHERE mascota_id = ? ORDER BY fecha_hora DESC", this::mapear, mascotaId);
    }
}
