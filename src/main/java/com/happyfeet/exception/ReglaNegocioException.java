package com.happyfeet.exception;

/**
 * Se lanza cuando algo no cumple una regla de negocio (datos invalidos, duplicados, etc.).
 */
public class ReglaNegocioException extends HappyFeetException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
