package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.EventoHistorial;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Raza;
import com.happyfeet.repository.DuenoRepository;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.MascotaRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;
    private final HistorialMedicoRepository historialRepository;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public MascotaService(MascotaRepository mascotaRepository, DuenoRepository duenoRepository,
                          HistorialMedicoRepository historialRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
        this.historialRepository = historialRepository;
    }

    public Mascota registrar(Mascota mascota) {
        if (mascota.nombre() == null || mascota.nombre().isBlank()) {
            throw new ReglaNegocioException("El nombre de la mascota es obligatorio");
        }
        // Cada mascota debe tener un unico dueño registrado y activo
        duenoRepository.buscarPorId(mascota.duenoId()).filter(Dueno::activo)
                .orElseThrow(() -> new ReglaNegocioException("El dueño no existe o esta inactivo"));
        mascotaRepository.buscarRaza(mascota.razaId())
                .orElseThrow(() -> new ReglaNegocioException("La raza seleccionada no existe"));

        if (mascota.fechaNacimiento() != null && mascota.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura");
        }
        if (mascota.pesoActual() != null && mascota.pesoActual().signum() <= 0) {
            throw new ReglaNegocioException("El peso debe ser mayor que cero");
        }
        if (mascota.microchip() != null) {
            mascotaRepository.buscarPorMicrochip(mascota.microchip()).ifPresent(existente -> {
                throw new ReglaNegocioException("Ya hay una mascota con ese microchip: " + existente.nombre());
            });
        }

        Mascota guardada = mascotaRepository.guardar(mascota);
        logger.info("Mascota registrada: " + guardada.nombre() + " (id " + guardada.id() + ")");
        return guardada;
    }

    public List<Mascota> listar() {
        return mascotaRepository.listar();
    }

    public List<Mascota> listarPorDueno(int duenoId) {
        return mascotaRepository.listarPorDueno(duenoId);
    }

    public Mascota buscarPorId(int id) {
        return mascotaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe una mascota con id " + id));
    }

    public List<Raza> listarRazas() {
        return mascotaRepository.listarRazas();
    }

    public List<EventoHistorial> obtenerHistorial(int mascotaId) {
        return historialRepository.listarPorMascota(mascotaId);
    }

    public void actualizarPeso(int mascotaId, BigDecimal peso) {
        if (peso == null || peso.signum() <= 0) {
            throw new ReglaNegocioException("El peso debe ser mayor que cero");
        }
        buscarPorId(mascotaId);
        mascotaRepository.actualizarPeso(mascotaId, peso);
        logger.info("Peso actualizado de la mascota " + mascotaId + ": " + peso + " kg");
    }

    /** Pasa la mascota a otro dueño (la mascota siempre tiene un solo dueño a la vez). */
    public void transferir(int mascotaId, int nuevoDuenoId) {
        Mascota mascota = buscarPorId(mascotaId);
        Dueno nuevoDueno = duenoRepository.buscarPorId(nuevoDuenoId).filter(Dueno::activo)
                .orElseThrow(() -> new ReglaNegocioException("El nuevo dueño no existe o esta inactivo"));
        if (mascota.duenoId() == nuevoDuenoId) {
            throw new ReglaNegocioException("La mascota ya pertenece a ese dueño");
        }
        db.enTransaccion(conexion -> {
            mascotaRepository.transferir(conexion, mascotaId, nuevoDuenoId);
            return null;
        });
        logger.info("Mascota " + mascota.nombre() + " transferida de dueño " + mascota.duenoId()
                + " a " + nuevoDueno.nombreCompleto());
    }
}
