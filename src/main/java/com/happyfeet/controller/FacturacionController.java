package com.happyfeet.controller;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.enums.MetodoPago;
import com.happyfeet.service.ConsultaService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.FacturacionService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.service.ProcedimientoService;
import com.happyfeet.service.ReporteService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.FacturacionView;
import com.happyfeet.view.PacienteView;
import com.happyfeet.view.ServiciosMedicosView;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Controlador del Modulo 4: facturacion y reportes. */
public class FacturacionController extends ControladorBase {

    private final FacturacionService facturacionService;
    private final ReporteService reporteService;
    private final ConsultaService consultaService;
    private final ProcedimientoService procedimientoService;
    private final InventarioService inventarioService;
    private final DuenoService duenoService;
    private final FacturacionView view;
    private final ServiciosMedicosView serviciosView;
    private final PacienteView pacienteView;

    public FacturacionController(Consola consola, FacturacionService facturacionService,
                                 ReporteService reporteService, ConsultaService consultaService,
                                 ProcedimientoService procedimientoService, InventarioService inventarioService,
                                 DuenoService duenoService, FacturacionView view,
                                 ServiciosMedicosView serviciosView, PacienteView pacienteView) {
        super(consola);
        this.facturacionService = facturacionService;
        this.reporteService = reporteService;
        this.consultaService = consultaService;
        this.procedimientoService = procedimientoService;
        this.inventarioService = inventarioService;
        this.duenoService = duenoService;
        this.view = view;
        this.serviciosView = serviciosView;
        this.pacienteView = pacienteView;
    }

    @Override
    protected int mostrarMenu() {
        return view.menu();
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> facturarConsulta();
            case 2 -> facturarProcedimiento();
            case 3 -> ventaDirecta();
            case 4 -> verFactura();
            case 5 -> view.mostrarFacturas(facturacionService.listarFacturas());
            case 6 -> view.mostrarServiciosMasSolicitados(reporteService.serviciosMasSolicitados(10));
            case 7 -> view.mostrarDesempeno(reporteService.desempenoVeterinarios());
            case 8 -> view.mostrarEstadoInventario(reporteService.estadoInventario());
            case 9 -> reportePorPeriodo();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void facturarConsulta() {
        serviciosView.mostrarConsultas(consultaService.listar());
        int consultaId = view.pedirIdConsulta();
        var servicio = view.elegirServicio(facturacionService.listarServicios());
        MetodoPago metodo = view.elegirMetodoPago();
        mostrarYGuardar(facturacionService.facturarConsulta(consultaId, servicio.id(), metodo));
    }

    private void facturarProcedimiento() {
        serviciosView.mostrarProcedimientos(procedimientoService.listar());
        int procedimientoId = view.pedirIdProcedimiento();
        var servicio = view.elegirServicio(facturacionService.listarServicios());
        MetodoPago metodo = view.elegirMetodoPago();
        mostrarYGuardar(facturacionService.facturarProcedimiento(procedimientoId, servicio.id(), metodo));
    }

    private void ventaDirecta() {
        pacienteView.mostrarDuenos(duenoService.listar());
        int duenoId = pacienteView.pedirIdDueno();
        duenoService.buscarPorId(duenoId);
        List<ItemFactura> items = view.pedirLineasVenta(inventarioService.listar().stream()
                .filter(p -> !p.estaVencido() && p.cantidadStock() > 0).toList(),
                facturacionService.listarServicios());
        if (items.isEmpty()) {
            throw new ReglaNegocioException("No se agrego ningun item a la venta");
        }
        MetodoPago metodo = view.elegirMetodoPago();
        mostrarYGuardar(facturacionService.facturarVenta(duenoId, items, metodo, view.pedirObservaciones()));
    }

    private void verFactura() {
        view.mostrarFacturas(facturacionService.listarFacturas());
        int id = view.pedirIdFactura();
        view.mostrarFacturaTexto(facturacionService.generarTexto(id));
        Path archivo = facturacionService.exportarTexto(id);
        consola.exito("Factura guardada en " + archivo);
    }

    private void reportePorPeriodo() {
        LocalDate desde = view.pedirFechaDesde();
        LocalDate hasta = view.pedirFechaHasta();
        if (hasta.isBefore(desde)) {
            throw new ReglaNegocioException("La fecha final no puede ser antes de la inicial");
        }
        view.mostrarResumenFacturacion(desde, hasta, reporteService.facturacionPorPeriodo(desde, hasta));
    }

    private void mostrarYGuardar(Factura factura) {
        view.mostrarFacturaTexto(facturacionService.generarTexto(factura.id()));
        Path archivo = facturacionService.exportarTexto(factura.id());
        consola.exito("Factura " + factura.numero() + " emitida y guardada en " + archivo);
    }
}
