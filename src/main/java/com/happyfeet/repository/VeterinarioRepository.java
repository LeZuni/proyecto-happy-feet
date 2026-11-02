package com.happyfeet.repository;

import com.happyfeet.model.entities.Veterinario;

import java.util.Optional;

public interface VeterinarioRepository extends Repository<Veterinario> {

    Optional<Veterinario> buscarPorDocumento(String documento);

    Optional<Veterinario> buscarPorLicencia(String licencia);

    Optional<Veterinario> buscarPorEmail(String email);
}
