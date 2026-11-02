package com.happyfeet.repository;

import com.happyfeet.model.entities.Dueno;

import java.util.Optional;

public interface DuenoRepository extends Repository<Dueno> {

    Optional<Dueno> buscarPorDocumento(String documento);

    Optional<Dueno> buscarPorEmail(String email);
}
