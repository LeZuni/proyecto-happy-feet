package com.happyfeet.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Una mascota vacunada durante una jornada. Los nombres solo se llenan al consultar. */
public record RegistroVacunacion(
        Integer id,
        int jornadaId,
        int mascotaId,
        String mascotaNombre,
        int duenoId,
        int vacunaId,
        String vacunaNombre,
        Integer veterinarioId,
        LocalDateTime fechaHora,
        String lote,
        LocalDate proximaDosis,
        String observaciones) {
}
