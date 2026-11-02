package com.happyfeet.view;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.EventoHistorial;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Raza;
import com.happyfeet.model.enums.Sexo;
import com.happyfeet.util.Formato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PacienteView {

    private final Consola consola;

    public PacienteView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Gestion de pacientes",
                "Registrar dueño",
                "Listar dueños",
                "Registrar mascota",
                "Listar mascotas",
                "Ver ficha e historial de una mascota",
                "Transferir una mascota a otro dueño",
                "Actualizar el peso de una mascota");
    }

    // ---------- Formularios ----------

    public Dueno pedirDueno() {
        consola.subtitulo("Datos del nuevo dueño");
        String nombre = consola.leerTexto("Nombre completo");
        String documento = consola.leerTexto("Documento de identidad");
        String direccion = consola.leerTextoOpcional("Direccion");
        String telefono = consola.leerTextoOpcional("Telefono");
        String email = consola.leerTexto("Correo electronico");
        String emergencia = consola.leerTextoOpcional("Contacto de emergencia");
        return new Dueno(null, nombre, documento, direccion, telefono, email, emergencia, true);
    }

    public Mascota pedirMascota(int duenoId, List<Raza> razas) {
        consola.subtitulo("Datos de la nueva mascota");
        String nombre = consola.leerTexto("Nombre");
        consola.mensaje("Elija la raza:");
        Raza raza = consola.elegirDeLista("Raza", razas, r -> r.especie() + " - " + r.nombre());
        Sexo sexo = consola.elegirEnum("Sexo", Sexo.class);

        LocalDate nacimiento;
        if (consola.confirmar("¿Conoce la fecha de nacimiento?")) {
            nacimiento = consola.leerFecha("Fecha de nacimiento");
        } else {
            // Si no se sabe, se calcula con la edad estimada
            int anios = consola.leerEnteroEntre("Edad estimada en años", 0, 40);
            nacimiento = LocalDate.now().minusYears(anios);
        }

        BigDecimal peso = consola.leerDecimalOpcional("Peso actual en kg");
        String microchip = consola.leerTextoOpcional("Numero de microchip");
        String tatuaje = consola.leerTextoOpcional("Tatuaje");
        String foto = consola.leerTextoOpcional("URL de la foto");
        String alergias = consola.leerTextoOpcional("Alergias conocidas");
        String condiciones = consola.leerTextoOpcional("Condiciones preexistentes");

        return new Mascota(null, duenoId, null, nombre, raza.id(), raza.nombre(), raza.especie(), nacimiento, sexo,
                peso, microchip, tatuaje, foto, alergias, condiciones, true);
    }

    public int pedirIdDueno() {
        return consola.leerEntero("Id del dueño");
    }

    public int pedirIdMascota() {
        return consola.leerEntero("Id de la mascota");
    }

    public BigDecimal pedirPeso() {
        return consola.leerDecimal("Nuevo peso en kg");
    }

    // ---------- Listados ----------

    public void mostrarDuenos(List<Dueno> duenos) {
        consola.subtitulo("Dueños registrados");
        if (duenos.isEmpty()) {
            consola.aviso("No hay dueños registrados.");
            return;
        }
        System.out.printf("%-4s %-28s %-14s %-14s %-30s%n", "ID", "Nombre", "Documento", "Telefono", "Correo");
        duenos.forEach(d -> System.out.printf("%-4d %-28s %-14s %-14s %-30s%n", d.id(),
                Formato.cortar(d.nombreCompleto(), 28), d.documentoIdentidad(), Formato.texto(d.telefono()),
                Formato.cortar(d.email(), 30)));
    }

    public void mostrarMascotas(List<Mascota> mascotas) {
        consola.subtitulo("Mascotas registradas");
        if (mascotas.isEmpty()) {
            consola.aviso("No hay mascotas para mostrar.");
            return;
        }
        System.out.printf("%-4s %-14s %-10s %-20s %-8s %-24s%n", "ID", "Nombre", "Especie", "Raza", "Sexo", "Dueño");
        mascotas.forEach(m -> System.out.printf("%-4d %-14s %-10s %-20s %-8s %-24s%n", m.id(),
                Formato.cortar(m.nombre(), 14), m.especieNombre(), Formato.cortar(m.razaNombre(), 20), m.sexo(),
                Formato.cortar(m.duenoNombre(), 24)));
    }

    public void mostrarFicha(Mascota m, List<EventoHistorial> historial) {
        consola.titulo("Ficha de " + m.nombre());
        System.out.println("Dueño:        " + m.duenoNombre());
        System.out.println("Especie/Raza: " + m.especieNombre() + " / " + m.razaNombre());
        System.out.println("Sexo:         " + m.sexo());
        System.out.println("Nacimiento:   " + Formato.fecha(m.fechaNacimiento()) + " (" + m.edadTexto() + ")");
        System.out.println("Peso actual:  " + (m.pesoActual() == null ? "-" : m.pesoActual() + " kg"));
        System.out.println("Microchip:    " + Formato.texto(m.microchip()));
        System.out.println("Tatuaje:      " + Formato.texto(m.tatuaje()));
        System.out.println("Foto (URL):   " + Formato.texto(m.urlFoto()));
        System.out.println("Alergias:     " + Formato.texto(m.alergias()));
        System.out.println("Condiciones:  " + Formato.texto(m.condicionesPreexistentes()));

        mostrarEventos("Historial de vacunas", historial.stream()
                .filter(e -> e.tipo().equals("Vacunación")).toList());
        mostrarEventos("Procedimientos quirurgicos", historial.stream()
                .filter(e -> e.tipo().equals("Cirugía")).toList());
        mostrarEventos("Historial medico completo", historial);
    }

    private void mostrarEventos(String titulo, List<EventoHistorial> eventos) {
        consola.subtitulo(titulo);
        if (eventos.isEmpty()) {
            System.out.println("(sin registros)");
            return;
        }
        eventos.forEach(e -> {
            System.out.println(Formato.fecha(e.fecha()) + " | " + e.tipo() + " | " + Formato.cortar(e.descripcion(), 60));
            if (e.diagnostico() != null) {
                System.out.println("    Diagnostico: " + e.diagnostico());
            }
            if (e.veterinarioNombre() != null) {
                System.out.println("    Veterinario: " + e.veterinarioNombre());
            }
        });
    }
}
