package com.happyfeet.model.enums;

public enum EstadoJornada implements ConEtiqueta {
    PLANIFICADA("Planificada"),
    EN_CURSO("En Curso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoJornada(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoJornada desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(EstadoJornada.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
