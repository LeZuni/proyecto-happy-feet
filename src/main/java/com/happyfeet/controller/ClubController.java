package com.happyfeet.controller;

import com.happyfeet.model.entities.BeneficioClub;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.service.ClubService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.view.ClubView;
import com.happyfeet.view.Consola;
import com.happyfeet.view.PacienteView;

/** Controlador del Club de Mascotas Frecuentes. */
public class ClubController extends ControladorBase {

    private final ClubService clubService;
    private final DuenoService duenoService;
    private final ClubView view;
    private final PacienteView pacienteView;

    public ClubController(Consola consola, ClubService clubService, DuenoService duenoService, ClubView view,
                          PacienteView pacienteView) {
        super(consola);
        this.clubService = clubService;
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
            case 1 -> inscribir();
            case 2 -> consultar();
            case 3 -> view.mostrarBeneficios(clubService.listarBeneficios());
            case 4 -> canjear();
            case 5 -> view.mostrarRanking(clubService.ranking(),
                    id -> duenoService.buscarPorId(id).nombreCompleto());
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void inscribir() {
        pacienteView.mostrarDuenos(duenoService.listar());
        ClubMascotas club = clubService.inscribir(pacienteView.pedirIdDueno());
        consola.exito("Dueño inscrito en el club (nivel " + club.nivel() + ")");
    }

    private void consultar() {
        int duenoId = pacienteView.pedirIdDueno();
        view.mostrarClub(clubService.obtenerPorDueno(duenoId), duenoService.buscarPorId(duenoId).nombreCompleto());
    }

    private void canjear() {
        int duenoId = pacienteView.pedirIdDueno();
        view.mostrarClub(clubService.obtenerPorDueno(duenoId), duenoService.buscarPorId(duenoId).nombreCompleto());
        view.mostrarBeneficios(clubService.listarBeneficios());
        BeneficioClub canjeado = clubService.canjear(duenoId, view.pedirIdBeneficio());
        consola.exito("Beneficio canjeado: " + canjeado.nombre() + " (valido por 30 dias)");
    }
}
