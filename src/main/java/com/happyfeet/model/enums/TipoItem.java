package com.happyfeet.model.enums;

public enum TipoItem implements ConEtiqueta {
    PRODUCTO("Producto"),
    SERVICIO("Servicio");

    private final String etiqueta;

    TipoItem(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoItem desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(TipoItem.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
