package com.happyfeet.model.entities;

import com.happyfeet.model.enums.NivelClub;

import java.time.LocalDate;

public record ClubMascotas(
        Integer id,
        int duenoId,
        int puntosAcumulados,
        int puntosCanjeados,
        int puntosDisponibles,
        NivelClub nivel,
        LocalDate fechaInscripcion,
        boolean activo) {
}
