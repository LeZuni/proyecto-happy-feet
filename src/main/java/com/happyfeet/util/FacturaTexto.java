package com.happyfeet.util;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Factura;

/** Convierte una factura en un texto plano limpio para imprimir o guardar. */
public final class FacturaTexto {

    private static final String LINEA = "=".repeat(64);
    private static final String LINEA_FINA = "-".repeat(64);

    private FacturaTexto() {
    }

    public static String generar(Factura f, Dueno cliente) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append(centrar("VETERINARIA HAPPY FEET")).append('\n');
        sb.append(centrar("NIT 1234567-8  |  Tel (502) 2555-0123")).append('\n');
        sb.append(centrar("12 calle 5-34, zona 10, Ciudad de Guatemala")).append('\n');
        sb.append(LINEA).append('\n');
        sb.append("FACTURA No: ").append(f.numero()).append('\n');
        sb.append("Fecha:      ").append(Formato.fechaHora(f.fechaEmision())).append('\n');
        sb.append("Estado:     ").append(f.estado()).append('\n');
        sb.append(LINEA_FINA).append('\n');
        sb.append("CLIENTE\n");
        sb.append("Nombre:     ").append(cliente.nombreCompleto()).append('\n');
        sb.append("Documento:  ").append(cliente.documentoIdentidad()).append('\n');
        sb.append("Correo:     ").append(cliente.email()).append('\n');
        sb.append(LINEA_FINA).append('\n');
        sb.append(String.format("%-28s %5s %14s %14s%n", "Descripcion", "Cant", "V. Unitario", "Subtotal"));
        sb.append(LINEA_FINA).append('\n');
        f.items().forEach(i -> sb.append(String.format("%-28s %5d %14s %14s%n",
                Formato.cortar(i.descripcion(), 28), i.cantidad(), Formato.dinero(i.precioUnitario()),
                Formato.dinero(i.subtotal()))));
        sb.append(LINEA_FINA).append('\n');
        sb.append(String.format("%-49s %14s%n", "Subtotal:", Formato.dinero(f.subtotal())));
        sb.append(String.format("%-49s %14s%n", "Descuento:", "-" + Formato.dinero(f.descuento())));
        sb.append(String.format("%-49s %14s%n", "IVA (12%):", Formato.dinero(f.impuesto())));
        sb.append(LINEA_FINA).append('\n');
        sb.append(String.format("%-49s %14s%n", "TOTAL A PAGAR:", Formato.dinero(f.total())));
        sb.append(LINEA_FINA).append('\n');
        sb.append("Metodo de pago: ").append(f.metodoPago() == null ? "-" : f.metodoPago()).append('\n');
        if (f.observaciones() != null) {
            sb.append("Observaciones:  ").append(f.observaciones()).append('\n');
        }
        sb.append(LINEA).append('\n');
        sb.append(centrar("Gracias por confiar en Happy Feet")).append('\n');
        sb.append(LINEA).append('\n');
        return sb.toString();
    }

    private static String centrar(String texto) {
        int espacios = Math.max(0, (64 - texto.length()) / 2);
        return " ".repeat(espacios) + texto;
    }
}
