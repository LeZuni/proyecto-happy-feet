package com.happyfeet.model.entities;

import com.happyfeet.model.enums.EstadoProcedimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProcedimientoEspecial(
        Integer id,
        int mascotaId,
        int veterinarioId,
        String tipoProcedimiento,
        String nombreProcedimiento,
        LocalDateTime fechaHora,
        Integer duracionEstimadaMinutos,
        String informacionPreoperatoria,
        String detalleProcedimiento,
        String complicaciones,
        String seguimientoPostoperatorio,
        LocalDate proximoControl,
        EstadoProcedimiento estado,
        BigDecimal costo) {

    public ProcedimientoEspecial conId(int nuevoId) {
        return new ProcedimientoEspecial(nuevoId, mascotaId, veterinarioId, tipoProcedimiento, nombreProcedimiento,
                fechaHora, duracionEstimadaMinutos, informacionPreoperatoria, detalleProcedimiento, complicaciones,
                seguimientoPostoperatorio, proximoControl, estado, costo);
    }
}
