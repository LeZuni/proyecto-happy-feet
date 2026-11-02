package com.happyfeet.model.enums;

import java.util.Arrays;

/**
 * Los enums que tienen un texto igual al que se guarda en la base de datos
 * (por ejemplo "En Proceso") implementan esta interfaz.
 */
public interface ConEtiqueta {

    String getEtiqueta();

    static <E extends Enum<E> & ConEtiqueta> E buscar(Class<E> tipo, String etiqueta) {
        return Arrays.stream(tipo.getEnumConstants())
                .filter(e -> e.getEtiqueta().equalsIgnoreCase(etiqueta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Valor no valido para " + tipo.getSimpleName() + ": " + etiqueta));
    }
}
