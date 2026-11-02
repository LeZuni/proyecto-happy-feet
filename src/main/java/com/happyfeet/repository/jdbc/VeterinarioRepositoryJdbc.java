package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.repository.VeterinarioRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VeterinarioRepositoryJdbc extends BaseRepository implements VeterinarioRepository {

    private static final String SELECT = "SELECT id, nombre_completo, documento_identidad, licencia_profesional, "
            + "especialidad, telefono, email, activo FROM veterinarios ";

    private Veterinario mapear(ResultSet rs) throws SQLException {
        return new Veterinario(
                rs.getInt("id"),
                rs.getString("nombre_completo"),
                rs.getString("documento_identidad"),
                rs.getString("licencia_profesional"),
                rs.getString("especialidad"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getBoolean("activo"));
    }

    @Override
    public Veterinario guardar(Veterinario v) {
        int id = insertar("INSERT INTO veterinarios (nombre_completo, documento_identidad, licencia_profesional, "
                        + "especialidad, telefono, email, fecha_contratacion) VALUES (?, ?, ?, ?, ?, ?, CURDATE())",
                v.nombreCompleto(), v.documentoIdentidad(), v.licenciaProfesional(), v.especialidad(), v.telefono(),
                v.email());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Veterinario> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Veterinario> listar() {
        return consultar(SELECT + "WHERE activo = TRUE ORDER BY nombre_completo", this::mapear);
    }

    @Override
    public Optional<Veterinario> buscarPorDocumento(String documento) {
        return consultarUno(SELECT + "WHERE documento_identidad = ?", this::mapear, documento);
    }

    @Override
    public Optional<Veterinario> buscarPorLicencia(String licencia) {
        return consultarUno(SELECT + "WHERE licencia_profesional = ?", this::mapear, licencia);
    }

    @Override
    public Optional<Veterinario> buscarPorEmail(String email) {
        return consultarUno(SELECT + "WHERE email = ?", this::mapear, email);
    }
}
