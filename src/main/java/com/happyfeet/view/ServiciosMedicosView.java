package com.happyfeet.view;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.ConsultaMedica;
import com.happyfeet.model.entities.InsumoProcedimiento;
import com.happyfeet.model.entities.Prescripcion;
import com.happyfeet.model.entities.ProcedimientoEspecial;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.model.enums.EstadoProcedimiento;
import com.happyfeet.util.Formato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiciosMedicosView {

    private final Consola consola;

    public ServiciosMedicosView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Servicios medicos y citas",
                "Agendar una cita",
                "Ver todas las citas",
                "Ver las citas de un dia",
                "Cambiar el estado de una cita",
                "Registrar consulta medica",
                "Registrar procedimiento especial (cirugia, etc.)",
                "Actualizar seguimiento postoperatorio",
                "Ver consultas y procedimientos de una mascota",
                "Ver veterinarios",
                "Registrar veterinario");
    }

    // ---------- Formularios ----------

    public Veterinario elegirVeterinario(List<Veterinario> veterinarios) {
        consola.mensaje("Elija el veterinario:");
        return consola.elegirDeLista("Veterinario", veterinarios,
                v -> v.nombreCompleto() + " (" + Formato.texto(v.especialidad()) + ")");
    }

    public LocalDateTime pedirFechaHoraCita() {
        return consola.leerFechaHora("Fecha y hora de la cita");
    }

    public String pedirMotivo() {
        return consola.leerTexto("Motivo de la visita");
    }

    public int pedirIdCita() {
        return consola.leerEntero("Id de la cita");
    }

    public LocalDate pedirFecha() {
        return consola.leerFecha("Fecha");
    }

    public Integer elegirCitaOpcional(List<Cita> citasPendientes) {
        if (citasPendientes.isEmpty() || !consola.confirmar("¿La consulta es de una cita agendada?")) {
            return null;
        }
        return consola.elegirDeLista("Cita", citasPendientes,
                c -> "#" + c.id() + " - " + Formato.fechaHora(c.fechaHora()) + " - " + c.motivo()).id();
    }

    public ConsultaMedica pedirConsulta(int mascotaId, int veterinarioId, Integer citaId) {
        consola.subtitulo("Datos de la consulta");
        String motivo = consola.leerTexto("Motivo de la visita");
        String sintomas = consola.leerTextoOpcional("Sintomas");
        String diagnostico = consola.leerTextoOpcional("Diagnostico");
        String recomendaciones = consola.leerTextoOpcional("Recomendaciones");
        String observaciones = consola.leerTextoOpcional("Observaciones");
        BigDecimal peso = consola.leerDecimalOpcional("Peso registrado hoy (kg)");
        BigDecimal temperatura = consola.leerDecimalOpcional("Temperatura (°C)");
        return new ConsultaMedica(null, mascotaId, veterinarioId, citaId, LocalDateTime.now(), motivo, sintomas,
                diagnostico, recomendaciones, observaciones, peso, temperatura);
    }

    /** Pregunta por los medicamentos recetados. El usuario elige de la lista de productos. */
    public List<Prescripcion> pedirPrescripciones(List<Producto> medicamentos) {
        List<Prescripcion> lista = new ArrayList<>();
        while (!medicamentos.isEmpty() && consola.confirmar("¿Desea prescribir un medicamento?")) {
            Producto producto = consola.elegirDeLista("Medicamento", medicamentos,
                    p -> p.nombre() + " (stock " + p.cantidadStock() + ", vence " + Formato.fecha(p.fechaVencimiento()) + ")");
            int cantidad = consola.leerEnteroEntre("Cantidad a entregar", 1, 10000);
            String dosis = consola.leerTextoOpcional("Dosis");
            String frecuencia = consola.leerTextoOpcional("Frecuencia");
            Integer dias = consola.leerEnteroOpcional("Duracion en dias");
            String instrucciones = consola.leerTextoOpcional("Instrucciones");
            lista.add(new Prescripcion(null, null, null, producto.id(), cantidad, dosis, frecuencia, dias,
                    instrucciones));
        }
        return lista;
    }

    public List<InsumoProcedimiento> pedirInsumos(List<Producto> insumos) {
        List<InsumoProcedimiento> lista = new ArrayList<>();
        while (!insumos.isEmpty() && consola.confirmar("¿Se uso algun insumo medico (jeringas, gasas...)?")) {
            Producto producto = consola.elegirDeLista("Insumo", insumos,
                    p -> p.nombre() + " (stock " + p.cantidadStock() + ")");
            int cantidad = consola.leerEnteroEntre("Cantidad usada", 1, 10000);
            lista.add(new InsumoProcedimiento(producto.id(), cantidad, null));
        }
        return lista;
    }

    public ProcedimientoEspecial pedirProcedimiento(int mascotaId, int veterinarioId) {
        consola.subtitulo("Datos del procedimiento");
        String tipo = consola.leerTexto("Tipo (Cirugía, Limpieza Dental, Radiografía, Examen de Sangre, otro)");
        String nombre = consola.leerTexto("Nombre del procedimiento");
        LocalDateTime fechaHora = consola.leerFechaHora("Fecha y hora");
        Integer duracion = consola.leerEnteroOpcional("Duracion estimada en minutos");
        String preoperatorio = consola.leerTextoOpcional("Informacion preoperatoria");
        String detalle = consola.leerTexto("Detalle del procedimiento");
        consola.mensaje("Estado del procedimiento:");
        EstadoProcedimiento estado = consola.elegirEnum("Estado", EstadoProcedimiento.class);
        String complicaciones = consola.leerTextoOpcional("Complicaciones");
        String seguimiento = consola.leerTextoOpcional("Seguimiento postoperatorio");
        LocalDate proximoControl = consola.leerFechaOpcional("Fecha del proximo control");
        BigDecimal costo = consola.leerDecimalOpcional("Costo del procedimiento");
        return new ProcedimientoEspecial(null, mascotaId, veterinarioId, tipo, nombre, fechaHora, duracion,
                preoperatorio, detalle, complicaciones, seguimiento, proximoControl, estado, costo);
    }

    public int pedirIdProcedimiento() {
        return consola.leerEntero("Id del procedimiento");
    }

    public Object[] pedirSeguimiento() {
        consola.mensaje("Nuevo estado del procedimiento:");
        EstadoProcedimiento estado = consola.elegirEnum("Estado", EstadoProcedimiento.class);
        String complicaciones = consola.leerTextoOpcional("Complicaciones");
        String seguimiento = consola.leerTextoOpcional("Seguimiento postoperatorio");
        LocalDate proximo = consola.leerFechaOpcional("Fecha del proximo control");
        return new Object[]{estado, complicaciones, seguimiento, proximo};
    }

    public Veterinario pedirVeterinario() {
        consola.subtitulo("Datos del veterinario");
        String nombre = consola.leerTexto("Nombre completo");
        String documento = consola.leerTexto("Documento de identidad");
        String licencia = consola.leerTexto("Licencia profesional");
        String especialidad = consola.leerTextoOpcional("Especialidad");
        String telefono = consola.leerTextoOpcional("Telefono");
        String email = consola.leerTextoOpcional("Correo");
        return new Veterinario(null, nombre, documento, licencia, especialidad, telefono, email, true);
    }

    // ---------- Listados ----------

    public void mostrarVeterinarios(List<Veterinario> veterinarios) {
        consola.subtitulo("Equipo veterinario");
        if (veterinarios.isEmpty()) {
            consola.aviso("No hay veterinarios registrados.");
            return;
        }
        System.out.printf("%-4s %-28s %-22s %-16s%n", "ID", "Nombre", "Especialidad", "Licencia");
        veterinarios.forEach(v -> System.out.printf("%-4d %-28s %-22s %-16s%n", v.id(),
                Formato.cortar(v.nombreCompleto(), 28), Formato.cortar(v.especialidad(), 22),
                v.licenciaProfesional()));
    }

    public void mostrarCitas(List<Cita> citas) {
        consola.subtitulo("Citas");
        if (citas.isEmpty()) {
            consola.aviso("No hay citas para mostrar.");
            return;
        }
        System.out.printf("%-4s %-17s %-12s %-22s %-12s %-24s%n", "ID", "Fecha y hora", "Mascota", "Veterinario",
                "Estado", "Motivo");
        citas.forEach(c -> System.out.printf("%-4d %-17s %-12s %-22s %-12s %-24s%n", c.id(),
                Formato.fechaHora(c.fechaHora()), Formato.cortar(c.mascotaNombre(), 12),
                Formato.cortar(c.veterinarioNombre(), 22), c.estado(), Formato.cortar(c.motivo(), 24)));
    }

    public void mostrarConsultas(List<ConsultaMedica> consultas) {
        consola.subtitulo("Consultas medicas");
        if (consultas.isEmpty()) {
            System.out.println("(sin consultas)");
            return;
        }
        consultas.forEach(c -> {
            System.out.println("#" + c.id() + " | " + Formato.fechaHora(c.fechaHora()) + " | " + c.motivo());
            System.out.println("    Diagnostico: " + Formato.texto(c.diagnostico()));
            System.out.println("    Recomendaciones: " + Formato.texto(c.recomendaciones()));
        });
    }

    public void mostrarProcedimientos(List<ProcedimientoEspecial> procedimientos) {
        consola.subtitulo("Procedimientos especiales");
        if (procedimientos.isEmpty()) {
            System.out.println("(sin procedimientos)");
            return;
        }
        procedimientos.forEach(p -> {
            System.out.println("#" + p.id() + " | " + Formato.fechaHora(p.fechaHora()) + " | "
                    + p.nombreProcedimiento() + " | " + p.estado());
            System.out.println("    Seguimiento: " + Formato.texto(p.seguimientoPostoperatorio())
                    + " | Proximo control: " + Formato.fecha(p.proximoControl()));
        });
    }
}
