package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Dueno;
import com.happyfeet.repository.DuenoRepository;
import com.happyfeet.util.AppLogger;

import java.util.List;

public class DuenoService {

    private final DuenoRepository duenoRepository;
    private final AppLogger logger = AppLogger.getInstancia();

    public DuenoService(DuenoRepository duenoRepository) {
        this.duenoRepository = duenoRepository;
    }

    public Dueno registrar(Dueno dueno) {
        if (dueno.nombreCompleto() == null || dueno.nombreCompleto().isBlank()) {
            throw new ReglaNegocioException("El nombre del dueño es obligatorio");
        }
        if (dueno.documentoIdentidad() == null || dueno.documentoIdentidad().isBlank()) {
            throw new ReglaNegocioException("El documento de identidad es obligatorio");
        }
        if (dueno.email() == null || !dueno.email().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ReglaNegocioException("El correo electronico no es valido");
        }
        duenoRepository.buscarPorDocumento(dueno.documentoIdentidad()).ifPresent(existente -> {
            throw new ReglaNegocioException("Ya existe un dueño con ese documento: " + existente.nombreCompleto());
        });
        duenoRepository.buscarPorEmail(dueno.email()).ifPresent(existente -> {
            throw new ReglaNegocioException("Ya existe un dueño con ese correo: " + existente.nombreCompleto());
        });

        Dueno guardado = duenoRepository.guardar(dueno);
        logger.info("Dueño registrado: " + guardado.nombreCompleto() + " (id " + guardado.id() + ")");
        return guardado;
    }

    public List<Dueno> listar() {
        return duenoRepository.listar();
    }

    public Dueno buscarPorId(int id) {
        return duenoRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe un dueño con id " + id));
    }

    public Dueno buscarPorDocumento(String documento) {
        return duenoRepository.buscarPorDocumento(documento)
                .orElseThrow(() -> new ReglaNegocioException("No existe un dueño con documento " + documento));
    }
}
