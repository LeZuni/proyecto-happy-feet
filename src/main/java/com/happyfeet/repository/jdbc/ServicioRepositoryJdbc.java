package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Servicio;
import com.happyfeet.repository.ServicioRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ServicioRepositoryJdbc extends BaseRepository implements ServicioRepository {

    private static final String SELECT = "SELECT id, nombre, descripcion, categoria, precio_base, activo "
            + "FROM servicios ";

    private Servicio mapear(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio_base"),
                rs.getBoolean("activo"));
    }

    @Override
    public Servicio guardar(Servicio s) {
        int id = insertar("INSERT INTO servicios (nombre, descripcion, categoria, precio_base) VALUES (?, ?, ?, ?)",
                s.nombre(), s.descripcion(), s.categoria(), s.precioBase());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Servicio> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Servicio> listar() {
        return consultar(SELECT + "WHERE activo = TRUE ORDER BY categoria, nombre", this::mapear);
    }
}
