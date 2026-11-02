package com.happyfeet.controller;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.MascotaService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.PacienteView;

/** Controlador del Modulo 1: pacientes (dueños y mascotas). */
public class PacienteController extends ControladorBase {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;
    private final PacienteView view;

    public PacienteController(Consola consola, DuenoService duenoService, MascotaService mascotaService,
                              PacienteView view) {
        super(consola);
        this.duenoService = duenoService;
        this.mascotaService = mascotaService;
        this.view = view;
    }

    @Override
    protected int mostrarMenu() {
        return view.menu();
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarDueno();
            case 2 -> view.mostrarDuenos(duenoService.listar());
            case 3 -> registrarMascota();
            case 4 -> view.mostrarMascotas(mascotaService.listar());
            case 5 -> verFicha();
            case 6 -> transferirMascota();
            case 7 -> actualizarPeso();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void registrarDueno() {
        Dueno guardado = duenoService.registrar(view.pedirDueno());
        consola.exito("Dueño registrado con id " + guardado.id());
    }

    private void registrarMascota() {
        view.mostrarDuenos(duenoService.listar());
        int duenoId = view.pedirIdDueno();
        duenoService.buscarPorId(duenoId); // si no existe lanza el error antes de pedir los datos
        Mascota guardada = mascotaService.registrar(view.pedirMascota(duenoId, mascotaService.listarRazas()));
        consola.exito("Mascota registrada con id " + guardada.id());
    }

    private void verFicha() {
        view.mostrarMascotas(mascotaService.listar());
        Mascota mascota = mascotaService.buscarPorId(view.pedirIdMascota());
        view.mostrarFicha(mascota, mascotaService.obtenerHistorial(mascota.id()));
    }

    private void transferirMascota() {
        view.mostrarMascotas(mascotaService.listar());
        int mascotaId = view.pedirIdMascota();
        view.mostrarDuenos(duenoService.listar());
        consola.mensaje("Escriba el id del NUEVO dueño:");
        int nuevoDuenoId = view.pedirIdDueno();
        mascotaService.transferir(mascotaId, nuevoDuenoId);
        consola.exito("La mascota fue transferida al nuevo dueño");
    }

    private void actualizarPeso() {
        view.mostrarMascotas(mascotaService.listar());
        int mascotaId = view.pedirIdMascota();
        mascotaService.actualizarPeso(mascotaId, view.pedirPeso());
        consola.exito("Peso actualizado");
    }
}
