package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.model.enums.EstadoCita;
import com.happyfeet.repository.ConsultaRepository;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.PrescripcionRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.time.LocalDate;
import java.util.List;

public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PrescripcionRepository prescripcionRepository;
    private final HistorialMedicoRepository historialRepository;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final CitaService citaService;
    private final InventarioService inventarioService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public ConsultaService(ConsultaRepository consultaRepository, PrescripcionRepository prescripcionRepository,
                           HistorialMedicoRepository historialRepository, MascotaService mascotaService,
                           VeterinarioService veterinarioService, CitaService citaService,
                           InventarioService inventarioService) {
        this.consultaRepository = consultaRepository;
        this.prescripcionRepository = prescripcionRepository;
        this.historialRepository = historialRepository;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.citaService = citaService;
        this.inventarioService = inventarioService;
    }

    /**
     * Registra la consulta y sus prescripciones. Regla clave: cada medicamento recetado
     * se descuenta automaticamente del inventario. Todo va en una sola transaccion,
     * asi que si falta stock o el producto esta vencido no se guarda nada.
     */
    public ConsultaMedica registrar(ConsultaMedica consulta, List<Prescripcion> prescripciones) {
        if (consulta.motivo() == null || consulta.motivo().isBlank()) {
            throw new ReglaNegocioException("El motivo de la consulta es obligatorio");
        }
        mascotaService.buscarPorId(consulta.mascotaId());
        veterinarioService.buscarPorId(consulta.veterinarioId());
        if (consulta.citaId() != null) {
            Cita cita = citaService.buscarPorId(consulta.citaId());
            if (cita.mascotaId() != consulta.mascotaId()) {
                throw new ReglaNegocioException("La cita no corresponde a esa mascota");
            }
            if (cita.estado().estaCerrada()) {
                throw new ReglaNegocioException("La cita ya esta " + cita.estado());
            }
        }
        // Se revisa antes de guardar para avisar rapido si algo no se puede usar
        prescripciones.forEach(p -> inventarioService.validarDisponible(
                inventarioService.buscarPorId(p.productoId()), p.cantidad()));

        ConsultaMedica guardada = db.enTransaccion(conexion -> {
            int id = consultaRepository.guardarEnTransaccion(conexion, consulta);
            for (Prescripcion p : prescripciones) {
                inventarioService.descontarPorUso(conexion, p.productoId(), p.cantidad(),
                        "Prescripcion en consulta #" + id, id, null);
                prescripcionRepository.guardarEnTransaccion(conexion, p.paraConsulta(id));
            }
            return consulta.conId(id);
        });
        logger.info("Consulta registrada id " + guardada.id() + " de la mascota " + guardada.mascotaId()
                + " con " + prescripciones.size() + " prescripcion(es)");

        // Lo que sigue es secundario: si algo falla se anota en el log pero la consulta ya quedo guardada
        ejecutarSeguro("historial medico", () -> historialRepository.registrar(guardada.mascotaId(), LocalDate.now(),
                "Consulta General", guardada.motivo(), guardada.diagnostico(), guardada.recomendaciones(),
                guardada.veterinarioId(), guardada.id(), null));
        if (guardada.pesoRegistrado() != null) {
            ejecutarSeguro("peso de la mascota",
                    () -> mascotaService.actualizarPeso(guardada.mascotaId(), guardada.pesoRegistrado()));
        }
        if (guardada.citaId() != null) {
            ejecutarSeguro("estado de la cita",
                    () -> citaService.cambiarEstado(guardada.citaId(), EstadoCita.FINALIZADA));
        }
        prescripciones.forEach(p -> ejecutarSeguro("alertas de inventario",
                () -> inventarioService.verificarAlertas(p.productoId())));
        return guardada;
    }

    public List<ConsultaMedica> listar() {
        return consultaRepository.listar();
    }

    public List<ConsultaMedica> listarPorMascota(int mascotaId) {
        return consultaRepository.listarPorMascota(mascotaId);
    }

    public ConsultaMedica buscarPorId(int id) {
        return consultaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe una consulta con id " + id));
    }

    private void ejecutarSeguro(String descripcion, Runnable tarea) {
        try {
            tarea.run();
        } catch (RuntimeException e) {
            logger.error("No se pudo actualizar " + descripcion + " despues de la consulta", e);
        }
    }
}
