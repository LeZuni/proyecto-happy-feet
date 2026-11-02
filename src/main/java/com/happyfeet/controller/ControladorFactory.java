package com.happyfeet.controller;

import com.happyfeet.repository.AdopcionRepository;
import com.happyfeet.repository.CitaRepository;
import com.happyfeet.repository.ClubRepository;
import com.happyfeet.repository.ConsultaRepository;
import com.happyfeet.repository.DuenoRepository;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.JornadaRepository;
import com.happyfeet.repository.MascotaRepository;
import com.happyfeet.repository.PrescripcionRepository;
import com.happyfeet.repository.ProcedimientoRepository;
import com.happyfeet.repository.ProductoRepository;
import com.happyfeet.repository.ProveedorRepository;
import com.happyfeet.repository.ServicioRepository;
import com.happyfeet.repository.VeterinarioRepository;
import com.happyfeet.repository.jdbc.AdopcionRepositoryJdbc;
import com.happyfeet.repository.jdbc.CitaRepositoryJdbc;
import com.happyfeet.repository.jdbc.ClubRepositoryJdbc;
import com.happyfeet.repository.jdbc.ConsultaRepositoryJdbc;
import com.happyfeet.repository.jdbc.DuenoRepositoryJdbc;
import com.happyfeet.repository.jdbc.FacturaRepositoryJdbc;
import com.happyfeet.repository.jdbc.HistorialMedicoRepositoryJdbc;
import com.happyfeet.repository.jdbc.JornadaRepositoryJdbc;
import com.happyfeet.repository.jdbc.MascotaRepositoryJdbc;
import com.happyfeet.repository.jdbc.PrescripcionRepositoryJdbc;
import com.happyfeet.repository.jdbc.ProcedimientoRepositoryJdbc;
import com.happyfeet.repository.jdbc.ProductoRepositoryJdbc;
import com.happyfeet.repository.jdbc.ProveedorRepositoryJdbc;
import com.happyfeet.repository.jdbc.ServicioRepositoryJdbc;
import com.happyfeet.repository.jdbc.VeterinarioRepositoryJdbc;
import com.happyfeet.service.AdopcionService;
import com.happyfeet.service.CitaService;
import com.happyfeet.service.ClubService;
import com.happyfeet.service.ConsultaService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.FacturacionService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.service.JornadaService;
import com.happyfeet.service.LogAlertaListener;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.ProcedimientoService;
import com.happyfeet.service.ProveedorService;
import com.happyfeet.service.ReporteService;
import com.happyfeet.service.VeterinarioService;
import com.happyfeet.view.AdopcionView;
import com.happyfeet.view.ClubView;
import com.happyfeet.view.Consola;
import com.happyfeet.view.FacturacionView;
import com.happyfeet.view.InventarioView;
import com.happyfeet.view.JornadaView;
import com.happyfeet.view.PacienteView;
import com.happyfeet.view.ServiciosMedicosView;

/**
 * Patron Factory: aqui se crean y se conectan todas las piezas del sistema
 * (repositorios, servicios, vistas y controladores). Los servicios reciben
 * interfaces, asi que si algun dia se cambia MySQL solo se cambia esta clase.
 */
public class ControladorFactory {

    public AppController crearAppController() {
        Consola consola = new Consola();

        // Repositorios (acceso a datos)
        DuenoRepository duenoRepo = new DuenoRepositoryJdbc();
        MascotaRepository mascotaRepo = new MascotaRepositoryJdbc();
        HistorialMedicoRepository historialRepo = new HistorialMedicoRepositoryJdbc();
        ProductoRepository productoRepo = new ProductoRepositoryJdbc();
        ProveedorRepository proveedorRepo = new ProveedorRepositoryJdbc();
        VeterinarioRepository veterinarioRepo = new VeterinarioRepositoryJdbc();
        CitaRepository citaRepo = new CitaRepositoryJdbc();
        ConsultaRepository consultaRepo = new ConsultaRepositoryJdbc();
        PrescripcionRepository prescripcionRepo = new PrescripcionRepositoryJdbc();
        ProcedimientoRepository procedimientoRepo = new ProcedimientoRepositoryJdbc();
        ServicioRepository servicioRepo = new ServicioRepositoryJdbc();
        FacturaRepository facturaRepo = new FacturaRepositoryJdbc();
        AdopcionRepository adopcionRepo = new AdopcionRepositoryJdbc();
        JornadaRepository jornadaRepo = new JornadaRepositoryJdbc();
        ClubRepository clubRepo = new ClubRepositoryJdbc();

        // Vistas
        PacienteView pacienteView = new PacienteView(consola);
        InventarioView inventarioView = new InventarioView(consola);
        ServiciosMedicosView serviciosView = new ServiciosMedicosView(consola);
        FacturacionView facturacionView = new FacturacionView(consola);
        AdopcionView adopcionView = new AdopcionView(consola);
        JornadaView jornadaView = new JornadaView(consola);
        ClubView clubView = new ClubView(consola);

        // Servicios (logica de negocio)
        DuenoService duenoService = new DuenoService(duenoRepo);
        MascotaService mascotaService = new MascotaService(mascotaRepo, duenoRepo, historialRepo);
        InventarioService inventarioService = new InventarioService(productoRepo);
        // Observers: la vista y el log se enteran de las alertas del inventario
        inventarioService.agregarListener(inventarioView);
        inventarioService.agregarListener(new LogAlertaListener());
        ProveedorService proveedorService = new ProveedorService(proveedorRepo);
        VeterinarioService veterinarioService = new VeterinarioService(veterinarioRepo);
        CitaService citaService = new CitaService(citaRepo, mascotaService, veterinarioService);
        ConsultaService consultaService = new ConsultaService(consultaRepo, prescripcionRepo, historialRepo,
                mascotaService, veterinarioService, citaService, inventarioService);
        ProcedimientoService procedimientoService = new ProcedimientoService(procedimientoRepo, prescripcionRepo,
                historialRepo, mascotaService, veterinarioService, inventarioService);
        ClubService clubService = new ClubService(clubRepo, duenoService);
        FacturacionService facturacionService = new FacturacionService(facturaRepo, servicioRepo, prescripcionRepo,
                duenoService, mascotaService, consultaService, procedimientoService, inventarioService, clubService);
        ReporteService reporteService = new ReporteService(facturaRepo, consultaRepo, procedimientoRepo,
                veterinarioService, inventarioService);
        AdopcionService adopcionService = new AdopcionService(adopcionRepo, mascotaRepo, mascotaService, duenoService);
        JornadaService jornadaService = new JornadaService(jornadaRepo, historialRepo, mascotaService,
                inventarioService);

        // Controladores
        PacienteController pacienteController = new PacienteController(consola, duenoService, mascotaService,
                pacienteView);
        ServiciosMedicosController serviciosController = new ServiciosMedicosController(consola, citaService,
                consultaService, procedimientoService, veterinarioService, mascotaService, inventarioService,
                serviciosView, pacienteView);
        InventarioController inventarioController = new InventarioController(consola, inventarioService,
                proveedorService, inventarioView);
        FacturacionController facturacionController = new FacturacionController(consola, facturacionService,
                reporteService, consultaService, procedimientoService, inventarioService, duenoService,
                facturacionView, serviciosView, pacienteView);
        AdopcionController adopcionController = new AdopcionController(consola, adopcionService, mascotaService,
                duenoService, adopcionView, pacienteView);
        JornadaController jornadaController = new JornadaController(consola, jornadaService, veterinarioService,
                jornadaView);
        ClubController clubController = new ClubController(consola, clubService, duenoService, clubView,
                pacienteView);
        ActividadesEspecialesController actividadesController = new ActividadesEspecialesController(consola,
                adopcionController, jornadaController, clubController);

        return new AppController(consola, pacienteController, serviciosController, inventarioController,
                facturacionController, actividadesController, inventarioService, inventarioView);
    }
}
