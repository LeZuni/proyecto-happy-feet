package com.happyfeet.controller;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.model.enums.EstadoCita;
import com.happyfeet.model.enums.EstadoProcedimiento;
import com.happyfeet.service.CitaService;
import com.happyfeet.service.ConsultaService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.ProcedimientoService;
import com.happyfeet.service.VeterinarioService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.PacienteView;
import com.happyfeet.view.ServiciosMedicosView;

import java.time.LocalDate;
import java.util.List;

/** Controlador del Modulo 2: citas, consultas y procedimientos. */
public class ServiciosMedicosController extends ControladorBase {

    private final CitaService citaService;
    private final ConsultaService consultaService;
    private final ProcedimientoService procedimientoService;
    private final VeterinarioService veterinarioService;
    private final MascotaService mascotaService;
    private final InventarioService inventarioService;
    private final ServiciosMedicosView view;
    private final PacienteView pacienteView;

    public ServiciosMedicosController(Consola consola, CitaService citaService, ConsultaService consultaService,
                                      ProcedimientoService procedimientoService,
                                      VeterinarioService veterinarioService, MascotaService mascotaService,
                                      InventarioService inventarioService, ServiciosMedicosView view,
                                      PacienteView pacienteView) {
        super(consola);
        this.citaService = citaService;
        this.consultaService = consultaService;
        this.procedimientoService = procedimientoService;
        this.veterinarioService = veterinarioService;
        this.mascotaService = mascotaService;
        this.inventarioService = inventarioService;
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
            case 1 -> agendarCita();
            case 2 -> view.mostrarCitas(citaService.listar());
            case 3 -> view.mostrarCitas(citaService.listarPorFecha(view.pedirFecha()));
            case 4 -> cambiarEstadoCita();
            case 5 -> registrarConsulta();
            case 6 -> registrarProcedimiento();
            case 7 -> actualizarSeguimiento();
            case 8 -> verAtencionesDeMascota();
            case 9 -> view.mostrarVeterinarios(veterinarioService.listar());
            case 10 -> registrarVeterinario();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private Mascota elegirMascota() {
        pacienteView.mostrarMascotas(mascotaService.listar());
        return mascotaService.buscarPorId(pacienteView.pedirIdMascota());
    }

    private void agendarCita() {
        Mascota mascota = elegirMascota();
        Veterinario vet = view.elegirVeterinario(veterinarioService.listar());
        Cita cita = citaService.agendar(mascota.id(), vet.id(), view.pedirFechaHoraCita(), view.pedirMotivo());
        consola.exito("Cita agendada con id " + cita.id());
    }

    private void cambiarEstadoCita() {
        view.mostrarCitas(citaService.listar().stream().filter(c -> !c.estado().estaCerrada()).toList());
        int citaId = view.pedirIdCita();
        consola.mensaje("Nuevo estado:");
        EstadoCita estado = consola.elegirEnum("Estado", EstadoCita.class);
        citaService.cambiarEstado(citaId, estado);
        consola.exito("Estado de la cita actualizado");
    }

    private void registrarConsulta() {
        Mascota mascota = elegirMascota();
        Veterinario vet = view.elegirVeterinario(veterinarioService.listar());

        List<Cita> pendientes = citaService.listar().stream()
                .filter(c -> c.mascotaId() == mascota.id() && !c.estado().estaCerrada())
                .toList();
        Integer citaId = view.elegirCitaOpcional(pendientes);

        ConsultaMedica consulta = view.pedirConsulta(mascota.id(), vet.id(), citaId);
        List<Prescripcion> prescripciones = view.pedirPrescripciones(inventarioService.listarPorTipo("Medicamento"));

        ConsultaMedica guardada = consultaService.registrar(consulta, prescripciones);
        consola.exito("Consulta registrada con id " + guardada.id()
                + (prescripciones.isEmpty() ? "" : " (inventario descontado automaticamente)"));
    }

    private void registrarProcedimiento() {
        Mascota mascota = elegirMascota();
        Veterinario vet = view.elegirVeterinario(veterinarioService.listar());
        ProcedimientoEspecial procedimiento = view.pedirProcedimiento(mascota.id(), vet.id());

        List<InsumoProcedimiento> insumos = List.of();
        List<Prescripcion> prescripciones = List.of();
        if (procedimiento.estado() == EstadoProcedimiento.EN_PROCESO
                || procedimiento.estado() == EstadoProcedimiento.FINALIZADO) {
            insumos = view.pedirInsumos(inventarioService.listarPorTipo("Insumo Médico"));
            prescripciones = view.pedirPrescripciones(inventarioService.listarPorTipo("Medicamento"));
        }

        ProcedimientoEspecial guardado = procedimientoService.registrar(procedimiento, insumos, prescripciones);
        consola.exito("Procedimiento registrado con id " + guardado.id());
    }

    private void actualizarSeguimiento() {
        view.mostrarProcedimientos(procedimientoService.listar());
        int id = view.pedirIdProcedimiento();
        Object[] datos = view.pedirSeguimiento();
        procedimientoService.actualizarSeguimiento(id, (EstadoProcedimiento) datos[0], (String) datos[1],
                (String) datos[2], (LocalDate) datos[3]);
        consola.exito("Seguimiento actualizado");
    }

    private void verAtencionesDeMascota() {
        Mascota mascota = elegirMascota();
        view.mostrarConsultas(consultaService.listarPorMascota(mascota.id()));
        view.mostrarProcedimientos(procedimientoService.listar().stream()
                .filter(p -> p.mascotaId() == mascota.id()).toList());
    }

    private void registrarVeterinario() {
        Veterinario guardado = veterinarioService.registrar(view.pedirVeterinario());
        consola.exito("Veterinario registrado con id " + guardado.id());
    }
}
