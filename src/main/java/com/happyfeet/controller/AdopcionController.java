package com.happyfeet.controller;

import com.happyfeet.model.entities.Adopcion;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.service.AdopcionService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.MascotaService;
import com.happyfeet.view.AdopcionView;
import com.happyfeet.view.Consola;
import com.happyfeet.view.PacienteView;

/** Controlador de los dias de adopcion. */
public class AdopcionController extends ControladorBase {

    private final AdopcionService adopcionService;
    private final MascotaService mascotaService;
    private final DuenoService duenoService;
    private final AdopcionView view;
    private final PacienteView pacienteView;

    public AdopcionController(Consola consola, AdopcionService adopcionService, MascotaService mascotaService,
                              DuenoService duenoService, AdopcionView view, PacienteView pacienteView) {
        super(consola);
        this.adopcionService = adopcionService;
        this.mascotaService = mascotaService;
        this.duenoService = duenoService;
        this.view = view;
        this.pacienteView = pacienteView;
    }

    @Override
    protected int mostrarMenu() {
        return view.menu();
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrar();
            case 2 -> view.mostrarDisponibles(adopcionService.listarDisponibles());
            case 3 -> adoptar();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void registrar() {
        consola.mensaje("La mascota debe estar registrada antes en el modulo de pacientes.");
        pacienteView.mostrarMascotas(mascotaService.listar());
        Mascota mascota = mascotaService.buscarPorId(pacienteView.pedirIdMascota());
        var guardada = adopcionService.registrar(view.pedirMascotaAdopcion(mascota.id()));
        consola.exito("Mascota registrada para adopcion con id " + guardada.id());
    }

    private void adoptar() {
        view.mostrarDisponibles(adopcionService.listarDisponibles());
        int adopcionId = view.pedirIdAdopcion();
        pacienteView.mostrarDuenos(duenoService.listar());
        consola.mensaje("Escriba el id del adoptante:");
        int duenoId = pacienteView.pedirIdDueno();
        Adopcion adopcion = adopcionService.adoptar(adopcionId, duenoId, view.pedirCondiciones());
        view.mostrarContrato(adopcion.contratoTexto());
        consola.exito("Adopcion registrada. La mascota ahora pertenece al adoptante.");
    }
}
