package com.happyfeet.controller;

import com.happyfeet.view.Consola;

/** Menu del Modulo 5: reparte hacia adopciones, jornadas de vacunacion y club. */
public class ActividadesEspecialesController extends ControladorBase {

    private final AdopcionController adopcionController;
    private final JornadaController jornadaController;
    private final ClubController clubController;

    public ActividadesEspecialesController(Consola consola, AdopcionController adopcionController,
                                           JornadaController jornadaController, ClubController clubController) {
        super(consola);
        this.adopcionController = adopcionController;
        this.jornadaController = jornadaController;
        this.clubController = clubController;
    }

    @Override
    protected int mostrarMenu() {
        return consola.menu("Actividades especiales", "Dias de adopcion", "Jornadas de vacunacion",
                "Club de mascotas frecuentes");
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> adopcionController.iniciar();
            case 2 -> jornadaController.iniciar();
            case 3 -> clubController.iniciar();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }
}
