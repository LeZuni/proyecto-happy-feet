package com.happyfeet.service.descuento;

import java.math.BigDecimal;

/**
 * Patron Strategy: cada forma de calcular el descuento es una clase distinta
 * y la factura no necesita saber cual se esta usando.
 */
public interface DescuentoStrategy {

    BigDecimal calcular(BigDecimal subtotal);

    String descripcion();
}
