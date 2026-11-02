package com.happyfeet.model.entities;

public record Proveedor(
        Integer id,
        String nombreEmpresa,
        String contacto,
        String telefono,
        String email,
        String direccion,
        boolean activo) {
}
