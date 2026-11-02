package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.EventoHistorial;
import com.happyfeet.repository.HistorialMedicoRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistorialMedicoRepositoryJdbc extends BaseRepository implements HistorialMedicoRepository {

    private EventoHistorial mapear(ResultSet rs) throws SQLException {
        return new EventoHistorial(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                fecha(rs, "fecha_evento"),
                rs.getString("tipo"),
                rs.getString("descripcion"),
                rs.getString("diagnostico"),
                rs.getString("tratamiento_recomendado"),
                rs.getString("veterinario"));
    }

    @Override
    public void registrar(int mascotaId, LocalDate fecha, String tipoEvento, String descripcion, String diagnostico,
                          String tratamiento, Integer veterinarioId, Integer consultaId, Integer procedimientoId) {
        actualizar("INSERT INTO historial_medico (mascota_id, fecha_evento, evento_tipo_id, descripcion, "
                        + "diagnostico, tratamiento_recomendado, veterinario_id, consulta_id, procedimiento_id) "
                        + "VALUES (?, ?, (SELECT id FROM evento_tipos WHERE nombre = ?), ?, ?, ?, ?, ?, ?)",
                mascotaId, fecha, tipoEvento, descripcion, diagnostico, tratamiento, veterinarioId, consultaId,
                procedimientoId);
    }

    @Override
    public List<EventoHistorial> listarPorMascota(int mascotaId) {
        return consultar("SELECT h.id, h.mascota_id, h.fecha_evento, t.nombre AS tipo, h.descripcion, h.diagnostico, "
                + "h.tratamiento_recomendado, v.nombre_completo AS veterinario "
                + "FROM historial_medico h "
                + "JOIN evento_tipos t ON h.evento_tipo_id = t.id "
                + "LEFT JOIN veterinarios v ON h.veterinario_id = v.id "
                + "WHERE h.mascota_id = ? ORDER BY h.fecha_evento DESC, h.id DESC", this::mapear, mascotaId);
    }
}
