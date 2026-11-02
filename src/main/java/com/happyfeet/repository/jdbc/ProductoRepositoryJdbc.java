package com.happyfeet.repository.jdbc;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.exception.StockInsuficienteException;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.TipoProducto;
import com.happyfeet.model.enums.TipoMovimiento;
import com.happyfeet.repository.ProductoRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoRepositoryJdbc extends BaseRepository implements ProductoRepository {

    private static final String SELECT = "SELECT i.id, i.nombre_producto, i.producto_tipo_id, t.nombre AS tipo, "
            + "i.fabricante, i.proveedor_id, i.lote, i.cantidad_stock, i.stock_minimo, i.unidad_medida, "
            + "i.fecha_vencimiento, i.precio_compra, i.precio_venta, i.requiere_receta, i.activo "
            + "FROM inventario i JOIN producto_tipos t ON i.producto_tipo_id = t.id ";

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre_producto"),
                rs.getInt("producto_tipo_id"),
                rs.getString("tipo"),
                rs.getString("fabricante"),
                entero(rs, "proveedor_id"),
                rs.getString("lote"),
                rs.getInt("cantidad_stock"),
                rs.getInt("stock_minimo"),
                rs.getString("unidad_medida"),
                fecha(rs, "fecha_vencimiento"),
                rs.getBigDecimal("precio_compra"),
                rs.getBigDecimal("precio_venta"),
                rs.getBoolean("requiere_receta"),
                rs.getBoolean("activo"));
    }

    @Override
    public Producto guardar(Producto p) {
        // El producto y su primer movimiento de inventario se guardan juntos
        int id = db.enTransaccion(c -> {
            int nuevoId = insertar(c, "INSERT INTO inventario (nombre_producto, producto_tipo_id, fabricante, "
                            + "proveedor_id, lote, cantidad_stock, stock_minimo, unidad_medida, fecha_vencimiento, "
                            + "precio_compra, precio_venta, requiere_receta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    p.nombre(), p.tipoId(), p.fabricante(), p.proveedorId(), p.lote(), p.cantidadStock(),
                    p.stockMinimo(), p.unidadMedida(), p.fechaVencimiento(), p.precioCompra(), p.precioVenta(),
                    p.requiereReceta());
            if (p.cantidadStock() > 0) {
                insertar(c, "INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, "
                                + "stock_anterior, stock_nuevo, motivo, usuario) VALUES (?, ?, ?, 0, ?, ?, ?)",
                        nuevoId, TipoMovimiento.ENTRADA.getEtiqueta(), p.cantidadStock(), p.cantidadStock(),
                        "Stock inicial", "sistema");
            }
            return nuevoId;
        });
        return buscarPorId(id).orElseThrow();
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE i.id = ?", this::mapear, id);
    }

    @Override
    public List<Producto> listar() {
        return consultar(SELECT + "WHERE i.activo = TRUE ORDER BY i.nombre_producto", this::mapear);
    }

    @Override
    public List<Producto> listarPorTipo(String nombreTipo) {
        return consultar(SELECT + "WHERE i.activo = TRUE AND t.nombre = ? ORDER BY i.nombre_producto",
                this::mapear, nombreTipo);
    }

    @Override
    public List<TipoProducto> listarTipos() {
        return consultar("SELECT id, nombre FROM producto_tipos ORDER BY nombre",
                rs -> new TipoProducto(rs.getInt("id"), rs.getString("nombre")));
    }

    @Override
    public void moverStock(Connection conexion, int productoId, int delta, TipoMovimiento tipo, String motivo,
                           Integer consultaId, Integer procedimientoId, String usuario) throws SQLException {
        // FOR UPDATE bloquea la fila para que dos personas no gasten el mismo stock a la vez
        int anterior = consultar(conexion, "SELECT cantidad_stock FROM inventario WHERE id = ? FOR UPDATE",
                rs -> rs.getInt(1), productoId).stream().findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El producto " + productoId + " no existe"));

        int nuevo = anterior + delta;
        if (nuevo < 0) {
            throw new StockInsuficienteException("Stock insuficiente: hay " + anterior + " y se necesitan "
                    + Math.abs(delta));
        }
        actualizar(conexion, "UPDATE inventario SET cantidad_stock = ? WHERE id = ?", nuevo, productoId);
        insertar(conexion, "INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, "
                        + "stock_anterior, stock_nuevo, motivo, referencia_consulta_id, referencia_procedimiento_id, "
                        + "usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                productoId, tipo.getEtiqueta(), Math.abs(delta), anterior, nuevo, motivo, consultaId,
                procedimientoId, usuario);
    }
}
