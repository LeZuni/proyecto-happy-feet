package com.happyfeet.model.entities;

import java.time.LocalDate;

/** Una fila del historial medico de una mascota (vacunas, cirugias, consultas...). */
public record EventoHistorial(
        Integer id,
        int mascotaId,
        LocalDate fecha,
        String tipo,
        String descripcion,
        String diagnostico,
        String tratamiento,
        String veterinarioNombre) {
}
