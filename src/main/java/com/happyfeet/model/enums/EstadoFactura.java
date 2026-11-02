package com.happyfeet.model.enums;

public enum EstadoFactura implements ConEtiqueta {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    ANULADA("Anulada");

    private final String etiqueta;

    EstadoFactura(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoFactura desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(EstadoFactura.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
