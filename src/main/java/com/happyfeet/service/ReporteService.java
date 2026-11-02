package com.happyfeet.service;

import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.enums.EstadoFactura;
import com.happyfeet.model.enums.EstadoProcedimiento;
import com.happyfeet.model.enums.TipoItem;
import com.happyfeet.repository.ConsultaRepository;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.repository.ProcedimientoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Reportes gerenciales. Se calculan con lambdas y la API Stream sobre las listas. */
public class ReporteService {

    public record ServicioSolicitado(String nombre, int cantidad, BigDecimal ingresos) {
    }

    public record DesempenoVeterinario(String nombre, long consultas, long procedimientos) {
        public long total() {
            return consultas + procedimientos;
        }
    }

    public record ResumenFacturacion(int cantidadFacturas, BigDecimal subtotal, BigDecimal descuentos,
                                     BigDecimal impuestos, BigDecimal total, Map<String, BigDecimal> totalPorMetodo) {
    }

    private final FacturaRepository facturaRepository;
    private final ConsultaRepository consultaRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final VeterinarioService veterinarioService;
    private final InventarioService inventarioService;

    public ReporteService(FacturaRepository facturaRepository, ConsultaRepository consultaRepository,
                          ProcedimientoRepository procedimientoRepository, VeterinarioService veterinarioService,
                          InventarioService inventarioService) {
        this.facturaRepository = facturaRepository;
        this.consultaRepository = consultaRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.veterinarioService = veterinarioService;
        this.inventarioService = inventarioService;
    }

    public List<ServicioSolicitado> serviciosMasSolicitados(int limite) {
        Map<String, List<ItemFactura>> porServicio = facturaRepository.listar().stream()
                .filter(f -> f.estado() != EstadoFactura.ANULADA)
                .flatMap(f -> f.items().stream())
                .filter(i -> i.tipo() == TipoItem.SERVICIO)
                .collect(Collectors.groupingBy(ItemFactura::descripcion));

        return porServicio.entrySet().stream()
                .map(e -> new ServicioSolicitado(e.getKey(),
                        e.getValue().stream().mapToInt(ItemFactura::cantidad).sum(),
                        sumar(e.getValue(), ItemFactura::subtotal)))
                .sorted(Comparator.comparingInt(ServicioSolicitado::cantidad).reversed())
                .limit(limite)
                .toList();
    }

    public List<DesempenoVeterinario> desempenoVeterinarios() {
        Map<Integer, Long> consultas = consultaRepository.listar().stream()
                .collect(Collectors.groupingBy(ConsultaMedica::veterinarioId, Collectors.counting()));
        Map<Integer, Long> procedimientos = procedimientoRepository.listar().stream()
                .filter(p -> p.estado() == EstadoProcedimiento.FINALIZADO)
                .collect(Collectors.groupingBy(ProcedimientoEspecial::veterinarioId, Collectors.counting()));

        return veterinarioService.listar().stream()
                .map(v -> new DesempenoVeterinario(v.nombreCompleto(),
                        consultas.getOrDefault(v.id(), 0L), procedimientos.getOrDefault(v.id(), 0L)))
                .sorted(Comparator.comparingLong(DesempenoVeterinario::total).reversed())
                .toList();
    }

    public List<AlertaInventario> estadoInventario() {
        return inventarioService.generarAlertas();
    }

    public ResumenFacturacion facturacionPorPeriodo(LocalDate desde, LocalDate hasta) {
        List<Factura> facturas = facturaRepository.listarPorPeriodo(desde.atStartOfDay(), hasta.atTime(23, 59, 59))
                .stream().filter(f -> f.estado() != EstadoFactura.ANULADA).toList();

        Map<String, BigDecimal> porMetodo = new TreeMap<>(facturas.stream().collect(Collectors.groupingBy(
                f -> f.metodoPago() == null ? "Sin definir" : f.metodoPago().getEtiqueta(),
                Collectors.reducing(BigDecimal.ZERO, Factura::total, BigDecimal::add))));

        return new ResumenFacturacion(facturas.size(), sumar(facturas, Factura::subtotal),
                sumar(facturas, Factura::descuento), sumar(facturas, Factura::impuesto),
                sumar(facturas, Factura::total), porMetodo);
    }

    private <T> BigDecimal sumar(List<T> lista, Function<T, BigDecimal> campo) {
        return lista.stream().map(campo).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
