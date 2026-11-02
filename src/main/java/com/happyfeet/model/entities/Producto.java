package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Producto del inventario: medicamento, vacuna, insumo medico, alimento o accesorio. */
public record Producto(
        Integer id,
        String nombre,
        int tipoId,
        String tipoNombre,
        String fabricante,
        Integer proveedorId,
        String lote,
        int cantidadStock,
        int stockMinimo,
        String unidadMedida,
        LocalDate fechaVencimiento,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        boolean requiereReceta,
        boolean activo) {

    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    /** True si todavia no vence pero vence dentro de los proximos "dias" dias. */
    public boolean venceEn(int dias) {
        return fechaVencimiento != null
                && !estaVencido()
                && !fechaVencimiento.isAfter(LocalDate.now().plusDays(dias));
    }

    public boolean stockBajo() {
        return cantidadStock <= stockMinimo;
    }
}
