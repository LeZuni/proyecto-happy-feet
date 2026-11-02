package com.happyfeet.exception;

/**
 * Excepcion base del sistema. Es "unchecked" para no llenar el codigo de try/catch.
 */
public class HappyFeetException extends RuntimeException {

    public HappyFeetException(String mensaje) {
        super(mensaje);
    }

    public HappyFeetException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
