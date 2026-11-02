package com.happyfeet.service;

/**
 * Patron Observer: quien quiera enterarse de las alertas del inventario
 * implementa esta interfaz y se registra en el InventarioService.
 */
@FunctionalInterface
public interface AlertaInventarioListener {

    void alertar(AlertaInventario alerta);
}
