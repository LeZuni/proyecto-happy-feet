package com.happyfeet.service.descuento;

import java.math.BigDecimal;

public class SinDescuento implements DescuentoStrategy {

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2);
    }

    @Override
    public String descripcion() {
        return "Sin descuento";
    }
}
