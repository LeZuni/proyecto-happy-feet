package com.happyfeet.model.entities;

import com.happyfeet.model.enums.EstadoCita;

import java.time.LocalDateTime;

public record Cita(
        Integer id,
        int mascotaId,
        String mascotaNombre,
        Integer veterinarioId,
        String veterinarioNombre,
        LocalDateTime fechaHora,
        String motivo,
        EstadoCita estado,
        String observaciones) {
}
