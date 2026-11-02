package com.happyfeet.model.entities;

import com.happyfeet.model.enums.EstadoJornada;

import java.time.LocalDate;
import java.time.LocalTime;

public record JornadaVacunacion(
        Integer id,
        String nombre,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String ubicacion,
        String descripcion,
        Integer capacidadMaxima,
        EstadoJornada estado) {
}
