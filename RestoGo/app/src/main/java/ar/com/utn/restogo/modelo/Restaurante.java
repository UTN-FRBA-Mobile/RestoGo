package ar.com.utn.restogo.modelo;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Set;

public class Restaurante {
    private String descripcion;
    private String url;
    private Double latitute;
    private Double longitute;

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

    @Exclude
    public Location getLocation() {
        if (latitute != null && longitute != null){
            Location location = new Location("restoGoProvider");
            location.setLatitude(latitute);
            location.setLongitude(longitute);
            return location;
        }
        return null;
    }

    public Double getLatitute() {
        return latitute;
    }

    public void setLatitute(Double latitute) {
        this.latitute = latitute;
    }

    public Double getLongitute() {
        return longitute;
    }

    public void setLongitute(Double longitute) {
        this.longitute = longitute;
    }
}
