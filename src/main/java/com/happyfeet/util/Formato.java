package com.happyfeet.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Metodos para mostrar fechas y dinero de forma bonita.
 */
public final class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(FECHA_HORA);
    }

    public static String hora(LocalTime hora) {
        return hora == null ? "-" : hora.format(HORA);
    }

    public static String dinero(BigDecimal valor) {
        if (valor == null) {
            return "-";
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-GT"));
        return nf.format(valor);
    }

    /** Devuelve "-" si el texto viene vacio o nulo. */
    public static String texto(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    /** Corta un texto largo para que quepa en una tabla. */
    public static String cortar(String texto, int max) {
        String t = texto(texto);
        return t.length() <= max ? t : t.substring(0, max - 3) + "...";
    }
}
