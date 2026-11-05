package com.happyfeet.controller;

import com.happyfeet.model.Producto;
import com.happyfeet.observer.Observador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArchivoLogObservador implements Observador {
    private static final String FILE_NAME = "reabastecimiento.log";

    @Override
    public void actualizar(Producto producto) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logLine = String.format("[%s] - ALERTA: Producto ID %s \"%s\" alcanzó el stock mínimo. Cantidad restante: %d.%n",
                timestamp, producto.getId(), producto.getNombre(), producto.getCantidadStock());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(logLine);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de log: " + e.getMessage());
        }
    }
}