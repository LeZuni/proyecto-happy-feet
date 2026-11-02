package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.repository.ProveedorRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProveedorRepositoryJdbc extends BaseRepository implements ProveedorRepository {

    private static final String SELECT = "SELECT id, nombre_empresa, contacto, telefono, email, direccion, activo "
            + "FROM proveedores ";

    private Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt("id"),
                rs.getString("nombre_empresa"),
                rs.getString("contacto"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getBoolean("activo"));
    }

    @Override
    public Proveedor guardar(Proveedor p) {
        int id = insertar("INSERT INTO proveedores (nombre_empresa, contacto, telefono, email, direccion) "
                        + "VALUES (?, ?, ?, ?, ?)",
                p.nombreEmpresa(), p.contacto(), p.telefono(), p.email(), p.direccion());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Proveedor> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Proveedor> listar() {
        return consultar(SELECT + "WHERE activo = TRUE ORDER BY nombre_empresa", this::mapear);
    }
}
