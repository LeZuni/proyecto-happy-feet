package com.happyfeet.service.descuento;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Descuento de un porcentaje fijo sobre el subtotal (por ejemplo 10%). */
public class DescuentoPorcentual implements DescuentoStrategy {

    private final int porcentaje;
    private final String nombre;

    public DescuentoPorcentual(int porcentaje, String nombre) {
        this.porcentaje = porcentaje;
        this.nombre = nombre;
    }

    @Override
    public BigDecimal calcular(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(porcentaje))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String descripcion() {
        return nombre + " (" + porcentaje + "%)";
    }
}
