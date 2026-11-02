package com.happyfeet.repository;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.model.enums.EstadoJornada;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface JornadaRepository extends Repository<JornadaVacunacion> {

    void cambiarEstado(int jornadaId, EstadoJornada estado);

    void registrarVacunacion(Connection conexion, RegistroVacunacion registro) throws SQLException;

    List<RegistroVacunacion> listarRegistros(int jornadaId);
}
