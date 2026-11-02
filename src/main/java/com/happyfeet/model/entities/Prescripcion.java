package com.happyfeet.model.entities;

/** Medicamento recetado en una consulta o en un procedimiento. */
public record Prescripcion(
        Integer id,
        Integer consultaId,
        Integer procedimientoId,
        int productoId,
        int cantidad,
        String dosis,
        String frecuencia,
        Integer duracionDias,
        String instrucciones) {

    public Prescripcion paraConsulta(int idConsulta) {
        return new Prescripcion(id, idConsulta, null, productoId, cantidad, dosis, frecuencia, duracionDias, instrucciones);
    }

    public Prescripcion paraProcedimiento(int idProcedimiento) {
        return new Prescripcion(id, null, idProcedimiento, productoId, cantidad, dosis, frecuencia, duracionDias, instrucciones);
    }
}
