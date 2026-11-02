package com.happyfeet.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger sencillo (patron Singleton). Escribe en el archivo happyfeet.log
 * que queda en la carpeta principal del proyecto.
 */
public class AppLogger {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static AppLogger instancia;

    private final Path archivo = Paths.get("happyfeet.log");

    private AppLogger() {
    }

    public static synchronized AppLogger getInstancia() {
        if (instancia == null) {
            instancia = new AppLogger();
        }
        return instancia;
    }

    public void info(String mensaje) {
        escribir("INFO", mensaje, null);
    }

    public void advertencia(String mensaje) {
        escribir("WARN", mensaje, null);
    }

    public void error(String mensaje, Throwable causa) {
        escribir("ERROR", mensaje, causa);
    }

    private synchronized void escribir(String nivel, String mensaje, Throwable causa) {
        StringBuilder linea = new StringBuilder();
        linea.append(LocalDateTime.now().format(FORMATO)).append(" [").append(nivel).append("] ").append(mensaje);
        linea.append(System.lineSeparator());
        if (causa != null) {
            StringWriter sw = new StringWriter();
            causa.printStackTrace(new PrintWriter(sw));
            linea.append(sw);
        }
        try {
            Files.writeString(archivo, linea.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Si no se puede escribir el log, al menos lo mostramos en la consola de errores
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }
}
