package com.happyfeet.view;

import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.Servicio;
import com.happyfeet.model.enums.MetodoPago;
import com.happyfeet.service.AlertaInventario;
import com.happyfeet.service.ReporteService.DesempenoVeterinario;
import com.happyfeet.service.ReporteService.ResumenFacturacion;
import com.happyfeet.service.ReporteService.ServicioSolicitado;
import com.happyfeet.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturacionView {

    private final Consola consola;

    public FacturacionView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Facturacion y reportes",
                "Facturar una consulta medica",
                "Facturar un procedimiento especial",
                "Venta directa (productos y servicios)",
                "Ver una factura en texto (y guardarla en un archivo)",
                "Listar facturas",
                "Reporte: servicios mas solicitados",
                "Reporte: desempeño del equipo veterinario",
                "Reporte: estado del inventario",
                "Reporte: facturacion por periodo");
    }

    // ---------- Formularios ----------

    public int pedirIdConsulta() {
        return consola.leerEntero("Id de la consulta a facturar");
    }

    public int pedirIdProcedimiento() {
        return consola.leerEntero("Id del procedimiento a facturar");
    }

    public int pedirIdFactura() {
        return consola.leerEntero("Id de la factura");
    }

    public Servicio elegirServicio(List<Servicio> servicios) {
        consola.mensaje("Elija el servicio a cobrar:");
        return consola.elegirDeLista("Servicio", servicios,
                s -> s.nombre() + " - " + Formato.dinero(s.precioBase()));
    }

    public MetodoPago elegirMetodoPago() {
        consola.mensaje("Metodo de pago:");
        return consola.elegirEnum("Metodo", MetodoPago.class);
    }

    public String pedirObservaciones() {
        return consola.leerTextoOpcional("Observaciones");
    }

    /** Arma las lineas de una venta directa: el usuario agrega productos y servicios uno por uno. */
    public List<ItemFactura> pedirLineasVenta(List<Producto> productos, List<Servicio> servicios) {
        List<ItemFactura> items = new ArrayList<>();
        boolean seguir = true;
        while (seguir) {
            consola.mensaje("1. Agregar producto   2. Agregar servicio   0. Terminar");
            int opcion = consola.leerEnteroEntre("Opcion", 0, 2);
            if (opcion == 1 && !productos.isEmpty()) {
                Producto p = consola.elegirDeLista("Producto", productos,
                        x -> x.nombre() + " - " + Formato.dinero(x.precioVenta()) + " (stock " + x.cantidadStock() + ")");
                int cantidad = consola.leerEnteroEntre("Cantidad", 1, 10000);
                items.add(ItemFactura.deProducto(p.id(), p.nombre(), cantidad, p.precioVenta()));
            } else if (opcion == 2 && !servicios.isEmpty()) {
                Servicio s = consola.elegirDeLista("Servicio", servicios,
                        x -> x.nombre() + " - " + Formato.dinero(x.precioBase()));
                int cantidad = consola.leerEnteroEntre("Cantidad", 1, 100);
                items.add(ItemFactura.deServicio(s.id(), s.nombre(), cantidad, s.precioBase()));
            } else {
                seguir = false;
            }
        }
        return items;
    }

    public LocalDate pedirFechaDesde() {
        return consola.leerFecha("Fecha inicial");
    }

    public LocalDate pedirFechaHasta() {
        return consola.leerFecha("Fecha final");
    }

    // ---------- Salidas ----------

    public void mostrarFacturaTexto(String texto) {
        System.out.println();
        System.out.println(texto);
    }

    public void mostrarFacturas(List<Factura> facturas) {
        consola.subtitulo("Facturas");
        if (facturas.isEmpty()) {
            consola.aviso("No hay facturas.");
            return;
        }
        System.out.printf("%-4s %-16s %-17s %-24s %-16s %-10s%n", "ID", "Numero", "Fecha", "Cliente", "Total", "Estado");
        facturas.forEach(f -> System.out.printf("%-4d %-16s %-17s %-24s %-16s %-10s%n", f.id(), f.numero(),
                Formato.fechaHora(f.fechaEmision()), Formato.cortar(f.duenoNombre(), 24), Formato.dinero(f.total()),
                f.estado()));
    }

    public void mostrarServiciosMasSolicitados(List<ServicioSolicitado> lista) {
        consola.titulo("Servicios mas solicitados");
        if (lista.isEmpty()) {
            consola.aviso("Todavia no hay servicios facturados.");
            return;
        }
        int puesto = 1;
        for (ServicioSolicitado s : lista) {
            System.out.printf("%2d. %-30s %4d veces   %s%n", puesto++, s.nombre(), s.cantidad(),
                    Formato.dinero(s.ingresos()));
        }
    }

    public void mostrarDesempeno(List<DesempenoVeterinario> lista) {
        consola.titulo("Desempeño del equipo veterinario");
        System.out.printf("%-30s %10s %15s %8s%n", "Veterinario", "Consultas", "Procedimientos", "Total");
        lista.forEach(d -> System.out.printf("%-30s %10d %15d %8d%n", Formato.cortar(d.nombre(), 30), d.consultas(),
                d.procedimientos(), d.total()));
    }

    public void mostrarEstadoInventario(List<AlertaInventario> alertas) {
        consola.titulo("Estado del inventario");
        if (alertas.isEmpty()) {
            consola.exito("No hay productos por vencer ni con stock bajo.");
            return;
        }
        System.out.println("Productos vencidos o por vencer:");
        alertas.stream().filter(a -> a.tipo() != com.happyfeet.model.enums.TipoAlerta.STOCK_BAJO)
                .forEach(a -> System.out.println(" * " + a.mensaje()));
        System.out.println("\nProductos que necesitan reabastecerse:");
        alertas.stream().filter(a -> a.tipo() == com.happyfeet.model.enums.TipoAlerta.STOCK_BAJO)
                .forEach(a -> System.out.println(" * " + a.mensaje()));
    }

    public void mostrarResumenFacturacion(LocalDate desde, LocalDate hasta, ResumenFacturacion r) {
        consola.titulo("Facturacion del " + Formato.fecha(desde) + " al " + Formato.fecha(hasta));
        System.out.println("Facturas emitidas: " + r.cantidadFacturas());
        System.out.println("Subtotal:          " + Formato.dinero(r.subtotal()));
        System.out.println("Descuentos:        " + Formato.dinero(r.descuentos()));
        System.out.println("Impuestos (IVA):   " + Formato.dinero(r.impuestos()));
        System.out.println("TOTAL FACTURADO:   " + Formato.dinero(r.total()));
        System.out.println("\nTotal por metodo de pago:");
        r.totalPorMetodo().forEach((metodo, total) -> System.out.println(" * " + metodo + ": " + Formato.dinero(total)));
    }
}
