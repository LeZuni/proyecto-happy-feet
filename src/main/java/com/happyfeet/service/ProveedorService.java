package com.happyfeet.service;

import com.happyfeet.exception.ReglaNegocioException;
import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.repository.ProveedorRepository;
import com.happyfeet.util.AppLogger;

import java.util.List;

public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final AppLogger logger = AppLogger.getInstancia();

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor registrar(Proveedor proveedor) {
        if (proveedor.nombreEmpresa() == null || proveedor.nombreEmpresa().isBlank()) {
            throw new ReglaNegocioException("El nombre de la empresa es obligatorio");
        }
        boolean repetido = proveedorRepository.listar().stream()
                .anyMatch(p -> p.nombreEmpresa().equalsIgnoreCase(proveedor.nombreEmpresa()));
        if (repetido) {
            throw new ReglaNegocioException("Ya existe un proveedor con ese nombre");
        }
        Proveedor guardado = proveedorRepository.guardar(proveedor);
        logger.info("Proveedor registrado: " + guardado.nombreEmpresa());
        return guardado;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.listar();
    }
}
