package com.happyfeet.model.enums;

public enum EstadoAdopcion implements ConEtiqueta {
    DISPONIBLE("Disponible"),
    EN_PROCESO("En Proceso"),
    ADOPTADA("Adoptada"),
    RETIRADA("Retirada");

    private final String etiqueta;

    EstadoAdopcion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoAdopcion desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(EstadoAdopcion.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
