package com.happyfeet.controller;

import com.happyfeet.service.InventarioService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.InventarioView;

/** Menu principal: desde aqui se llega a los 5 modulos del sistema. */
public class AppController extends ControladorBase {

    private final PacienteController pacienteController;
    private final ServiciosMedicosController serviciosMedicosController;
    private final InventarioController inventarioController;
    private final FacturacionController facturacionController;
    private final ActividadesEspecialesController actividadesController;
    private final InventarioService inventarioService;
    private final InventarioView inventarioView;

    public AppController(Consola consola, PacienteController pacienteController,
                         ServiciosMedicosController serviciosMedicosController,
                         InventarioController inventarioController, FacturacionController facturacionController,
                         ActividadesEspecialesController actividadesController, InventarioService inventarioService,
                         InventarioView inventarioView) {
        super(consola);
        this.pacienteController = pacienteController;
        this.serviciosMedicosController = serviciosMedicosController;
        this.inventarioController = inventarioController;
        this.facturacionController = facturacionController;
        this.actividadesController = actividadesController;
        this.inventarioService = inventarioService;
        this.inventarioView = inventarioView;
    }

    /** Al abrir el sistema se muestran las alertas del inventario. */
    public void mostrarAlertasIniciales() {
        var alertas = inventarioService.generarAlertas();
        if (!alertas.isEmpty()) {
            consola.aviso("Hay " + alertas.size() + " alerta(s) en el inventario:");
            inventarioView.mostrarAlertas(alertas);
        }
    }

    @Override
    protected int mostrarMenu() {
        return consola.menu("Happy Feet - Sistema de gestion veterinaria",
                "Gestion de pacientes (dueños y mascotas)",
                "Servicios medicos y citas",
                "Inventario y farmacia",
                "Facturacion y reportes",
                "Actividades especiales (adopcion, vacunacion, club)");
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> pacienteController.iniciar();
            case 2 -> serviciosMedicosController.iniciar();
            case 3 -> inventarioController.iniciar();
            case 4 -> facturacionController.iniciar();
            case 5 -> actividadesController.iniciar();
            case 0 -> {
                consola.mensaje("¡Hasta pronto!");
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }
}
