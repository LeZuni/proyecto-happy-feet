package com.happyfeet.model.enums;

public enum EstadoProcedimiento implements ConEtiqueta {
    PROGRAMADO("Programado"),
    EN_PROCESO("En Proceso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoProcedimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoProcedimiento desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(EstadoProcedimiento.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
