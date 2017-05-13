package ar.com.utn.restogo.modelo;

import android.location.Location;

public class Restaurante {
    private String descripcion;
    private Location ubicacion;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
