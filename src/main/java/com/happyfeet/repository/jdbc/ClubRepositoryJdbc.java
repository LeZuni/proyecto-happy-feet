package com.happyfeet.repository.jdbc;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.BeneficioClub;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.model.enums.NivelClub;
import com.happyfeet.model.enums.TipoBeneficio;
import com.happyfeet.model.enums.TipoPuntos;
import com.happyfeet.repository.ClubRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClubRepositoryJdbc extends BaseRepository implements ClubRepository {

    private static final String SELECT = "SELECT id, dueno_id, puntos_acumulados, puntos_canjeados, "
            + "puntos_disponibles, nivel, fecha_inscripcion, activo FROM club_mascotas ";

    private static final String SELECT_BENEFICIO = "SELECT id, nombre, descripcion, nivel_requerido, "
            + "puntos_necesarios, tipo_beneficio, valor_beneficio FROM beneficios_club ";

    private ClubMascotas mapear(ResultSet rs) throws SQLException {
        return new ClubMascotas(
                rs.getInt("id"),
                rs.getInt("dueno_id"),
                rs.getInt("puntos_acumulados"),
                rs.getInt("puntos_canjeados"),
                rs.getInt("puntos_disponibles"),
                NivelClub.desdeEtiqueta(rs.getString("nivel")),
                fecha(rs, "fecha_inscripcion"),
                rs.getBoolean("activo"));
    }

    private BeneficioClub mapearBeneficio(ResultSet rs) throws SQLException {
        return new BeneficioClub(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                NivelClub.desdeEtiqueta(rs.getString("nivel_requerido")),
                rs.getInt("puntos_necesarios"),
                TipoBeneficio.desdeEtiqueta(rs.getString("tipo_beneficio")),
                rs.getBigDecimal("valor_beneficio"));
    }

    @Override
    public ClubMascotas guardar(ClubMascotas club) {
        int id = insertar("INSERT INTO club_mascotas (dueno_id, fecha_inscripcion) VALUES (?, ?)",
                club.duenoId(), club.fechaInscripcion());
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<ClubMascotas> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<ClubMascotas> listar() {
        return consultar(SELECT + "WHERE activo = TRUE ORDER BY puntos_disponibles DESC", this::mapear);
    }

    @Override
    public Optional<ClubMascotas> buscarPorDueno(int duenoId) {
        return consultarUno(SELECT + "WHERE dueno_id = ?", this::mapear, duenoId);
    }

    @Override
    public void registrarMovimiento(Connection c, int clubId, int puntos, TipoPuntos tipo, Integer facturaId,
                                    String descripcion) throws SQLException {
        // FOR UPDATE bloquea la fila del club mientras se calcula el nuevo saldo
        ClubMascotas club = consultar(c, SELECT + "WHERE id = ? FOR UPDATE", this::mapear, clubId)
                .stream().findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El club del dueño no existe"));

        int saldoNuevo = club.puntosDisponibles() + puntos;
        if (saldoNuevo < 0) {
            throw new ReglaNegocioException("Puntos insuficientes: tiene " + club.puntosDisponibles());
        }
        int acumulados = club.puntosAcumulados() + (puntos > 0 ? puntos : 0);
        int canjeados = club.puntosCanjeados() + (tipo == TipoPuntos.CANJEADOS ? Math.abs(puntos) : 0);
        NivelClub nivel = NivelClub.paraPuntos(acumulados);

        actualizar(c, "UPDATE club_mascotas SET puntos_acumulados = ?, puntos_canjeados = ?, "
                + "puntos_disponibles = ?, nivel = ? WHERE id = ?",
                acumulados, canjeados, saldoNuevo, nivel.getEtiqueta(), clubId);
        insertar(c, "INSERT INTO transacciones_puntos (club_mascotas_id, factura_id, puntos, tipo, descripcion, "
                        + "saldo_anterior, saldo_nuevo) VALUES (?, ?, ?, ?, ?, ?, ?)",
                clubId, facturaId, puntos, tipo.getEtiqueta(), descripcion, club.puntosDisponibles(), saldoNuevo);
    }

    @Override
    public List<BeneficioClub> listarBeneficios() {
        return consultar(SELECT_BENEFICIO + "WHERE activo = TRUE ORDER BY puntos_necesarios", this::mapearBeneficio);
    }

    @Override
    public Optional<BeneficioClub> buscarBeneficio(int beneficioId) {
        return consultarUno(SELECT_BENEFICIO + "WHERE id = ?", this::mapearBeneficio, beneficioId);
    }

    @Override
    public void guardarCanje(Connection c, int clubId, BeneficioClub beneficio) throws SQLException {
        insertar(c, "INSERT INTO canjes_beneficios (club_mascotas_id, beneficio_id, puntos_canjeados, estado, "
                        + "fecha_expiracion) VALUES (?, ?, ?, 'Pendiente', DATE_ADD(CURDATE(), INTERVAL 30 DAY))",
                clubId, beneficio.id(), beneficio.puntosNecesarios());
    }
}
