package com.happyfeet.view;

import com.happyfeet.model.entities.JornadaVacunacion;
import com.happyfeet.model.entities.Producto;
import com.happyfeet.model.entities.RegistroVacunacion;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.model.enums.EstadoJornada;
import com.happyfeet.util.Formato;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JornadaView {

    private final Consola consola;

    public JornadaView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Jornadas de vacunacion",
                "Crear una jornada",
                "Ver jornadas",
                "Cambiar estado de una jornada (iniciar, finalizar, cancelar)",
                "Registro rapido de vacunacion",
                "Ver mascotas vacunadas en una jornada");
    }

    public JornadaVacunacion pedirJornada() {
        consola.subtitulo("Datos de la jornada");
        String nombre = consola.leerTexto("Nombre de la jornada");
        LocalDate fecha = consola.leerFecha("Fecha");
        LocalTime inicio = consola.leerHora("Hora de inicio");
        LocalTime fin = consola.leerHora("Hora de fin");
        String ubicacion = consola.leerTextoOpcional("Ubicacion");
        String descripcion = consola.leerTextoOpcional("Descripcion");
        Integer capacidad = consola.leerEnteroOpcional("Capacidad maxima de mascotas");
        return new JornadaVacunacion(null, nombre, fecha, inicio, fin, ubicacion, descripcion, capacidad,
                EstadoJornada.PLANIFICADA);
    }

    public int pedirIdJornada() {
        return consola.leerEntero("Id de la jornada");
    }

    public EstadoJornada pedirNuevoEstado() {
        consola.mensaje("Nuevo estado:");
        return consola.elegirEnum("Estado", EstadoJornada.class);
    }

    public Producto elegirVacuna(List<Producto> vacunas) {
        consola.mensaje("Elija la vacuna de la jornada:");
        return consola.elegirDeLista("Vacuna", vacunas,
                v -> v.nombre() + " (stock " + v.cantidadStock() + ", vence " + Formato.fecha(v.fechaVencimiento()) + ")");
    }

    public Veterinario elegirVeterinario(List<Veterinario> veterinarios) {
        consola.mensaje("Veterinario que aplica las vacunas:");
        return consola.elegirDeLista("Veterinario", veterinarios, Veterinario::nombreCompleto);
    }

    /** Devuelve 0 cuando el usuario quiere terminar el registro rapido. */
    public int pedirMascotaRapida() {
        return consola.leerEntero("Id de la mascota a vacunar (0 para terminar)");
    }

    public LocalDate pedirProximaDosis() {
        LocalDate porDefecto = LocalDate.now().plusYears(1);
        LocalDate fecha = consola.leerFechaOpcional("Proxima dosis (Enter = " + Formato.fecha(porDefecto) + ")");
        return fecha == null ? porDefecto : fecha;
    }

    public void mostrarJornadas(List<JornadaVacunacion> jornadas) {
        consola.subtitulo("Jornadas de vacunacion");
        if (jornadas.isEmpty()) {
            consola.aviso("No hay jornadas registradas.");
            return;
        }
        System.out.printf("%-4s %-34s %-12s %-12s %-8s%n", "ID", "Nombre", "Fecha", "Estado", "Cupos");
        jornadas.forEach(j -> System.out.printf("%-4d %-34s %-12s %-12s %-8s%n", j.id(),
                Formato.cortar(j.nombre(), 34), Formato.fecha(j.fecha()), j.estado(),
                j.capacidadMaxima() == null ? "-" : j.capacidadMaxima()));
    }

    public void mostrarRegistros(List<RegistroVacunacion> registros) {
        consola.subtitulo("Mascotas vacunadas (" + registros.size() + ")");
        registros.forEach(r -> System.out.println(Formato.fechaHora(r.fechaHora()) + " | " + r.mascotaNombre()
                + " | " + r.vacunaNombre() + " | proxima dosis: " + Formato.fecha(r.proximaDosis())));
    }
}
