package com.happyfeet.controller;

import com.happyfeet.model.Producto;
import com.happyfeet.observer.Observador;

public class ConsolaAlertaObservador implements Observador {
    @Override
    public void actualizar(Producto producto) {
        System.out.println("========================================");
        System.out.println("*** ALERTA DE STOCK BAJO ***");
        System.out.println("Producto ID: " + producto.getId());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Stock Actual: " + producto.getCantidadStock());
        System.out.println("Stock Mínimo: " + producto.getStockMinimo());
        System.out.println("========================================");
    }
}git config --global