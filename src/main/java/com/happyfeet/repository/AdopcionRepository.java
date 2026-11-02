package com.happyfeet.repository;

import com.happyfeet.model.entities.Adopcion;
import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.model.enums.EstadoAdopcion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AdopcionRepository extends Repository<MascotaAdopcion> {

    List<MascotaAdopcion> listarPorEstado(EstadoAdopcion estado);

    void actualizarEstado(Connection conexion, int mascotaAdopcionId, EstadoAdopcion estado) throws SQLException;

    int guardarAdopcion(Connection conexion, Adopcion adopcion) throws SQLException;
}
