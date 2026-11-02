package com.happyfeet.repository;

import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.enums.EstadoProcedimiento;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ProcedimientoRepository extends Repository<ProcedimientoEspecial> {

    int guardarEnTransaccion(Connection conexion, ProcedimientoEspecial procedimiento) throws SQLException;

    void guardarInsumo(Connection conexion, int procedimientoId, InsumoProcedimiento insumo) throws SQLException;

    List<InsumoProcedimiento> listarInsumos(int procedimientoId);

    List<ProcedimientoEspecial> listarPorMascota(int mascotaId);

    void actualizarSeguimiento(int procedimientoId, EstadoProcedimiento estado, String complicaciones,
                               String seguimiento, LocalDate proximoControl);
}
