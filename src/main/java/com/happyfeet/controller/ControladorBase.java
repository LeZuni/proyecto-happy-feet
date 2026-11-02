package com.happyfeet.controller;

import com.happyfeet.exception.HappyFeetException;
import com.happyfeet.util.AppLogger;
import com.happyfeet.view.Consola;

import java.util.NoSuchElementException;

/**
 * Patron Template Method: el ciclo del menu (mostrar menu, ejecutar la opcion,
 * manejar los errores) es igual para todos los modulos. Cada controlador solo
 * dice como se muestra su menu y que hace cada opcion.
 */
public abstract class ControladorBase {

    protected final Consola consola;
    protected final AppLogger logger = AppLogger.getInstancia();

    protected ControladorBase(Consola consola) {
        this.consola = consola;
    }

    /** Muestra el menu del modulo y devuelve la opcion elegida. */
    protected abstract int mostrarMenu();

    /** Ejecuta la opcion. Devuelve false si el usuario quiere salir del menu. */
    protected abstract boolean ejecutarOpcion(int opcion);

    public final void iniciar() {
        boolean continuar = true;
        while (continuar) {
            try {
                int opcion = mostrarMenu();
                continuar = ejecutarOpcion(opcion);
                if (continuar) {
                    consola.pausa();
                }
            } catch (HappyFeetException e) {
                // Errores esperados (reglas de negocio, base de datos): se muestran y se guardan en el log
                logger.advertencia(e.getMessage());
                consola.error(e.getMessage());
            } catch (NoSuchElementException e) {
                // Se cerro la entrada del teclado, no hay nada mas que leer
                continuar = false;
            } catch (Exception e) {
                logger.error("Error inesperado", e);
                consola.error("Ocurrio un error inesperado. Revise el archivo happyfeet.log");
            }
        }
    }
}
