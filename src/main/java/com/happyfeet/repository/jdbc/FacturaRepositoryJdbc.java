package com.happyfeet.repository.jdbc;

import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.enums.EstadoFactura;
import com.happyfeet.model.enums.MetodoPago;
import com.happyfeet.model.enums.TipoItem;
import com.happyfeet.repository.FacturaRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacturaRepositoryJdbc extends BaseRepository implements FacturaRepository {

    private static final String SELECT = "SELECT f.id, f.dueno_id, d.nombre_completo AS dueno, f.numero_factura, "
            + "f.fecha_emision, f.subtotal, f.impuesto, f.descuento, f.total, f.metodo_pago, f.estado, "
            + "f.observaciones FROM facturas f JOIN duenos d ON f.dueno_id = d.id ";

    private Factura mapear(ResultSet rs) throws SQLException {
        String metodo = rs.getString("metodo_pago");
        return new Factura(
                rs.getInt("id"),
                rs.getInt("dueno_id"),
                rs.getString("dueno"),
                rs.getString("numero_factura"),
                fechaHora(rs, "fecha_emision"),
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("impuesto"),
                rs.getBigDecimal("descuento"),
                rs.getBigDecimal("total"),
                metodo == null ? null : MetodoPago.desdeEtiqueta(metodo),
                EstadoFactura.desdeEtiqueta(rs.getString("estado")),
                rs.getString("observaciones"),
                new ArrayList<>());
    }

    private ItemFactura mapearItem(ResultSet rs) throws SQLException {
        return new ItemFactura(
                rs.getInt("id"),
                TipoItem.desdeEtiqueta(rs.getString("tipo_item")),
                entero(rs, "producto_id"),
                entero(rs, "servicio_id"),
                rs.getString("descripcion"),
                rs.getInt("cantidad"),
                rs.getBigDecimal("precio_unitario"),
                rs.getBigDecimal("subtotal"));
    }

    private Factura conItems(Factura factura) {
        List<ItemFactura> items = consultar("SELECT i.id, i.tipo_item, i.producto_id, i.servicio_id, "
                + "COALESCE(i.servicio_descripcion, s.nombre, p.nombre_producto) AS descripcion, i.cantidad, "
                + "i.precio_unitario, i.subtotal FROM items_factura i "
                + "LEFT JOIN servicios s ON i.servicio_id = s.id "
                + "LEFT JOIN inventario p ON i.producto_id = p.id "
                + "WHERE i.factura_id = ? ORDER BY i.id", this::mapearItem, factura.id());
        return factura.conItems(items);
    }

    @Override
    public Factura guardarEnTransaccion(Connection c, Factura f) throws SQLException {
        int id = insertar(c, "INSERT INTO facturas (dueno_id, numero_factura, subtotal, impuesto, descuento, total, "
                        + "metodo_pago, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                f.duenoId(), f.numero(), f.subtotal(), f.impuesto(), f.descuento(), f.total(),
                f.metodoPago() == null ? null : f.metodoPago().getEtiqueta(), f.estado().getEtiqueta(),
                f.observaciones());
        for (ItemFactura item : f.items()) {
            insertar(c, "INSERT INTO items_factura (factura_id, tipo_item, producto_id, servicio_id, "
                            + "servicio_descripcion, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    id, item.tipo().getEtiqueta(), item.productoId(), item.servicioId(), item.descripcion(),
                    item.cantidad(), item.precioUnitario(), item.subtotal());
        }
        return f.conId(id);
    }

    @Override
    public Factura guardar(Factura factura) {
        return db.enTransaccion(c -> guardarEnTransaccion(c, factura));
    }

    @Override
    public Optional<Factura> buscarPorId(int id) {
        return consultarUno(SELECT + "WHERE f.id = ?", this::mapear, id).map(this::conItems);
    }

    @Override
    public List<Factura> listar() {
        return consultar(SELECT + "ORDER BY f.fecha_emision DESC", this::mapear).stream()
                .map(this::conItems).toList();
    }

    @Override
    public String siguienteNumero() {
        int siguiente = consultar("SELECT COALESCE(MAX(id), 0) + 1 FROM facturas", rs -> rs.getInt(1)).get(0);
        return String.format("FAC-%d-%06d", LocalDate.now().getYear(), siguiente);
    }

    @Override
    public List<Factura> listarPorPeriodo(LocalDateTime desde, LocalDateTime hasta) {
        return consultar(SELECT + "WHERE f.fecha_emision BETWEEN ? AND ? ORDER BY f.fecha_emision",
                this::mapear, desde, hasta).stream().map(this::conItems).toList();
    }
}
