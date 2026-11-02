package com.happyfeet.controller;

import com.happyfeet.model.entities.Producto;
import com.happyfeet.service.InventarioService;
import com.happyfeet.service.ProveedorService;
import com.happyfeet.view.Consola;
import com.happyfeet.view.InventarioView;

/** Controlador del Modulo 3: inventario y farmacia. */
public class InventarioController extends ControladorBase {

    private final InventarioService inventarioService;
    private final ProveedorService proveedorService;
    private final InventarioView view;

    public InventarioController(Consola consola, InventarioService inventarioService,
                                ProveedorService proveedorService, InventarioView view) {
        super(consola);
        this.inventarioService = inventarioService;
        this.proveedorService = proveedorService;
        this.view = view;
    }

    @Override
    protected int mostrarMenu() {
        return view.menu();
    }

    @Override
    protected boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> view.mostrarProductos(inventarioService.listar());
            case 2 -> registrarProducto();
            case 3 -> reabastecer();
            case 4 -> view.mostrarAlertas(inventarioService.generarAlertas());
            case 5 -> view.mostrarProveedores(proveedorService.listar());
            case 6 -> registrarProveedor();
            case 0 -> {
                return false;
            }
            default -> consola.error("Opcion no valida");
        }
        return true;
    }

    private void registrarProducto() {
        Producto nuevo = view.pedirProducto(inventarioService.listarTipos(), proveedorService.listar());
        Producto guardado = inventarioService.registrar(nuevo);
        consola.exito("Producto registrado con id " + guardado.id());
    }

    private void reabastecer() {
        view.mostrarProductos(inventarioService.listar());
        int id = view.pedirIdProducto();
        inventarioService.reabastecer(id, view.pedirCantidad());
        consola.exito("Stock actualizado");
    }

    private void registrarProveedor() {
        var guardado = proveedorService.registrar(view.pedirProveedor());
        consola.exito("Proveedor registrado con id " + guardado.id());
    }
}
