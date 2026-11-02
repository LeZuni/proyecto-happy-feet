package com.happyfeet.model.enums;

public enum MetodoPago implements ConEtiqueta {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    MIXTO("Mixto");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static MetodoPago desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(MetodoPago.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
