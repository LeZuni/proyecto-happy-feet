package com.happyfeet.model.enums;

public enum Sexo implements ConEtiqueta {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String etiqueta;

    Sexo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static Sexo desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(Sexo.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
