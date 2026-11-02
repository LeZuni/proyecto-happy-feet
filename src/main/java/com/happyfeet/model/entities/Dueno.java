package com.happyfeet.model.entities;

public record Dueno(
        Integer id,
        String nombreCompleto,
        String documentoIdentidad,
        String direccion,
        String telefono,
        String email,
        String contactoEmergencia,
        boolean activo) {
}
