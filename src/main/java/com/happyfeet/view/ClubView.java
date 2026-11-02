package com.happyfeet.view;

import com.happyfeet.model.entities.BeneficioClub;
import com.happyfeet.model.entities.ClubMascotas;
import com.happyfeet.model.enums.NivelClub;
import com.happyfeet.util.Formato;

import java.util.List;

public class ClubView {

    private final Consola consola;

    public ClubView(Consola consola) {
        this.consola = consola;
    }

    public int menu() {
        return consola.menu("Club de mascotas frecuentes",
                "Inscribir a un dueño en el club",
                "Consultar puntos de un dueño",
                "Ver beneficios",
                "Canjear un beneficio",
                "Ranking de clientes");
    }

    public int pedirIdBeneficio() {
        return consola.leerEntero("Id del beneficio");
    }

    public void mostrarClub(ClubMascotas club, String nombreDueno) {
        consola.subtitulo("Club de " + nombreDueno);
        System.out.println("Nivel:               " + club.nivel());
        System.out.println("Puntos acumulados:   " + club.puntosAcumulados());
        System.out.println("Puntos canjeados:    " + club.puntosCanjeados());
        System.out.println("Puntos disponibles:  " + club.puntosDisponibles());
        System.out.println("Inscrito desde:      " + Formato.fecha(club.fechaInscripcion()));
        NivelClub[] niveles = NivelClub.values();
        if (club.nivel().ordinal() < niveles.length - 1) {
            NivelClub siguiente = niveles[club.nivel().ordinal() + 1];
            System.out.println("Para llegar a " + siguiente + " le faltan "
                    + (siguiente.getPuntosMinimos() - club.puntosAcumulados()) + " puntos");
        }
    }

    public void mostrarBeneficios(List<BeneficioClub> beneficios) {
        consola.subtitulo("Beneficios del club");
        System.out.printf("%-4s %-30s %-10s %-8s %-16s%n", "ID", "Beneficio", "Nivel", "Puntos", "Tipo");
        beneficios.forEach(b -> System.out.printf("%-4d %-30s %-10s %-8d %-16s%n", b.id(),
                Formato.cortar(b.nombre(), 30), b.nivelRequerido(), b.puntosNecesarios(), b.tipo()));
    }

    public void mostrarRanking(List<ClubMascotas> ranking, java.util.function.IntFunction<String> nombres) {
        consola.subtitulo("Ranking de clientes frecuentes");
        int puesto = 1;
        for (ClubMascotas c : ranking) {
            System.out.printf("%d. %-28s %-8s %d puntos%n", puesto++, nombres.apply(c.duenoId()), c.nivel(),
                    c.puntosDisponibles());
        }
    }
}
