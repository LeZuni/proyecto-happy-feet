package com.happyfeet.service;

import com.happyfeet.exception.ProductoVencidoException;
import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.exception.StockInsuficienteException;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.TipoProducto;
import com.happyfeet.model.enums.TipoAlerta;
import com.happyfeet.model.enums.TipoMovimiento;
import com.happyfeet.repository.ProductoRepository;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;
import com.happyfeet.util.Formato;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Logica del inventario: control de stock, productos vencidos y alertas.
 * Los observers (listeners) se enteran cuando se genera una alerta.
 */
public class InventarioService {

    public static final int DIAS_ALERTA_VENCIMIENTO = 30;
    private static final String USUARIO = "sistema";

    private final ProductoRepository productoRepository;
    private final DatabaseConnection db = DatabaseConnection.getInstancia();
    private final AppLogger logger = AppLogger.getInstancia();
    private final List<AlertaInventarioListener> listeners = new ArrayList<>();

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public void agregarListener(AlertaInventarioListener listener) {
        listeners.add(listener);
    }

    // ---------- Productos ----------

    public List<Producto> listar() {
        return productoRepository.listar();
    }

    public List<Producto> listarPorTipo(String tipo) {
        return productoRepository.listarPorTipo(tipo);
    }

    public List<TipoProducto> listarTipos() {
        return productoRepository.listarTipos();
    }

    public Producto buscarPorId(int id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe un producto con id " + id));
    }

    public Producto registrar(Producto producto) {
        if (producto.nombre() == null || producto.nombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio");
        }
        if (producto.cantidadStock() < 0 || producto.stockMinimo() < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
        if (producto.precioVenta() == null || producto.precioVenta().signum() < 0) {
            throw new ReglaNegocioException("El precio de venta no es valido");
        }
        Producto guardado = productoRepository.guardar(producto);
        logger.info("Producto registrado: " + guardado.nombre() + " (id " + guardado.id() + ")");
        verificarAlertas(guardado.id());
        return guardado;
    }

    /** Entrada de mercancia (reabastecimiento). */
    public void reabastecer(int productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        buscarPorId(productoId);
        db.enTransaccion(conexion -> {
            productoRepository.moverStock(conexion, productoId, cantidad, TipoMovimiento.ENTRADA,
                    "Reabastecimiento", null, null, USUARIO);
            return null;
        });
        logger.info("Reabastecimiento de " + cantidad + " unidades del producto " + productoId);
    }

    // ---------- Reglas para usar productos ----------

    /** Revisa que el producto se pueda usar: activo, no vencido y con stock suficiente. */
    public void validarDisponible(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
        if (!producto.activo()) {
            throw new ReglaNegocioException("El producto " + producto.nombre() + " esta inactivo");
        }
        if (producto.estaVencido()) {
            throw new ProductoVencidoException("No se puede usar " + producto.nombre() + ": vencio el "
                    + Formato.fecha(producto.fechaVencimiento()));
        }
        if (producto.cantidadStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente de " + producto.nombre() + ": hay "
                    + producto.cantidadStock() + " y se piden " + cantidad);
        }
    }

    /**
     * Descuenta del inventario lo que se uso en una consulta, procedimiento, venta o vacunacion.
     * Se llama dentro de una transaccion para que, si algo falla, no se descuente nada.
     */
    public void descontarPorUso(Connection conexion, int productoId, int cantidad, String motivo,
                                Integer consultaId, Integer procedimientoId) throws SQLException {
        Producto producto = buscarPorId(productoId);
        validarDisponible(producto, cantidad);
        productoRepository.moverStock(conexion, productoId, -cantidad, TipoMovimiento.SALIDA, motivo,
                consultaId, procedimientoId, USUARIO);
    }

    // ---------- Alertas ----------

    public List<Producto> productosBajoStock() {
        return productoRepository.listar().stream().filter(Producto::stockBajo).toList();
    }

    public List<Producto> productosPorVencer(int dias) {
        return productoRepository.listar().stream()
                .filter(p -> p.venceEn(dias))
                .sorted(Comparator.comparing(Producto::fechaVencimiento))
                .toList();
    }

    public List<Producto> productosVencidos() {
        return productoRepository.listar().stream().filter(Producto::estaVencido).toList();
    }

    /** Junta todas las alertas actuales del inventario. */
    public List<AlertaInventario> generarAlertas() {
        List<AlertaInventario> alertas = new ArrayList<>();
        productosVencidos().forEach(p -> alertas.add(new AlertaInventario(p, TipoAlerta.VENCIDO)));
        productosPorVencer(DIAS_ALERTA_VENCIMIENTO).forEach(p -> alertas.add(new AlertaInventario(p, TipoAlerta.POR_VENCER)));
        productosBajoStock().forEach(p -> alertas.add(new AlertaInventario(p, TipoAlerta.STOCK_BAJO)));
        return alertas;
    }

    /** Despues de mover stock se revisa el producto y se avisa a los observers si hace falta. */
    public void verificarAlertas(int productoId) {
        Producto producto = buscarPorId(productoId);
        if (producto.stockBajo()) {
            notificar(new AlertaInventario(producto, TipoAlerta.STOCK_BAJO));
        }
        if (producto.venceEn(DIAS_ALERTA_VENCIMIENTO)) {
            notificar(new AlertaInventario(producto, TipoAlerta.POR_VENCER));
        }
    }

    private void notificar(AlertaInventario alerta) {
        listeners.forEach(listener -> listener.alertar(alerta));
    }
}
