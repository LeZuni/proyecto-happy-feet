package com.happyfeet.repository;

import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.TipoProducto;
import com.happyfeet.model.enums.TipoMovimiento;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductoRepository extends Repository<Producto> {

    List<Producto> listarPorTipo(String nombreTipo);

    List<TipoProducto> listarTipos();

    /**
     * Suma o resta stock (delta positivo = entrada, negativo = salida) y deja el
     * movimiento registrado. Recibe la conexion porque siempre va dentro de una transaccion.
     */
    void moverStock(Connection conexion, int productoId, int delta, TipoMovimiento tipo, String motivo,
                    Integer consultaId, Integer procedimientoId, String usuario) throws SQLException;
}
