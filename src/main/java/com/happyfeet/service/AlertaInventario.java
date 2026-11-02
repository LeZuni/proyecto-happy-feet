package com.happyfeet.service;

import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.enums.TipoAlerta;
import com.happyfeet.util.Formato;

/** Aviso que genera el inventario (stock bajo, producto por vencer o vencido). */
public record AlertaInventario(Producto producto, TipoAlerta tipo) {

    public String mensaje() {
        return switch (tipo) {
            case STOCK_BAJO -> "Stock bajo de " + producto.nombre() + ": quedan " + producto.cantidadStock()
                    + " (minimo " + producto.stockMinimo() + ")";
            case POR_VENCER -> producto.nombre() + " vence pronto: " + Formato.fecha(producto.fechaVencimiento());
            case VENCIDO -> producto.nombre() + " esta VENCIDO desde " + Formato.fecha(producto.fechaVencimiento());
        };
    }
}
