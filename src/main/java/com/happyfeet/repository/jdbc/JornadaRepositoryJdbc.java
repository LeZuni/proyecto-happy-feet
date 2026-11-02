package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.model.enums.EstadoJornada;
import com.happyfeet.repository.JornadaRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JornadaRepositoryJdbc extends BaseRepository implements JornadaRepository {

    private static final String SELECT = "SELECT id, nombre, fecha, hora_inicio, hora_fin, ubicacion, descripcion, "
            + "capacidad_maxima, estado FROM jornadas_vacunacion ";

    private JornadaVacunacion mapear(ResultSet rs) throws SQLException {
        return new JornadaVacunacion(
                rs.getInt("id"),
                rs.getString("nombre"),
                fecha(rs, "fecha"),
                hora(rs, "hora_inicio"),
                hora(rs, "hora_fin"),
                rs.getString("ubicacion"),
                rs.getString("descripcion"),
                entero(rs, "capacidad_maxima"),
                EstadoJornada.desdeEtiqueta(rs.getString("estado")));
    }

    private RegistroVacunacion mapearRegistro(ResultSet rs) throws SQLException {
        return new RegistroVacunacion(
                rs.getInt("id"),
                rs.getInt("jornada_id"),
                rs.getInt("mascota_id"),
                rs.getString("mascota"),
                rs.getInt("dueno_id"),
                rs.getInt("vacuna_id"),
                rs.getString("vacuna"),
                entero(rs, "veterinario_id"),
                fechaHora(rs, "fecha_hora"),
                rs.getString("lote_vacuna"),
                fecha(rs, "proxima_dosis"),
                rs.getString("observaciones"));
    }

    @Override
    public JornadaVacunacion guardar(JornadaVacunacion j) {
        int id = insertar("INSERT INTO jornadas_vacunacion (nombre, fecha, hora_inicio, hora_fin, ubicacion, "
                        + "descripcion, capacidad_maxima, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                j.nombre(), j.fecha(), j.horaInicio(), j.horaFin(), j.ubicacion(), j.descripcion(),
                j.capacidadMaxima(), j.estado().getEtiqueta());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<JornadaVacunacion> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<JornadaVacunacion> listar() {
        return consultar(SELECT + "ORDER BY fecha DESC", this::mapear);
    }

    @Override
    public void cambiarEstado(int jornadaId, EstadoJornada estado) {
        actualizar("UPDATE jornadas_vacunacion SET estado = ? WHERE id = ?", estado.getEtiqueta(), jornadaId);
    }

    @Override
    public void registrarVacunacion(Connection c, RegistroVacunacion r) throws SQLException {
        insertar(c, "INSERT INTO registro_jornada_vacunacion (jornada_id, mascota_id, dueno_id, vacuna_id, "
                        + "veterinario_id, fecha_hora, lote_vacuna, proxima_dosis, observaciones) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                r.jornadaId(), r.mascotaId(), r.duenoId(), r.vacunaId(), r.veterinarioId(), r.fechaHora(),
                r.lote(), r.proximaDosis(), r.observaciones());
    }

    @Override
    public List<RegistroVacunacion> listarRegistros(int jornadaId) {
        return consultar("SELECT r.id, r.jornada_id, r.mascota_id, m.nombre AS mascota, r.dueno_id, r.vacuna_id, "
                + "i.nombre_producto AS vacuna, r.veterinario_id, r.fecha_hora, r.lote_vacuna, r.proxima_dosis, "
                + "r.observaciones FROM registro_jornada_vacunacion r "
                + "JOIN mascotas m ON r.mascota_id = m.id "
                + "JOIN inventario i ON r.vacuna_id = i.id "
                + "WHERE r.jornada_id = ? ORDER BY r.fecha_hora", this::mapearRegistro, jornadaId);
    }
}
