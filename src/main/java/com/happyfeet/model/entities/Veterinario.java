package com.happyfeet.model.entities;

public record Veterinario(
        Integer id,
        String nombreCompleto,
        String documentoIdentidad,
        String licenciaProfesional,
        String especialidad,
        String telefono,
        String email,
        boolean activo) {
}
