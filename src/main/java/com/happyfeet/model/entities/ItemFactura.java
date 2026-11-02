package com.happyfeet.model.entities;

import com.happyfeet.model.enums.TipoItem;

import java.math.BigDecimal;

public record ItemFactura(
        Integer id,
        TipoItem tipo,
        Integer productoId,
        Integer servicioId,
        String descripcion,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {

    public static ItemFactura deServicio(int servicioId, String descripcion, int cantidad, BigDecimal precio) {
        return new ItemFactura(null, TipoItem.SERVICIO, null, servicioId, descripcion, cantidad, precio,
                precio.multiply(BigDecimal.valueOf(cantidad)));
    }

    public static ItemFactura deProducto(int productoId, String descripcion, int cantidad, BigDecimal precio) {
        return new ItemFactura(null, TipoItem.PRODUCTO, productoId, null, descripcion, cantidad, precio,
                precio.multiply(BigDecimal.valueOf(cantidad)));
    }
}
