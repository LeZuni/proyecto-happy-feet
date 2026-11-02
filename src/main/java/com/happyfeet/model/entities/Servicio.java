package com.happyfeet.model.entities;

import java.math.BigDecimal;

public record Servicio(
        Integer id,
        String nombre,
        String descripcion,
        String categoria,
        BigDecimal precioBase,
        boolean activo) {
}
