package com.happyfeet.repository;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Raza;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends Repository<Mascota> {

    List<Mascota> listarPorDueno(int duenoId);

    Optional<Mascota> buscarPorMicrochip(String microchip);

    void actualizarPeso(int mascotaId, BigDecimal peso);

    /** Cambia el dueño. Recibe la conexion porque se usa dentro de una transaccion. */
    void transferir(Connection conexion, int mascotaId, int nuevoDuenoId) throws SQLException;

    List<Raza> listarRazas();

    Optional<Raza> buscarRaza(int razaId);
}
