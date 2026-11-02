package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.repository.VeterinarioRepository;
import com.happyfeet.util.AppLogger;

import java.util.List;

public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final AppLogger logger = AppLogger.getInstancia();

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    public Veterinario registrar(Veterinario veterinario) {
        if (veterinario.nombreCompleto() == null || veterinario.nombreCompleto().isBlank()) {
            throw new ReglaNegocioException("El nombre del veterinario es obligatorio");
        }
        veterinarioRepository.buscarPorDocumento(veterinario.documentoIdentidad()).ifPresent(v -> {
            throw new ReglaNegocioException("Ya existe un veterinario con ese documento");
        });
        veterinarioRepository.buscarPorLicencia(veterinario.licenciaProfesional()).ifPresent(v -> {
            throw new ReglaNegocioException("Ya existe un veterinario con esa licencia profesional");
        });
        if (veterinario.email() != null) {
            veterinarioRepository.buscarPorEmail(veterinario.email()).ifPresent(v -> {
                throw new ReglaNegocioException("Ya existe un veterinario con ese correo");
            });
        }
        Veterinario guardado = veterinarioRepository.guardar(veterinario);
        logger.info("Veterinario registrado: " + guardado.nombreCompleto());
        return guardado;
    }

    public List<Veterinario> listar() {
        return veterinarioRepository.listar();
    }

    public Veterinario buscarPorId(int id) {
        return veterinarioRepository.buscarPorId(id)
                .filter(Veterinario::activo)
                .orElseThrow(() -> new ReglaNegocioException("No existe un veterinario activo con id " + id));
    }
}
