package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Adopcion;
import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.model.enums.EstadoAdopcion;
import com.happyfeet.repository.AdopcionRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdopcionRepositoryJdbc extends BaseRepository implements AdopcionRepository {

    private static final String SELECT = "SELECT a.id, a.mascota_id, m.nombre AS mascota, a.fecha_ingreso, "
            + "a.motivo_ingreso, a.estado, a.historia, a.temperamento, a.necesidades_especiales "
            + "FROM mascotas_adopcion a JOIN mascotas m ON a.mascota_id = m.id ";

    private MascotaAdopcion mapear(ResultSet rs) throws SQLException {
        return new MascotaAdopcion(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                rs.getString("mascota"),
                fecha(rs, "fecha_ingreso"),
                rs.getString("motivo_ingreso"),
                EstadoAdopcion.desdeEtiqueta(rs.getString("estado")),
                rs.getString("historia"),
                rs.getString("temperamento"),
                rs.getString("necesidades_especiales"));
    }

    @Override
    public MascotaAdopcion guardar(MascotaAdopcion a) {
        int id = insertar("INSERT INTO mascotas_adopcion (mascota_id, fecha_ingreso, motivo_ingreso, estado, historia, "
                        + "temperamento, necesidades_especiales) VALUES (?, ?, ?, ?, ?, ?, ?)",
                a.mascotaId(), a.fechaIngreso(), a.motivoIngreso(), a.estado().getEtiqueta(), a.historia(),
                a.temperamento(), a.necesidadesEspeciales());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<MascotaAdopcion> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE a.id = ?", this::mapear, id);
    }

    @Override
    public List<MascotaAdopcion> listar() {
        return consultar(SELECT + "ORDER BY a.fecha_ingreso DESC", this::mapear);
    }

    @Override
    public List<MascotaAdopcion> listarPorEstado(EstadoAdopcion estado) {
        return consultar(SELECT + "WHERE a.estado = ? ORDER BY a.fecha_ingreso", this::mapear, estado.getEtiqueta());
    }

    @Override
    public void actualizarEstado(Connection c, int id, EstadoAdopcion estado) throws SQLException {
        actualizar(c, "UPDATE mascotas_adopcion SET estado = ? WHERE id = ?", estado.getEtiqueta(), id);
    }

    @Override
    public int guardarAdopcion(Connection c, Adopcion a) throws SQLException {
        return insertar(c, "INSERT INTO adopciones (mascota_adopcion_id, dueno_id, fecha_adopcion, contrato_texto, "
                        + "condiciones_especiales, seguimiento_requerido, fecha_primer_seguimiento) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                a.mascotaAdopcionId(), a.duenoId(), a.fechaAdopcion(), a.contratoTexto(), a.condicionesEspeciales(),
                a.seguimientoRequerido(), a.fechaPrimerSeguimiento());
    }
}
