package com.happyfeet.model.entities;

import com.happyfeet.model.enums.EstadoFactura;
import com.happyfeet.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Factura(
        Integer id,
        int duenoId,
        String duenoNombre,
        String numero,
        LocalDateTime fechaEmision,
        BigDecimal subtotal,
        BigDecimal impuesto,
        BigDecimal descuento,
        BigDecimal total,
        MetodoPago metodoPago,
        EstadoFactura estado,
        String observaciones,
        List<ItemFactura> items) {

    public Factura conId(int nuevoId) {
        return new Factura(nuevoId, duenoId, duenoNombre, numero, fechaEmision, subtotal, impuesto, descuento,
                total, metodoPago, estado, observaciones, items);
    }

    public Factura conItems(List<ItemFactura> nuevosItems) {
        return new Factura(id, duenoId, duenoNombre, numero, fechaEmision, subtotal, impuesto, descuento,
                total, metodoPago, estado, observaciones, nuevosItems);
    }
}
