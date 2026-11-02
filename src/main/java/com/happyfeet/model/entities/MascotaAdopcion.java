package com.happyfeet.model.entities;

import com.happyfeet.model.enums.EstadoAdopcion;

import java.time.LocalDate;

public record MascotaAdopcion(
        Integer id,
        int mascotaId,
        String mascotaNombre,
        LocalDate fechaIngreso,
        String motivoIngreso,
        EstadoAdopcion estado,
        String historia,
        String temperamento,
        String necesidadesEspeciales) {
}
