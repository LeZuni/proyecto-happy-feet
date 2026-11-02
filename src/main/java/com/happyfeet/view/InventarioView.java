package com.happyfeet.view;

import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.model.entities.TipoProducto;
import com.happyfeet.service.AlertaInventario;
import com.happyfeet.service.AlertaInventarioListener;
import com.happyfeet.util.Formato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista del inventario. Tambien es un "observer": cuando el InventarioService
 * genera una alerta, esta vista la muestra en pantalla.
 */
public class InventarioView implements AlertaInventarioListener {

    private final Consola consola;

    public InventarioView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Inventario y farmacia",
                "Ver inventario completo",
                "Registrar producto nuevo",
                "Reabastecer un producto (entrada de stock)",
                "Ver alertas (stock bajo, por vencer, vencidos)",
                "Ver proveedores",
                "Registrar proveedor");
    }

    @Override
    public void alertar(AlertaInventario alerta) {
        consola.aviso("ALERTA DE INVENTARIO - " + alerta.mensaje());
    }

    // ---------- Formularios ----------

    public Producto pedirProducto(List<TipoProducto> tipos, List<Proveedor> proveedores) {
        consola.subtitulo("Datos del producto");
        String nombre = consola.leerTexto("Nombre del producto");
        consola.mensaje("Tipo de producto:");
        TipoProducto tipo = consola.elegirDeLista("Tipo", tipos, TipoProducto::nombre);
        String fabricante = consola.leerTextoOpcional("Fabricante");

        Integer proveedorId = null;
        if (!proveedores.isEmpty() && consola.confirmar("¿Quiere asociar un proveedor?")) {
            proveedorId = consola.elegirDeLista("Proveedor", proveedores, Proveedor::nombreEmpresa).id();
        }

        String lote = consola.leerTextoOpcional("Lote");
        int stock = consola.leerEnteroEntre("Cantidad en stock", 0, 100000);
        int stockMinimo = consola.leerEnteroEntre("Stock minimo para alerta", 0, 100000);
        String unidad = consola.leerTextoOpcional("Unidad de medida (por defecto: unidad)");
        LocalDate vencimiento = consola.leerFechaOpcional("Fecha de vencimiento");
        BigDecimal precioCompra = consola.leerDecimalOpcional("Precio de compra");
        BigDecimal precioVenta = consola.leerDecimal("Precio de venta");
        boolean receta = consola.confirmar("¿Requiere receta?");

        return new Producto(null, nombre, tipo.id(), tipo.nombre(), fabricante, proveedorId, lote, stock,
                stockMinimo, unidad == null ? "unidad" : unidad, vencimiento, precioCompra, precioVenta, receta, true);
    }

    public int pedirIdProducto() {
        return consola.leerEntero("Id del producto");
    }

    public int pedirCantidad() {
        return consola.leerEnteroEntre("Cantidad", 1, 100000);
    }

    public Proveedor pedirProveedor() {
        consola.subtitulo("Datos del proveedor");
        String nombre = consola.leerTexto("Nombre de la empresa");
        String contacto = consola.leerTextoOpcional("Persona de contacto");
        String telefono = consola.leerTextoOpcional("Telefono");
        String email = consola.leerTextoOpcional("Correo");
        String direccion = consola.leerTextoOpcional("Direccion");
        return new Proveedor(null, nombre, contacto, telefono, email, direccion, true);
    }

    // ---------- Listados ----------

    public void mostrarProductos(List<Producto> productos) {
        consola.subtitulo("Inventario");
        if (productos.isEmpty()) {
            consola.aviso("No hay productos para mostrar.");
            return;
        }
        System.out.printf("%-4s %-28s %-14s %-7s %-7s %-12s %-12s%n", "ID", "Producto", "Tipo", "Stock", "Minimo",
                "Vence", "Precio");
        productos.forEach(p -> {
            String marca = p.estaVencido() ? " (VENCIDO)" : p.stockBajo() ? " (bajo)" : "";
            System.out.printf("%-4d %-28s %-14s %-7d %-7d %-12s %-12s%s%n", p.id(), Formato.cortar(p.nombre(), 28),
                    p.tipoNombre(), p.cantidadStock(), p.stockMinimo(), Formato.fecha(p.fechaVencimiento()),
                    Formato.dinero(p.precioVenta()), marca);
        });
    }

    public void mostrarProveedores(List<Proveedor> proveedores) {
        consola.subtitulo("Proveedores");
        if (proveedores.isEmpty()) {
            consola.aviso("No hay proveedores registrados.");
            return;
        }
        System.out.printf("%-4s %-28s %-20s %-14s%n", "ID", "Empresa", "Contacto", "Telefono");
        proveedores.forEach(p -> System.out.printf("%-4d %-28s %-20s %-14s%n", p.id(),
                Formato.cortar(p.nombreEmpresa(), 28), Formato.cortar(p.contacto(), 20), Formato.texto(p.telefono())));
    }

    public void mostrarAlertas(List<AlertaInventario> alertas) {
        consola.subtitulo("Alertas del inventario");
        if (alertas.isEmpty()) {
            consola.exito("No hay alertas. Todo el inventario esta en orden.");
            return;
        }
        alertas.forEach(a -> System.out.println(" * [" + a.tipo() + "] " + a.mensaje()));
    }
}
