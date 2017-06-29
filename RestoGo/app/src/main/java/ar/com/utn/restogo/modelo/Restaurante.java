package ar.com.utn.restogo.modelo;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class Restaurante implements Serializable{
    private String descripcion;
    private String url;
    private String direccion;
    private Double latitute;
    private Double longitute;
    private String horaApertura;
    private String horaCierre;
    private List<String> comidas;

    @Exclude
    private String key;

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

    @Exclude
    public String getHorario(){
        return getHoraApertura() + " - " + getHoraCierre();
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(String horaApertura) {
        this.horaApertura = horaApertura;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(String horaCierre) {
        this.horaCierre = horaCierre;
    }

    public List<String> getComidas() {
        return comidas;
    }

    public void setComidas(List<String> comidas) {
        this.comidas = comidas;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
