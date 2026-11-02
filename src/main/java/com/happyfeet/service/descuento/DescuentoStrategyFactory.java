package com.happyfeet.service.descuento;

import com.happyfeet.model.enums.NivelClub;

/** Patron Factory: elige el descuento que le corresponde a cada nivel del club. */
public final class DescuentoStrategyFactory {

    private DescuentoStrategyFactory() {
    }

    public static DescuentoStrategy para(NivelClub nivel) {
        return switch (nivel) {
            case BRONCE -> new SinDescuento();
            case PLATA -> new DescuentoPorcentual(5, "Club Plata");
            case ORO -> new DescuentoPorcentual(10, "Club Oro");
            case PLATINO -> new DescuentoPorcentual(15, "Club Platino");
        };
    }
}
