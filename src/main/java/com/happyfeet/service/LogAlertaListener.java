package com.happyfeet.service;

import com.happyfeet.util.AppLogger;

/** Observer que guarda cada alerta del inventario en el archivo de log. */
public class LogAlertaListener implements AlertaInventarioListener {

    @Override
    public void alertar(AlertaInventario alerta) {
        AppLogger.getInstancia().advertencia("Alerta de inventario: " + alerta.mensaje());
    }
}
