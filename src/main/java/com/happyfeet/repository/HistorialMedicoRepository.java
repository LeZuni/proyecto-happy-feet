package com.happyfeet.repository;

import com.happyfeet.model.entities.EventoHistorial;

import java.time.LocalDate;
import java.util.List;

public interface HistorialMedicoRepository {

    void registrar(int mascotaId, LocalDate fecha, String tipoEvento, String descripcion, String diagnostico,
                   String tratamiento, Integer veterinarioId, Integer consultaId, Integer procedimientoId);

    List<EventoHistorial> listarPorMascota(int mascotaId);
}
