package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.enums.EstadoProcedimiento;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.PrescripcionRepository;
import com.happyfeet.repository.ProcedimientoRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.time.LocalDate;
import java.util.List;

/** Cirugias y tratamientos complejos (preoperatorio, detalle y seguimiento). */
public class ProcedimientoService {

    private static final List<String> TIPOS_CON_EVENTO = List.of("Cirugía", "Limpieza Dental", "Radiografía",
            "Examen de Sangre");

    private final ProcedimientoRepository procedimientoRepository;
    private final PrescripcionRepository prescripcionRepository;
    private final HistorialMedicoRepository historialRepository;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final InventarioService inventarioService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public ProcedimientoService(ProcedimientoRepository procedimientoRepository,
                                PrescripcionRepository prescripcionRepository,
                                HistorialMedicoRepository historialRepository, MascotaService mascotaService,
                                VeterinarioService veterinarioService, InventarioService inventarioService) {
        this.procedimientoRepository = procedimientoRepository;
        this.prescripcionRepository = prescripcionRepository;
        this.historialRepository = historialRepository;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.inventarioService = inventarioService;
    }

    public ProcedimientoEspecial registrar(ProcedimientoEspecial procedimiento, List<InsumoProcedimiento> insumos,
                                           List<Prescripcion> prescripciones) {
        if (procedimiento.nombreProcedimiento() == null || procedimiento.nombreProcedimiento().isBlank()) {
            throw new ReglaNegocioException("El nombre del procedimiento es obligatorio");
        }
        mascotaService.buscarPorId(procedimiento.mascotaId());
        veterinarioService.buscarPorId(procedimiento.veterinarioId());

        boolean usaInventario = !insumos.isEmpty() || !prescripciones.isEmpty();
        boolean seRealiza = procedimiento.estado() == EstadoProcedimiento.EN_PROCESO
                || procedimiento.estado() == EstadoProcedimiento.FINALIZADO;
        if (usaInventario && !seRealiza) {
            throw new ReglaNegocioException("Solo se descuentan insumos de procedimientos En Proceso o Finalizados");
        }
        insumos.forEach(i -> inventarioService.validarDisponible(inventarioService.buscarPorId(i.productoId()),
                i.cantidad()));
        prescripciones.forEach(p -> inventarioService.validarDisponible(inventarioService.buscarPorId(p.productoId()),
                p.cantidad()));

        ProcedimientoEspecial guardado = db.enTransaccion(conexion -> {
            int id = procedimientoRepository.guardarEnTransaccion(conexion, procedimiento);
            for (InsumoProcedimiento insumo : insumos) {
                inventarioService.descontarPorUso(conexion, insumo.productoId(), insumo.cantidad(),
                        "Insumo usado en procedimiento #" + id, null, id);
                procedimientoRepository.guardarInsumo(conexion, id, insumo);
            }
            for (Prescripcion p : prescripciones) {
                inventarioService.descontarPorUso(conexion, p.productoId(), p.cantidad(),
                        "Prescripcion en procedimiento #" + id, null, id);
                prescripcionRepository.guardarEnTransaccion(conexion, p.paraProcedimiento(id));
            }
            return procedimiento.conId(id);
        });
        logger.info("Procedimiento registrado id " + guardado.id() + ": " + guardado.nombreProcedimiento());

        if (guardado.estado() == EstadoProcedimiento.FINALIZADO) {
            String tipoEvento = TIPOS_CON_EVENTO.contains(guardado.tipoProcedimiento())
                    ? guardado.tipoProcedimiento() : "Cirugía";
            try {
                historialRepository.registrar(guardado.mascotaId(), LocalDate.now(), tipoEvento,
                        guardado.nombreProcedimiento(), null, guardado.seguimientoPostoperatorio(),
                        guardado.veterinarioId(), null, guardado.id());
            } catch (RuntimeException e) {
                logger.error("No se pudo guardar el procedimiento en el historial", e);
            }
        }
        insumos.forEach(i -> inventarioService.verificarAlertas(i.productoId()));
        prescripciones.forEach(p -> inventarioService.verificarAlertas(p.productoId()));
        return guardado;
    }

    /** Actualiza el seguimiento postoperatorio de un procedimiento ya registrado. */
    public void actualizarSeguimiento(int procedimientoId, EstadoProcedimiento estado, String complicaciones,
                                      String seguimiento, LocalDate proximoControl) {
        buscarPorId(procedimientoId);
        if (proximoControl != null && proximoControl.isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("El proximo control no puede estar en el pasado");
        }
        procedimientoRepository.actualizarSeguimiento(procedimientoId, estado, complicaciones, seguimiento,
                proximoControl);
        logger.info("Seguimiento actualizado del procedimiento " + procedimientoId);
    }

    public ProcedimientoEspecial buscarPorId(int id) {
        return procedimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe un procedimiento con id " + id));
    }

    public List<ProcedimientoEspecial> listar() {
        return procedimientoRepository.listar();
    }

    public List<InsumoProcedimiento> listarInsumos(int procedimientoId) {
        return procedimientoRepository.listarInsumos(procedimientoId);
    }
}
