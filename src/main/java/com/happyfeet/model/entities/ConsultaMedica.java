package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaMedica(
        Integer id,
        int mascotaId,
        int veterinarioId,
        Integer citaId,
        LocalDateTime fechaHora,
        String motivo,
        String sintomas,
        String diagnostico,
        String recomendaciones,
        String observaciones,
        BigDecimal pesoRegistrado,
        BigDecimal temperatura) {

    /** Devuelve una copia de la consulta con el id que le dio la base de datos. */
    public ConsultaMedica conId(int nuevoId) {
        return new ConsultaMedica(nuevoId, mascotaId, veterinarioId, citaId, fechaHora, motivo, sintomas,
                diagnostico, recomendaciones, observaciones, pesoRegistrado, temperatura);
    }
}
