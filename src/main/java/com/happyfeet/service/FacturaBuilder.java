package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.ItemFactura;
import com.happyfeet.model.enums.EstadoFactura;
import com.happyfeet.model.enums.MetodoPago;
import com.happyfeet.service.descuento.DescuentoStrategy;
import com.happyfeet.service.descuento.SinDescuento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Patron Builder: arma la factura paso a paso y al final calcula
 * subtotal, descuento, impuesto y total.
 */
public class FacturaBuilder {

    public static final BigDecimal TASA_IVA = new BigDecimal("0.12");

    private int duenoId;
    private String duenoNombre;
    private String numero;
    private MetodoPago metodoPago;
    private String observaciones;
    private DescuentoStrategy descuento = new SinDescuento();
    private final List<ItemFactura> items = new ArrayList<>();

    public FacturaBuilder paraDueno(int id, String nombre) {
        this.duenoId = id;
        this.duenoNombre = nombre;
        return this;
    }

    public FacturaBuilder conNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public FacturaBuilder conMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
        return this;
    }

    public FacturaBuilder conObservaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    public FacturaBuilder conDescuento(DescuentoStrategy descuento) {
        this.descuento = descuento;
        return this;
    }

    public FacturaBuilder agregarItem(ItemFactura item) {
        items.add(item);
        return this;
    }

    public Factura construir() {
        if (items.isEmpty()) {
            throw new ReglaNegocioException("La factura debe tener al menos un item");
        }
        BigDecimal subtotal = items.stream().map(ItemFactura::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuentoValor = descuento.calcular(subtotal);
        BigDecimal base = subtotal.subtract(descuentoValor);
        BigDecimal impuesto = base.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = base.add(impuesto);

        return new Factura(null, duenoId, duenoNombre, numero, null, subtotal, impuesto, descuentoValor, total,
                metodoPago, EstadoFactura.PAGADA, observaciones, List.copyOf(items));
    }
}
