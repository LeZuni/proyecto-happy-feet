package com.happyfeet.model.entities;

/** Insumo (jeringas, gasas, suturas...) que se gasto en un procedimiento. */
public record InsumoProcedimiento(int productoId, int cantidad, String observaciones) {
}
