package com.happyfeet.model.enums;

public enum TipoPuntos implements ConEtiqueta {
    GANADOS("Ganados"),
    CANJEADOS("Canjeados"),
    EXPIRADOS("Expirados"),
    AJUSTE("Ajuste");

    private final String etiqueta;

    TipoPuntos(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoPuntos desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(TipoPuntos.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
