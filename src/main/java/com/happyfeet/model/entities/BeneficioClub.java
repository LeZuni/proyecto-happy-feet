package com.happyfeet.model.entities;

import com.happyfeet.model.enums.NivelClub;
import com.happyfeet.model.enums.TipoBeneficio;

import java.math.BigDecimal;

public record BeneficioClub(
        Integer id,
        String nombre,
        String descripcion,
        NivelClub nivelRequerido,
        int puntosNecesarios,
        TipoBeneficio tipo,
        BigDecimal valor) {
}
