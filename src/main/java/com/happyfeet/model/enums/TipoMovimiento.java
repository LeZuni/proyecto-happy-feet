package com.happyfeet.model.enums;

public enum TipoMovimiento implements ConEtiqueta {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
    AJUSTE("Ajuste"),
    VENCIMIENTO("Vencimiento");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoMovimiento desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(TipoMovimiento.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
