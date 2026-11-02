package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Raza;
import com.happyfeet.model.enums.Sexo;
import com.happyfeet.repository.MascotaRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MascotaRepositoryJdbc extends BaseRepository implements MascotaRepository {

    private static final String SELECT = "SELECT m.id, m.dueno_id, d.nombre_completo AS dueno_nombre, m.nombre, "
            + "m.raza_id, r.nombre AS raza, e.nombre AS especie, m.fecha_nacimiento, m.sexo, m.peso_actual, "
            + "m.microchip, m.tatuaje, m.url_foto, m.alergias, m.condiciones_preexistentes, m.activo "
            + "FROM mascotas m "
            + "JOIN duenos d ON m.dueno_id = d.id "
            + "JOIN razas r ON m.raza_id = r.id "
            + "JOIN especies e ON r.especie_id = e.id ";

    private Mascota mapear(ResultSet rs) throws SQLException {
        return new Mascota(
                rs.getInt("id"),
                rs.getInt("dueno_id"),
                rs.getString("dueno_nombre"),
                rs.getString("nombre"),
                rs.getInt("raza_id"),
                rs.getString("raza"),
                rs.getString("especie"),
                fecha(rs, "fecha_nacimiento"),
                Sexo.desdeEtiqueta(rs.getString("sexo")),
                rs.getBigDecimal("peso_actual"),
                rs.getString("microchip"),
                rs.getString("tatuaje"),
                rs.getString("url_foto"),
                rs.getString("alergias"),
                rs.getString("condiciones_preexistentes"),
                rs.getBoolean("activo"));
    }

    private Raza mapearRaza(ResultSet rs) throws SQLException {
        return new Raza(rs.getInt("id"), rs.getInt("especie_id"), rs.getString("nombre"), rs.getString("especie"));
    }

    @Override
    public Mascota guardar(Mascota m) {
        int id = insertar("INSERT INTO mascotas (dueno_id, nombre, raza_id, fecha_nacimiento, sexo, peso_actual, "
                        + "microchip, tatuaje, url_foto, alergias, condiciones_preexistentes) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                m.duenoId(), m.nombre(), m.razaId(), m.fechaNacimiento(), m.sexo().getEtiqueta(), m.pesoActual(),
                m.microchip(), m.tatuaje(), m.urlFoto(), m.alergias(), m.condicionesPreexistentes());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Mascota> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE m.id = ?", this::mapear, id);
    }

    @Override
    public List<Mascota> listar() {
        return consultar(SELECT + "WHERE m.activo = TRUE ORDER BY m.nombre", this::mapear);
    }

    @Override
    public List<Mascota> listarPorDueno(int duenoId) {
        return consultar(SELECT + "WHERE m.dueno_id = ? AND m.activo = TRUE ORDER BY m.nombre", this::mapear, duenoId);
    }

    @Override
    public Optional<Mascota> buscarPorMicrochip(String microchip) {
        return consultarUno(SELECT + "WHERE m.microchip = ?", this::mapear, microchip);
    }

    @Override
    public void actualizarPeso(int mascotaId, BigDecimal peso) {
        actualizar("UPDATE mascotas SET peso_actual = ? WHERE id = ?", peso, mascotaId);
    }

    @Override
    public void transferir(Connection conexion, int mascotaId, int nuevoDuenoId) throws SQLException {
        actualizar(conexion, "UPDATE mascotas SET dueno_id = ? WHERE id = ?", nuevoDuenoId, mascotaId);
    }

    @Override
    public List<Raza> listarRazas() {
        return consultar("SELECT r.id, r.especie_id, r.nombre, e.nombre AS especie FROM razas r "
                + "JOIN especies e ON r.especie_id = e.id ORDER BY e.nombre, r.nombre", this::mapearRaza);
    }

    @Override
    public Optional<Raza> buscarRaza(int razaId) {
        return consultarUno("SELECT r.id, r.especie_id, r.nombre, e.nombre AS especie FROM razas r "
                + "JOIN especies e ON r.especie_id = e.id WHERE r.id = ?", this::mapearRaza, razaId);
    }
}
