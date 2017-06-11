package ar.com.utn.restogo.modelo;

import android.location.Location;

import java.util.ArrayList;
import java.util.Set;

public class Restaurante {
    private String descripcion;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
