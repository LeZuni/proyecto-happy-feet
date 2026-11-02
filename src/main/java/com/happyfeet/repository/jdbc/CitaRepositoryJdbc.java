package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.enums.EstadoCita;
import com.happyfeet.repository.CitaRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CitaRepositoryJdbc extends BaseRepository implements CitaRepository {

    private static final String SELECT = "SELECT c.id, c.mascota_id, m.nombre AS mascota, c.veterinario_id, "
            + "v.nombre_completo AS veterinario, c.fecha_hora, c.motivo, e.nombre AS estado, c.observaciones "
            + "FROM citas c "
            + "JOIN mascotas m ON c.mascota_id = m.id "
            + "LEFT JOIN veterinarios v ON c.veterinario_id = v.id "
            + "JOIN cita_estados e ON c.estado_id = e.id ";

    private Cita mapear(ResultSet rs) throws SQLException {
        return new Cita(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                rs.getString("mascota"),
                entero(rs, "veterinario_id"),
                rs.getString("veterinario"),
                fechaHora(rs, "fecha_hora"),
                rs.getString("motivo"),
                EstadoCita.desdeEtiqueta(rs.getString("estado")),
                rs.getString("observaciones"));
    }

    @Override
    public Cita guardar(Cita c) {
        int id = insertar("INSERT INTO citas (mascota_id, veterinario_id, fecha_hora, motivo, estado_id, observaciones) "
                        + "VALUES (?, ?, ?, ?, (SELECT id FROM cita_estados WHERE nombre = ?), ?)",
                c.mascotaId(), c.veterinarioId(), c.fechaHora(), c.motivo(), c.estado().getEtiqueta(),
                c.observaciones());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Cita> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE c.id = ?", this::mapear, id);
    }

    @Override
    public List<Cita> listar() {
        return consultar(SELECT + "ORDER BY c.fecha_hora DESC", this::mapear);
    }

    @Override
    public List<Cita> listarPorFecha(LocalDate fecha) {
        return consultar(SELECT + "WHERE DATE(c.fecha_hora) = ? ORDER BY c.fecha_hora", this::mapear, fecha);
    }

    @Override
    public void cambiarEstado(int citaId, EstadoCita estado) {
        actualizar("UPDATE citas SET estado_id = (SELECT id FROM cita_estados WHERE nombre = ?) WHERE id = ?",
                estado.getEtiqueta(), citaId);
    }

    @Override
    public int contarSolapadas(int veterinarioId, int mascotaId, LocalDateTime desde, LocalDateTime hasta) {
        return consultar("SELECT COUNT(*) FROM citas c JOIN cita_estados e ON c.estado_id = e.id "
                        + "WHERE (c.veterinario_id = ? OR c.mascota_id = ?) "
                        + "AND e.nombre NOT IN ('Cancelada', 'No Asistió', 'Finalizada') "
                        + "AND c.fecha_hora > ? AND c.fecha_hora < ?",
                rs -> rs.getInt(1), veterinarioId, mascotaId, desde, hasta).get(0);
    }
}
