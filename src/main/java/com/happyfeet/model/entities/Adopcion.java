package com.happyfeet.model.entities;

import java.time.LocalDate;

public record Adopcion(
        Integer id,
        int mascotaAdopcionId,
        int duenoId,
        LocalDate fechaAdopcion,
        String contratoTexto,
        String condicionesEspeciales,
        boolean seguimientoRequerido,
        LocalDate fechaPrimerSeguimiento) {
}
