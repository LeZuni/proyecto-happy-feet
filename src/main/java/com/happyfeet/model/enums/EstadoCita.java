package com.happyfeet.model.enums;

public enum EstadoCita implements ConEtiqueta {
    PROGRAMADA("Programada"),
    CONFIRMADA("Confirmada"),
    EN_PROCESO("En Proceso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada"),
    NO_ASISTIO("No Asistió");

    private final String etiqueta;

    EstadoCita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    /** Una cita cerrada ya no se puede modificar ni ocupa horario. */
    public boolean estaCerrada() {
        return this == FINALIZADA || this == CANCELADA || this == NO_ASISTIO;
    }

    public static EstadoCita desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(EstadoCita.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
