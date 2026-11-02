package com.happyfeet.view;

import com.happyfeet.model.entities.MascotaAdopcion;
import com.happyfeet.model.enums.EstadoAdopcion;
import com.happyfeet.util.Formato;

import java.time.LocalDate;
import java.util.List;

public class AdopcionView {

    private final Consola consola;

    public AdopcionView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Dias de adopcion",
                "Registrar una mascota para adopcion",
                "Ver mascotas disponibles",
                "Registrar una adopcion (genera contrato)");
    }

    public MascotaAdopcion pedirMascotaAdopcion(int mascotaId) {
        consola.subtitulo("Datos para la adopcion");
        String motivo = consola.leerTextoOpcional("Motivo de ingreso");
        String historia = consola.leerTextoOpcional("Historia de la mascota");
        String temperamento = consola.leerTextoOpcional("Temperamento");
        String necesidades = consola.leerTextoOpcional("Necesidades especiales");
        return new MascotaAdopcion(null, mascotaId, null, LocalDate.now(), motivo, EstadoAdopcion.DISPONIBLE,
                historia, temperamento, necesidades);
    }

    public int pedirIdAdopcion() {
        return consola.leerEntero("Id del registro de adopcion");
    }

    public String pedirCondiciones() {
        return consola.leerTextoOpcional("Condiciones especiales del contrato");
    }

    public void mostrarDisponibles(List<MascotaAdopcion> lista) {
        consola.subtitulo("Mascotas disponibles para adopcion");
        if (lista.isEmpty()) {
            consola.aviso("No hay mascotas disponibles por ahora.");
            return;
        }
        System.out.printf("%-4s %-14s %-12s %-18s %-30s%n", "ID", "Mascota", "Ingreso", "Temperamento", "Necesidades");
        lista.forEach(a -> System.out.printf("%-4d %-14s %-12s %-18s %-30s%n", a.id(),
                Formato.cortar(a.mascotaNombre(), 14), Formato.fecha(a.fechaIngreso()),
                Formato.cortar(a.temperamento(), 18), Formato.cortar(a.necesidadesEspeciales(), 30)));
    }

    public void mostrarContrato(String contrato) {
        System.out.println();
        System.out.println(contrato);
    }
}
