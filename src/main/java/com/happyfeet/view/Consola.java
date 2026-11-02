package com.happyfeet.view;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.enums.ConEtiqueta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Se encarga de todo lo que es leer del teclado y escribir en pantalla.
 * Todas las vistas usan esta clase (hay un solo Scanner para todo el programa).
 */
public class Consola {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final Scanner scanner = new Scanner(System.in);

    // ---------- Mensajes ----------

    public void titulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(64));
        System.out.println("  " + texto.toUpperCase());
        System.out.println("=".repeat(64));
    }

    public void subtitulo(String texto) {
        System.out.println();
        System.out.println("--- " + texto + " ---");
    }

    public void mensaje(String texto) {
        System.out.println(texto);
    }

    public void exito(String texto) {
        System.out.println("[OK] " + texto);
    }

    public void error(String texto) {
        System.out.println("[ERROR] " + texto);
    }

    public void aviso(String texto) {
        System.out.println("[!] " + texto);
    }

    public void pausa() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /** Muestra un menu numerado, agrega la opcion 0 y devuelve lo que eligio el usuario. */
    public int menu(String titulo, String... opciones) {
        titulo(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opciones[i]);
        }
        System.out.println("  0. Volver / Salir");
        return leerEntero("Seleccione una opcion");
    }

    // ---------- Lectura de texto ----------

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            error("Este campo es obligatorio.");
        }
    }

    /** Devuelve null si el usuario solo presiona Enter. */
    public String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje + " (Enter para omitir): ");
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? null : texto;
    }

    // ---------- Lectura de numeros ----------

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                error("Debe escribir un numero entero.");
            }
        }
    }

    public int leerEnteroEntre(String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            error("El numero debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    public Integer leerEnteroOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (Enter para omitir): ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                error("Debe escribir un numero entero.");
            }
        }
    }

    public BigDecimal leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return new BigDecimal(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                error("Debe escribir un numero (ejemplo: 12.5).");
            }
        }
    }

    public BigDecimal leerDecimalOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (Enter para omitir): ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(texto.replace(',', '.'));
            } catch (NumberFormatException e) {
                error("Debe escribir un numero (ejemplo: 12.5).");
            }
        }
    }

    // ---------- Lectura de fechas ----------

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (dd/MM/yyyy): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim(), FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha invalida. Ejemplo: 25/12/2026");
            }
        }
    }

    public LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (dd/MM/yyyy, Enter para omitir): ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(texto, FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha invalida. Ejemplo: 25/12/2026");
            }
        }
    }

    public LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (dd/MM/yyyy HH:mm): ");
            try {
                return LocalDateTime.parse(scanner.nextLine().trim(), FECHA_HORA);
            } catch (DateTimeParseException e) {
                error("Fecha y hora invalida. Ejemplo: 25/12/2026 14:30");
            }
        }
    }

    public LocalTime leerHora(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (HH:mm): ");
            try {
                return LocalTime.parse(scanner.nextLine().trim(), HORA);
            } catch (DateTimeParseException e) {
                error("Hora invalida. Ejemplo: 14:30");
            }
        }
    }

    // ---------- Confirmaciones y listas ----------

    public boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            error("Responda s o n.");
        }
    }

    /** Muestra los valores de un enum numerados y devuelve el que elija el usuario. */
    public <E extends Enum<E> & ConEtiqueta> E elegirEnum(String mensaje, Class<E> tipo) {
        E[] valores = tipo.getEnumConstants();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i].getEtiqueta());
        }
        return valores[leerEnteroEntre(mensaje, 1, valores.length) - 1];
    }

    /** Muestra una lista numerada y devuelve el elemento elegido. */
    public <T> T elegirDeLista(String mensaje, List<T> items, Function<T, String> descripcion) {
        if (items.isEmpty()) {
            throw new ReglaNegocioException("No hay opciones disponibles para elegir");
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + descripcion.apply(items.get(i)));
        }
        return items.get(leerEnteroEntre(mensaje, 1, items.size()) - 1);
    }
}
