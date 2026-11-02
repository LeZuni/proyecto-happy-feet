package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.BeneficioClub;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.enums.TipoPuntos;
import com.happyfeet.repository.ClubRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Club de Mascotas Frecuentes: puntos por compras, niveles y canje de beneficios. */
public class ClubService {

    /** Se gana 1 punto por cada $1.000 pagados. */
    private static final BigDecimal VALOR_POR_PUNTO = BigDecimal.valueOf(1000);

    private final ClubRepository clubRepository;
    private final DuenoService duenoService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public ClubService(ClubRepository clubRepository, DuenoService duenoService) {
        this.clubRepository = clubRepository;
        this.duenoService = duenoService;
    }

    public ClubMascotas inscribir(int duenoId) {
        duenoService.buscarPorId(duenoId);
        if (clubRepository.buscarPorDueno(duenoId).isPresent()) {
            throw new ReglaNegocioException("Ese dueño ya esta inscrito en el club");
        }
        ClubMascotas club = clubRepository.guardar(
                new ClubMascotas(null, duenoId, 0, 0, 0, null, LocalDate.now(), true));
        logger.info("Dueño " + duenoId + " inscrito en el club");
        return club;
    }

    public Optional<ClubMascotas> buscarActivoPorDueno(int duenoId) {
        return clubRepository.buscarPorDueno(duenoId).filter(ClubMascotas::activo);
    }

    public ClubMascotas obtenerPorDueno(int duenoId) {
        return buscarActivoPorDueno(duenoId)
                .orElseThrow(() -> new ReglaNegocioException("Ese dueño no esta inscrito en el club"));
    }

    public List<ClubMascotas> ranking() {
        return clubRepository.listar().stream()
                .sorted((a, b) -> Integer.compare(b.puntosDisponibles(), a.puntosDisponibles()))
                .toList();
    }

    public List<BeneficioClub> listarBeneficios() {
        return clubRepository.listarBeneficios();
    }

    public int calcularPuntos(BigDecimal total) {
        return total.divideToIntegralValue(VALOR_POR_PUNTO).intValue();
    }

    /** Suma los puntos de una factura. Se llama dentro de la transaccion de la factura. */
    public int acumularPorFactura(Connection conexion, ClubMascotas club, Factura factura) throws SQLException {
        int puntos = calcularPuntos(factura.total());
        if (puntos > 0) {
            clubRepository.registrarMovimiento(conexion, club.id(), puntos, TipoPuntos.GANADOS, factura.id(),
                    "Compra factura " + factura.numero());
        }
        return puntos;
    }

    public BeneficioClub canjear(int duenoId, int beneficioId) {
        ClubMascotas club = obtenerPorDueno(duenoId);
        BeneficioClub beneficio = clubRepository.buscarBeneficio(beneficioId)
                .orElseThrow(() -> new ReglaNegocioException("No existe ese beneficio"));
        if (club.nivel().ordinal() < beneficio.nivelRequerido().ordinal()) {
            throw new ReglaNegocioException("Este beneficio requiere nivel " + beneficio.nivelRequerido()
                    + " y el dueño es " + club.nivel());
        }
        if (club.puntosDisponibles() < beneficio.puntosNecesarios()) {
            throw new ReglaNegocioException("Puntos insuficientes: tiene " + club.puntosDisponibles()
                    + " y necesita " + beneficio.puntosNecesarios());
        }
        db.enTransaccion(conexion -> {
            clubRepository.registrarMovimiento(conexion, club.id(), -beneficio.puntosNecesarios(),
                    TipoPuntos.CANJEADOS, null, "Canje: " + beneficio.nombre());
            clubRepository.guardarCanje(conexion, club.id(), beneficio);
            return null;
        });
        logger.info("Dueño " + duenoId + " canjeo el beneficio " + beneficio.nombre());
        return beneficio;
    }
}
