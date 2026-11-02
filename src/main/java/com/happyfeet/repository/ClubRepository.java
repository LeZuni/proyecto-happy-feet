package com.happyfeet.repository;

import com.happyfeet.model.entities.BeneficioClub;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.model.enums.TipoPuntos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClubRepository extends Repository<ClubMascotas> {

    Optional<ClubMascotas> buscarPorDueno(int duenoId);

    /**
     * Suma o resta puntos (positivo o negativo), actualiza el nivel y deja la
     * transaccion de puntos registrada. Se usa dentro de una transaccion.
     */
    void registrarMovimiento(Connection conexion, int clubId, int puntos, TipoPuntos tipo, Integer facturaId,
                             String descripcion) throws SQLException;

    List<BeneficioClub> listarBeneficios();

    Optional<BeneficioClub> buscarBeneficio(int beneficioId);

    void guardarCanje(Connection conexion, int clubId, BeneficioClub beneficio) throws SQLException;
}
