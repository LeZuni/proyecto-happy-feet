package com.happyfeet;

import com.happyfeet.controller.AppController;
import com.happyfeet.controller.ControladorFactory;
import com.happyfeet.util.AppLogger;
import com.happyfeet.util.DatabaseConnection;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Punto de entrada de la aplicacion. */
public class Main {

    public static void main(String[] args) {
        // Para que se vean bien las tildes y la ñ en la consola
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        AppLogger logger = AppLogger.getInstancia();
        logger.info("Aplicacion iniciada");

        try {
            DatabaseConnection.getInstancia().probarConexion();
            AppController app = new ControladorFactory().crearAppController();
            app.mostrarAlertasIniciales();
            app.iniciar();
        } catch (Exception e) {
            logger.error("La aplicacion termino por un error", e);
            System.out.println("[ERROR] No se pudo iniciar el sistema: " + e.getMessage());
            System.out.println("Revise que MySQL este encendido y que db.properties tenga los datos correctos.");
        }
        logger.info("Aplicacion cerrada");
    }
}
