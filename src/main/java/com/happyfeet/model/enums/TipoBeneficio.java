package com.happyfeet.model.enums;

public enum TipoBeneficio implements ConEtiqueta {
    DESCUENTO("Descuento"),
    SERVICIO_GRATIS("Servicio Gratis"),
    PRODUCTO_GRATIS("Producto Gratis"),
    PUNTOS_EXTRA("Puntos Extra");

    private final String etiqueta;

    TipoBeneficio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoBeneficio desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(TipoBeneficio.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
