package com.happyfeet.model;

public class Producto {
    private String id;
    private String nombre;
    private int cantidadStock;
    private int stockMinimo;

    public Producto(String id, String nombre, int cantidadStock, int stockMinimo) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
}