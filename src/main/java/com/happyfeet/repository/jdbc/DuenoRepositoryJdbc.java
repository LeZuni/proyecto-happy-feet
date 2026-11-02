package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.repository.DuenoRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DuenoRepositoryJdbc extends BaseRepository implements DuenoRepository {

    private static final String SELECT = "SELECT id, nombre_completo, documento_identidad, direccion, telefono, "
            + "email, contacto_emergencia, activo FROM duenos ";

    private Dueno mapear(ResultSet rs) throws SQLException {
        return new Dueno(
                rs.getInt("id"),
                rs.getString("nombre_completo"),
                rs.getString("documento_identidad"),
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("contacto_emergencia"),
                rs.getBoolean("activo"));
    }

    @Override
    public Dueno guardar(Dueno d) {
        int id = insertar("INSERT INTO duenos (nombre_completo, documento_identidad, direccion, telefono, email, "
                        + "contacto_emergencia) VALUES (?, ?, ?, ?, ?, ?)",
                d.nombreCompleto(), d.documentoIdentidad(), d.direccion(), d.telefono(), d.email(),
                d.contactoEmergencia());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Dueno> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Dueno> listar() {
        return consultar(SELECT + "WHERE activo = TRUE ORDER BY nombre_completo", this::mapear);
    }

    @Override
    public Optional<Dueno> buscarPorDocumento(String documento) {
        return consultarUno(SELECT + "WHERE documento_identidad = ?", this::mapear, documento);
    }

    @Override
    public Optional<Dueno> buscarPorEmail(String email) {
        return consultarUno(SELECT + "WHERE email = ?", this::mapear, email);
    }
}
