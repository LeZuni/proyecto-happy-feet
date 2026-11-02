package com.happyfeet.model.enums;

public enum TipoAlerta implements ConEtiqueta {
    STOCK_BAJO("Stock bajo"),
    POR_VENCER("Proximo a vencer"),
    VENCIDO("Vencido");

    private final String etiqueta;

    TipoAlerta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
