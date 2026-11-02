package com.happyfeet.controller;

import com.happyfeet.exception.HappyFeetException;
import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.service.JornadaService;
import com.happyfeet.service.VeterinarioService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.JornadaView;

import java.time.LocalDate;

/** Controlador de las jornadas de vacunacion. */
public class JornadaController extends ControladorBase {

    private final JornadaService jornadaService;
    private final VeterinarioService veterinarioService;
    private final JornadaView view;

    public JornadaController(Consola consola, JornadaService jornadaService, VeterinarioService veterinarioService,
                             JornadaView view) {
        super(consola);
        this.jornadaService = jornadaService;
        this.veterinarioService = veterinarioService;
        this.view = view;
    }

    @Override
    protected int mostrarMenu() {
        return view.menu();
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> {
                JornadaVacunacion j = jornadaService.crear(view.pedirJornada());
                consola.exito("Jornada creada con id " + j.id());
            }
            case 2 -> view.mostrarJornadas(jornadaService.listar());
            case 3 -> cambiarEstado();
            case 4 -> registroRapido();
            case 5 -> {
                view.mostrarJornadas(jornadaService.listar());
                view.mostrarRegistros(jornadaService.listarRegistros(view.pedirIdJornada()));
            }
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void cambiarEstado() {
        view.mostrarJornadas(jornadaService.listar());
        int id = view.pedirIdJornada();
        jornadaService.cambiarEstado(id, view.pedirNuevoEstado());
        consola.exito("Estado de la jornada actualizado");
    }

    /** Se eligen la vacuna y el veterinario una sola vez y luego solo se escriben ids de mascotas. */
    private void registroRapido() {
        view.mostrarJornadas(jornadaService.listar().stream()
                .filter(j -> j.estado().name().equals("EN_CURSO")).toList());
        int jornadaId = view.pedirIdJornada();
        jornadaService.buscarPorId(jornadaId);
        Producto vacuna = view.elegirVacuna(jornadaService.vacunasDisponibles());
        Veterinario vet = view.elegirVeterinario(veterinarioService.listar());
        LocalDate proxima = view.pedirProximaDosis();

        int vacunadas = 0;
        int mascotaId = view.pedirMascotaRapida();
        while (mascotaId != 0) {
            try {
                var registro = jornadaService.registrarVacunacion(jornadaId, mascotaId, vacuna.id(), vet.id(), proxima);
                vacunadas++;
                consola.exito(registro.mascotaNombre() + " vacunada");
            } catch (HappyFeetException e) {
                // Un error con una mascota no debe cortar el registro de las demas
                logger.advertencia(e.getMessage());
                consola.error(e.getMessage());
            }
            mascotaId = view.pedirMascotaRapida();
        }
        consola.mensaje("Mascotas vacunadas en esta sesion: " + vacunadas);
    }
}
