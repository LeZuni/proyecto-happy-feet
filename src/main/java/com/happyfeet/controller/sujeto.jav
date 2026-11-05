package com.happyfeet.controller;

import com.happyfeet.model.Producto;

public interface Sujeto {
    void registrarObservador(Observador observador);
    void removerObservador(Observador observador);
    void notificarObservadores(Producto producto);
}