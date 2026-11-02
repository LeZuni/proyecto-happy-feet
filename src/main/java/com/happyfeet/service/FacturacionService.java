package com.happyfeet.service;

import com.happyfeet.exception.HappyFeetException;
import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.Servicio;
import com.happyfeet.model.enums.MetodoPago;
import com.happyfeet.model.enums.TipoItem;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.repository.PrescripcionRepository;
import com.happyfeet.repository.ServicioRepository;
import com.happyfeet.service.descuento.DescuentoStrategy;
import com.happyfeet.service.descuento.DescuentoStrategyFactory;
import com.happyfeet.service.descuento.SinDescuento;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;
import com.happyfeet.util.FacturaTexto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Facturacion: arma las facturas, descuenta inventario en ventas y suma puntos del club. */
public class FacturacionService {

    private static final Path CARPETA_FACTURAS = Paths.get("facturas");

    private final FacturaRepository facturaRepository;
    private final ServicioRepository servicioRepository;
    private final PrescripcionRepository prescripcionRepository;
    private final DuenoService duenoService;
    private final MascotaService mascotaService;
    private final ConsultaService consultaService;
    private final ProcedimientoService procedimientoService;
    private final InventarioService inventarioService;
    private final ClubService clubService;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();

    public FacturacionService(FacturaRepository facturaRepository, ServicioRepository servicioRepository,
                              PrescripcionRepository prescripcionRepository, DuenoService duenoService,
                              MascotaService mascotaService, ConsultaService consultaService,
                              ProcedimientoService procedimientoService, InventarioService inventarioService,
                              ClubService clubService) {
        this.facturaRepository = facturaRepository;
        this.servicioRepository = servicioRepository;
        this.prescripcionRepository = prescripcionRepository;
        this.duenoService = duenoService;
        this.mascotaService = mascotaService;
        this.consultaService = consultaService;
        this.procedimientoService = procedimientoService;
        this.inventarioService = inventarioService;
        this.clubService = clubService;
    }

    public List<Servicio> listarServicios() {
        return servicioRepository.listar();
    }

    public List<Factura> listarFacturas() {
        return facturaRepository.listar();
    }

    public Factura buscarPorId(int id) {
        return facturaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe una factura con id " + id));
    }

    // ---------- Formas de facturar ----------

    /** Factura una consulta: el servicio elegido mas los medicamentos que se recetaron. */
    public Factura facturarConsulta(int consultaId, int servicioId, MetodoPago metodo) {
        ConsultaMedica consulta = consultaService.buscarPorId(consultaId);
        Mascota mascota = mascotaService.buscarPorId(consulta.mascotaId());
        Servicio servicio = buscarServicio(servicioId);

        List<ItemFactura> items = new ArrayList<>();
        items.add(ItemFactura.deServicio(servicio.id(), servicio.nombre(), 1, servicio.precioBase()));
        for (Prescripcion p : prescripcionRepository.listarPorConsulta(consultaId)) {
            items.add(itemDeProducto(p.productoId(), p.cantidad()));
        }
        // El inventario ya se descontó cuando se registró la consulta, por eso no se vuelve a descontar
        return emitir(mascota.duenoId(), items, false, metodo,
                "Consulta #" + consultaId + " - mascota " + mascota.nombre());
    }

    /** Factura un procedimiento: su costo mas los insumos y medicamentos usados. */
    public Factura facturarProcedimiento(int procedimientoId, int servicioId, MetodoPago metodo) {
        ProcedimientoEspecial procedimiento = procedimientoService.buscarPorId(procedimientoId);
        Mascota mascota = mascotaService.buscarPorId(procedimiento.mascotaId());
        Servicio servicio = buscarServicio(servicioId);

        List<ItemFactura> items = new ArrayList<>();
        var precio = procedimiento.costo() != null ? procedimiento.costo() : servicio.precioBase();
        items.add(ItemFactura.deServicio(servicio.id(), servicio.nombre(), 1, precio));
        for (InsumoProcedimiento insumo : procedimientoService.listarInsumos(procedimientoId)) {
            items.add(itemDeProducto(insumo.productoId(), insumo.cantidad()));
        }
        for (Prescripcion p : prescripcionRepository.listarPorProcedimiento(procedimientoId)) {
            items.add(itemDeProducto(p.productoId(), p.cantidad()));
        }
        return emitir(mascota.duenoId(), items, false, metodo,
                "Procedimiento #" + procedimientoId + " (" + procedimiento.nombreProcedimiento() + ") - mascota "
                        + mascota.nombre());
    }

    /** Venta directa de productos y/o servicios. Los productos si descuentan inventario. */
    public Factura facturarVenta(int duenoId, List<ItemFactura> items, MetodoPago metodo, String observaciones) {
        return emitir(duenoId, items, true, metodo, observaciones);
    }

    public ItemFactura itemDeProducto(int productoId, int cantidad) {
        Producto p = inventarioService.buscarPorId(productoId);
        return ItemFactura.deProducto(p.id(), p.nombre(), cantidad, p.precioVenta());
    }

    public ItemFactura itemDeServicio(int servicioId, int cantidad) {
        Servicio s = buscarServicio(servicioId);
        return ItemFactura.deServicio(s.id(), s.nombre(), cantidad, s.precioBase());
    }

    // ---------- Texto de la factura ----------

    public String generarTexto(int facturaId) {
        Factura factura = buscarPorId(facturaId);
        Dueno cliente = duenoService.buscarPorId(factura.duenoId());
        return FacturaTexto.generar(factura, cliente);
    }

    /** Guarda la factura como archivo .txt dentro de la carpeta facturas/. */
    public Path exportarTexto(int facturaId) {
        Factura factura = buscarPorId(facturaId);
        try {
            Files.createDirectories(CARPETA_FACTURAS);
            Path archivo = CARPETA_FACTURAS.resolve(factura.numero() + ".txt");
            Files.writeString(archivo, generarTexto(facturaId), StandardCharsets.UTF_8);
            return archivo;
        } catch (IOException e) {
            throw new HappyFeetException("No se pudo guardar el archivo de la factura", e);
        }
    }

    // ---------- Logica comun ----------

    private Factura emitir(int duenoId, List<ItemFactura> items, boolean descontarInventario, MetodoPago metodo,
                           String observaciones) {
        Dueno dueno = duenoService.buscarPorId(duenoId);
        Optional<ClubMascotas> club = clubService.buscarActivoPorDueno(duenoId);
        // Strategy: el descuento depende del nivel del cliente en el club
        DescuentoStrategy descuento = club.map(c -> DescuentoStrategyFactory.para(c.nivel()))
                .orElseGet(SinDescuento::new);

        FacturaBuilder builder = new FacturaBuilder()
                .paraDueno(dueno.id(), dueno.nombreCompleto())
                .conNumero(facturaRepository.siguienteNumero())
                .conMetodoPago(metodo)
                .conObservaciones(observaciones)
                .conDescuento(descuento);
        items.forEach(builder::agregarItem);
        Factura factura = builder.construir();

        if (descontarInventario) {
            factura.items().stream().filter(i -> i.tipo() == TipoItem.PRODUCTO).forEach(i ->
                    inventarioService.validarDisponible(inventarioService.buscarPorId(i.productoId()), i.cantidad()));
        }

        Factura guardada = db.enTransaccion(conexion -> {
            Factura f = facturaRepository.guardarEnTransaccion(conexion, factura);
            if (descontarInventario) {
                for (ItemFactura item : f.items()) {
                    if (item.tipo() == TipoItem.PRODUCTO) {
                        inventarioService.descontarPorUso(conexion, item.productoId(), item.cantidad(),
                                "Venta factura " + f.numero(), null, null);
                    }
                }
            }
            if (club.isPresent()) {
                clubService.acumularPorFactura(conexion, club.get(), f);
            }
            return f;
        });
        logger.info("Factura emitida " + guardada.numero() + " por " + guardada.total() + " (" + descuento.descripcion() + ")");

        if (descontarInventario) {
            guardada.items().stream().filter(i -> i.tipo() == TipoItem.PRODUCTO)
                    .forEach(i -> inventarioService.verificarAlertas(i.productoId()));
        }
        return buscarPorId(guardada.id());
    }

    private Servicio buscarServicio(int servicioId) {
        return servicioRepository.buscarPorId(servicioId).filter(Servicio::activo)
                .orElseThrow(() -> new ReglaNegocioException("No existe un servicio activo con id " + servicioId));
    }
}
