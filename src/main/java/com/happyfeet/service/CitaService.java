package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.enums.EstadoCita;
import com.happyfeet.repository.CitaRepository;
import com.happyfeet.util.AppLogger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** Agenda de citas: evita que se solapen y controla los cambios de estado. */
public class CitaService {

    private static final int DURACION_CITA_MINUTOS = 30;
    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(18, 0);

    private final CitaRepository citaRepository;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final AppLogger logger = AppLogger.getInstancia();

    public CitaService(CitaRepository citaRepository, MascotaService mascotaService,
                       VeterinarioService veterinarioService) {
        this.citaRepository = citaRepository;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
    }

    public Cita agendar(int mascotaId, int veterinarioId, LocalDateTime fechaHora, String motivo) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se puede agendar una cita en el pasado");
        }
        LocalTime hora = fechaHora.toLocalTime();
        if (hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE.minusMinutes(DURACION_CITA_MINUTOS))) {
            throw new ReglaNegocioException("La clinica atiende de 08:00 a 18:00");
        }
        mascotaService.buscarPorId(mascotaId);
        veterinarioService.buscarPorId(veterinarioId);

        int solapadas = citaRepository.contarSolapadas(veterinarioId, mascotaId,
                fechaHora.minusMinutes(DURACION_CITA_MINUTOS), fechaHora.plusMinutes(DURACION_CITA_MINUTOS));
        if (solapadas > 0) {
            throw new ReglaNegocioException("Ese horario se cruza con otra cita del veterinario o de la mascota");
        }

        Cita cita = new Cita(null, mascotaId, null, veterinarioId, null, fechaHora, motivo, EstadoCita.PROGRAMADA, null);
        Cita guardada = citaRepository.guardar(cita);
        logger.info("Cita agendada id " + guardada.id() + " para la mascota " + mascotaId);
        return guardada;
    }

    public List<Cita> listar() {
        return citaRepository.listar();
    }

    public List<Cita> listarPorFecha(LocalDate fecha) {
        return citaRepository.listarPorFecha(fecha);
    }

    public Cita buscarPorId(int id) {
        return citaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe una cita con id " + id));
    }

    public void cambiarEstado(int citaId, EstadoCita nuevoEstado) {
        Cita cita = buscarPorId(citaId);
        if (cita.estado().estaCerrada()) {
            throw new ReglaNegocioException("La cita ya esta " + cita.estado() + " y no se puede modificar");
        }
        if (cita.estado() == nuevoEstado) {
            throw new ReglaNegocioException("La cita ya tiene ese estado");
        }
        citaRepository.cambiarEstado(citaId, nuevoEstado);
        logger.info("Cita " + citaId + " cambio de " + cita.estado() + " a " + nuevoEstado);
    }
}
