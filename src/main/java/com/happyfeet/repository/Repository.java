package com.happyfeet.repository;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones basicas que tienen todos los repositorios.
 * Cada repositorio concreto agrega las consultas que necesita.
 */
public interface Repository<T> {

    T guardar(T entidad);

    Optional<T> buscarPorId(int id);

    List<T> listar();
}
