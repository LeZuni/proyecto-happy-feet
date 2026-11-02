package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.model.enums.EstadoJornada;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.JornadaRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Jornadas de vacunacion: registro rapido de mascotas atendidas en la campaña. */
public class JornadaService {

    private final JornadaRepository jornadaRepository;
    private final HistorialMedicoRepository historialRepository;
    private final MascotaService mascotaService;
    private final InventarioService inventarioService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public JornadaService(JornadaRepository jornadaRepository, HistorialMedicoRepository historialRepository,
                          MascotaService mascotaService, InventarioService inventarioService) {
        this.jornadaRepository = jornadaRepository;
        this.historialRepository = historialRepository;
        this.mascotaService = mascotaService;
        this.inventarioService = inventarioService;
    }

    public JornadaVacunacion crear(JornadaVacunacion jornada) {
        if (jornada.nombre() == null || jornada.nombre().isBlank()) {
            throw new ReglaNegocioException("El nombre de la jornada es obligatorio");
        }
        if (jornada.fecha().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de la jornada no puede estar en el pasado");
        }
        if (jornada.horaInicio() != null && jornada.horaFin() != null
                && !jornada.horaFin().isAfter(jornada.horaInicio())) {
            throw new ReglaNegocioException("La hora de fin debe ser despues de la hora de inicio");
        }
        if (jornada.capacidadMaxima() != null && jornada.capacidadMaxima() <= 0) {
            throw new ReglaNegocioException("La capacidad debe ser mayor que cero");
        }
        JornadaVacunacion guardada = jornadaRepository.guardar(jornada);
        logger.info("Jornada de vacunacion creada: " + guardada.nombre());
        return guardada;
    }

    public List<JornadaVacunacion> listar() {
        return jornadaRepository.listar();
    }

    public JornadaVacunacion buscarPorId(int id) {
        return jornadaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe una jornada con id " + id));
    }

    public void cambiarEstado(int jornadaId, EstadoJornada nuevo) {
        JornadaVacunacion jornada = buscarPorId(jornadaId);
        boolean valido = switch (jornada.estado()) {
            case PLANIFICADA -> nuevo == EstadoJornada.EN_CURSO || nuevo == EstadoJornada.CANCELADA;
            case EN_CURSO -> nuevo == EstadoJornada.FINALIZADA || nuevo == EstadoJornada.CANCELADA;
            default -> false;
        };
        if (!valido) {
            throw new ReglaNegocioException("No se puede pasar de " + jornada.estado() + " a " + nuevo);
        }
        jornadaRepository.cambiarEstado(jornadaId, nuevo);
        logger.info("Jornada " + jornadaId + " ahora esta " + nuevo);
    }

    public List<RegistroVacunacion> listarRegistros(int jornadaId) {
        return jornadaRepository.listarRegistros(jornadaId);
    }

    /** Vacunas disponibles para usar (tipo Vacuna, con stock y sin vencer). */
    public List<Producto> vacunasDisponibles() {
        return inventarioService.listarPorTipo("Vacuna").stream()
                .filter(v -> !v.estaVencido() && v.cantidadStock() > 0)
                .toList();
    }

    public RegistroVacunacion registrarVacunacion(int jornadaId, int mascotaId, int vacunaId, Integer veterinarioId,
                                                  LocalDate proximaDosis) {
        JornadaVacunacion jornada = buscarPorId(jornadaId);
        if (jornada.estado() != EstadoJornada.EN_CURSO) {
            throw new ReglaNegocioException("La jornada debe estar En Curso para registrar vacunas");
        }
        List<RegistroVacunacion> registros = jornadaRepository.listarRegistros(jornadaId);
        if (jornada.capacidadMaxima() != null && registros.size() >= jornada.capacidadMaxima()) {
            throw new ReglaNegocioException("La jornada ya alcanzo su capacidad maxima");
        }
        if (registros.stream().anyMatch(r -> r.mascotaId() == mascotaId && r.vacunaId() == vacunaId)) {
            throw new ReglaNegocioException("Esa mascota ya recibio esa vacuna en esta jornada");
        }
        Mascota mascota = mascotaService.buscarPorId(mascotaId);
        Producto vacuna = inventarioService.buscarPorId(vacunaId);
        if (!vacuna.tipoNombre().equals("Vacuna")) {
            throw new ReglaNegocioException("El producto seleccionado no es una vacuna");
        }

        RegistroVacunacion registro = new RegistroVacunacion(null, jornadaId, mascotaId, mascota.nombre(),
                mascota.duenoId(), vacunaId, vacuna.nombre(), veterinarioId, LocalDateTime.now(), vacuna.lote(),
                proximaDosis, null);

        // Se guarda el registro y se descuenta la vacuna (una dosis) en la misma transaccion
        db.enTransaccion(conexion -> {
            inventarioService.descontarPorUso(conexion, vacunaId, 1,
                    "Jornada de vacunacion #" + jornadaId, null, null);
            jornadaRepository.registrarVacunacion(conexion, registro);
            return null;
        });
        logger.info("Vacunada " + mascota.nombre() + " con " + vacuna.nombre() + " en jornada " + jornadaId);

        try {
            historialRepository.registrar(mascotaId, LocalDate.now(), "Vacunación",
                    vacuna.nombre() + " (" + jornada.nombre() + ")", null, null, veterinarioId, null, null);
            inventarioService.verificarAlertas(vacunaId);
        } catch (RuntimeException e) {
            logger.error("No se pudo completar el historial de la vacunacion", e);
        }
        return registro;
    }
}
