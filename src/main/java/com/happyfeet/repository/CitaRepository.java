package com.happyfeet.repository;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends Repository<Cita> {

    List<Cita> listarPorFecha(LocalDate fecha);

    void cambiarEstado(int citaId, EstadoCita estado);

    /** Cuenta las citas abiertas del veterinario o de la mascota que caen entre las dos fechas. */
    int contarSolapadas(int veterinarioId, int mascotaId, LocalDateTime desde, LocalDateTime hasta);
}
