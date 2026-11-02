package com.happyfeet.model.entities;

import com.happyfeet.model.enums.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public record Mascota(
        Integer id,
        int duenoId,
        String duenoNombre,
        String nombre,
        int razaId,
        String razaNombre,
        String especieNombre,
        LocalDate fechaNacimiento,
        Sexo sexo,
        BigDecimal pesoActual,
        String microchip,
        String tatuaje,
        String urlFoto,
        String alergias,
        String condicionesPreexistentes,
        boolean activo) {

    /** Calcula la edad en años y meses a partir de la fecha de nacimiento. */
    public String edadTexto() {
        if (fechaNacimiento == null) {
            return "No registrada";
        }
        Period periodo = Period.between(fechaNacimiento, LocalDate.now());
        return periodo.getYears() + " años, " + periodo.getMonths() + " meses";
    }
}
