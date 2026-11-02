package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.enums.EstadoProcedimiento;
import com.happyfeet.repository.ProcedimientoRepository;
import com.happyfeet.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProcedimientoRepositoryJdbc extends BaseRepository implements ProcedimientoRepository {

    private static final String SELECT = "SELECT id, mascota_id, veterinario_id, tipo_procedimiento, "
            + "nombre_procedimiento, fecha_hora, duracion_estimada_minutos, informacion_preoperatoria, "
            + "detalle_procedimiento, complicaciones, seguimiento_postoperatorio, proximo_control, estado, "
            + "costo_procedimiento FROM procedimientos_especiales ";

    private ProcedimientoEspecial mapear(ResultSet rs) throws SQLException {
        return new ProcedimientoEspecial(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                rs.getInt("veterinario_id"),
                rs.getString("tipo_procedimiento"),
                rs.getString("nombre_procedimiento"),
                fechaHora(rs, "fecha_hora"),
                entero(rs, "duracion_estimada_minutos"),
                rs.getString("informacion_preoperatoria"),
                rs.getString("detalle_procedimiento"),
                rs.getString("complicaciones"),
                rs.getString("seguimiento_postoperatorio"),
                fecha(rs, "proximo_control"),
                EstadoProcedimiento.desdeEtiqueta(rs.getString("estado")),
                rs.getBigDecimal("costo_procedimiento"));
    }

    @Override
    public int guardarEnTransaccion(Connection c, ProcedimientoEspecial p) throws SQLException {
        return insertar(c, "INSERT INTO procedimientos_especiales (mascota_id, veterinario_id, tipo_procedimiento, "
                        + "nombre_procedimiento, fecha_hora, duracion_estimada_minutos, informacion_preoperatoria, "
                        + "detalle_procedimiento, complicaciones, seguimiento_postoperatorio, proximo_control, estado, "
                        + "costo_procedimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                p.mascotaId(), p.veterinarioId(), p.tipoProcedimiento(), p.nombreProcedimiento(), p.fechaHora(),
                p.duracionEstimadaMinutos(), p.informacionPreoperatoria(), p.detalleProcedimiento(),
                p.complicaciones(), p.seguimientoPostoperatorio(), p.proximoControl(), p.estado().getEtiqueta(),
                p.costo());
    }

    @Override
    public void guardarInsumo(Connection c, int procedimientoId, InsumoProcedimiento insumo) throws SQLException {
        insertar(c, "INSERT INTO insumos_procedimientos (procedimiento_id, producto_id, cantidad_usada, observaciones) "
                        + "VALUES (?, ?, ?, ?)",
                procedimientoId, insumo.productoId(), insumo.cantidad(), insumo.observaciones());
    }

    @Override
    public List<InsumoProcedimiento> listarInsumos(int procedimientoId) {
        return consultar("SELECT producto_id, cantidad_usada, observaciones FROM insumos_procedimientos "
                        + "WHERE procedimiento_id = ?",
                rs -> new InsumoProcedimiento(rs.getInt("producto_id"), rs.getInt("cantidad_usada"),
                        rs.getString("observaciones")), procedimientoId);
    }

    @Override
    public ProcedimientoEspecial guardar(ProcedimientoEspecial p) {
        int id = DatabaseConnection.getInstancia().enTransaccion(c -> guardarEnTransaccion(c, p));
        return p.conId(id);
    }

    @Override
    public Optional<ProcedimientoEspecial> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<ProcedimientoEspecial> listar() {
        return consultar(SELECT + "ORDER BY fecha_hora DESC", this::mapear);
    }

    @Override
    public List<ProcedimientoEspecial> listarPorMascota(int mascotaId) {
        return consultar(SELECT + "WHERE mascota_id = ? ORDER BY fecha_hora DESC", this::mapear, mascotaId);
    }

    @Override
    public void actualizarSeguimiento(int procedimientoId, EstadoProcedimiento estado, String complicaciones,
                                      String seguimiento, LocalDate proximoControl) {
        actualizar("UPDATE procedimientos_especiales SET estado = ?, complicaciones = ?, "
                        + "seguimiento_postoperatorio = ?, proximo_control = ? WHERE id = ?",
                estado.getEtiqueta(), complicaciones, seguimiento, proximoControl, procedimientoId);
    }
}
