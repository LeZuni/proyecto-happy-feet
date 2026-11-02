package com.happyfeet.model.enums;

/**
 * Niveles del Club de Mascotas Frecuentes. El nivel depende de los puntos acumulados.
 */
public enum NivelClub implements ConEtiqueta {
    BRONCE("Bronce", 0),
    PLATA("Plata", 500),
    ORO("Oro", 1500),
    PLATINO("Platino", 3000);

    private final String etiqueta;
    private final int puntosMinimos;

    NivelClub(String etiqueta, int puntosMinimos) {
        this.etiqueta = etiqueta;
        this.puntosMinimos = puntosMinimos;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    public int getPuntosMinimos() {
        return puntosMinimos;
    }

    /** Devuelve el nivel mas alto que alcanza con esos puntos acumulados. */
    public static NivelClub paraPuntos(int puntosAcumulados) {
        NivelClub resultado = BRONCE;
        for (NivelClub nivel : values()) {
            if (puntosAcumulados >= nivel.puntosMinimos) {
                resultado = nivel;
            }
        }
        return resultado;
    }

    public static NivelClub desdeEtiqueta(String texto) {
        return ConEtiqueta.buscar(NivelClub.class, texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
