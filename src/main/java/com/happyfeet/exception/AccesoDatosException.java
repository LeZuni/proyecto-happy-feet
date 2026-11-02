package com.happyfeet.exception;

/**
 * Se lanza cuando falla algo con la base de datos (SQLException envuelta).
 */
public class AccesoDatosException extends HappyFeetException {

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
