package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Adopcion;
import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.model.enums.EstadoAdopcion;
import com.happyfeet.repository.AdopcionRepository;
import com.happyfeet.repository.MascotaRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;
import com.happyfeet.util.Formato;

import java.time.LocalDate;
import java.util.List;

/** Dias de adopcion: registro de mascotas disponibles y contrato en texto. */
public class AdopcionService {

    private final AdopcionRepository adopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final MascotaService mascotaService;
    private final DuenoService duenoService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public AdopcionService(AdopcionRepository adopcionRepository, MascotaRepository mascotaRepository,
                           MascotaService mascotaService, DuenoService duenoService) {
        this.adopcionRepository = adopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.mascotaService = mascotaService;
        this.duenoService = duenoService;
    }

    public MascotaAdopcion registrar(MascotaAdopcion nueva) {
        mascotaService.buscarPorId(nueva.mascotaId());
        boolean yaEnAdopcion = adopcionRepository.listar().stream()
                .anyMatch(a -> a.mascotaId() == nueva.mascotaId()
                        && (a.estado() == EstadoAdopcion.DISPONIBLE || a.estado() == EstadoAdopcion.EN_PROCESO));
        if (yaEnAdopcion) {
            throw new ReglaNegocioException("Esa mascota ya esta registrada para adopcion");
        }
        MascotaAdopcion guardada = adopcionRepository.guardar(nueva);
        logger.info("Mascota " + nueva.mascotaId() + " registrada para adopcion");
        return guardada;
    }

    public List<MascotaAdopcion> listarDisponibles() {
        return adopcionRepository.listarPorEstado(EstadoAdopcion.DISPONIBLE);
    }

    /** Pasa la mascota al adoptante y guarda el contrato, todo en una transaccion. */
    public Adopcion adoptar(int mascotaAdopcionId, int duenoId, String condiciones) {
        MascotaAdopcion registro = adopcionRepository.buscarPorId(mascotaAdopcionId)
                .orElseThrow(() -> new ReglaNegocioException("No existe ese registro de adopcion"));
        if (registro.estado() != EstadoAdopcion.DISPONIBLE && registro.estado() != EstadoAdopcion.EN_PROCESO) {
            throw new ReglaNegocioException("La mascota no esta disponible, estado: " + registro.estado());
        }
        Mascota mascota = mascotaService.buscarPorId(registro.mascotaId());
        Dueno adoptante = duenoService.buscarPorId(duenoId);
        if (mascota.duenoId() == duenoId) {
            throw new ReglaNegocioException("El adoptante ya es el dueño de la mascota");
        }

        LocalDate hoy = LocalDate.now();
        String contrato = construirContrato(mascota, adoptante, hoy, condiciones);
        Adopcion adopcion = new Adopcion(null, mascotaAdopcionId, duenoId, hoy, contrato, condiciones, true,
                hoy.plusDays(30));

        db.enTransaccion(conexion -> {
            adopcionRepository.guardarAdopcion(conexion, adopcion);
            adopcionRepository.actualizarEstado(conexion, mascotaAdopcionId, EstadoAdopcion.ADOPTADA);
            mascotaRepository.transferir(conexion, mascota.id(), duenoId);
            return null;
        });
        logger.info("Adopcion registrada: " + mascota.nombre() + " adoptada por " + adoptante.nombreCompleto());
        return adopcion;
    }

    private String construirContrato(Mascota m, Dueno adoptante, LocalDate fecha, String condiciones) {
        return """
                ============================================================
                        CONTRATO DE ADOPCION - VETERINARIA HAPPY FEET
                ============================================================
                Fecha: %s

                ENTREGA: %s
                ADOPTANTE: %s (Documento: %s)
                Direccion: %s | Telefono: %s

                MASCOTA: %s (%s - %s), sexo %s

                CLAUSULAS:
                1. El adoptante se compromete a darle a la mascota alimento, agua,
                   refugio y atencion veterinaria.
                2. El adoptante no maltratara, abandonara ni vendera a la mascota.
                3. Happy Feet hara un seguimiento a los 30 dias de la adopcion.
                4. Condiciones especiales: %s

                Firma del adoptante: ______________________
                Firma de Happy Feet: ______________________
                """.formatted(Formato.fecha(fecha), m.duenoNombre(), adoptante.nombreCompleto(),
                adoptante.documentoIdentidad(), Formato.texto(adoptante.direccion()),
                Formato.texto(adoptante.telefono()), m.nombre(), m.especieNombre(), m.razaNombre(), m.sexo(),
                Formato.texto(condiciones));
    }
}
