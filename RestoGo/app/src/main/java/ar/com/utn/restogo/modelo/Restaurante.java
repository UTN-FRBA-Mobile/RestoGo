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
    private String usuarioRestaurante;
    private List<String> comidas;
    private boolean lunes = false;
    private boolean martes = false;
    private boolean miercoles = false;
    private boolean jueves = false;
    private boolean viernes = false;
    private boolean sabado = false;
    private boolean domingo = false;

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

    public void setUsuarioRestaurante(String usuarioRestaurante) {
        this.usuarioRestaurante = usuarioRestaurante;
    }

    public String getUsuarioRestaurante() {
        return usuarioRestaurante;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public boolean isLunes() {
        return lunes;
    }

    public void setLunes(boolean lunes) {
        this.lunes = lunes;
    }

    public boolean isMartes() {
        return martes;
    }

    public void setMartes(boolean martes) {
        this.martes = martes;
    }

    public boolean isMiercoles() {
        return miercoles;
    }

    public void setMiercoles(boolean miercoles) {
        this.miercoles = miercoles;
    }

    public boolean isJueves() {
        return jueves;
    }

    public void setJueves(boolean jueves) {
        this.jueves = jueves;
    }

    public boolean isViernes() {
        return viernes;
    }

    public void setViernes(boolean viernes) {
        this.viernes = viernes;
    }

    public boolean isSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    @Exclude
    public String getComidasText() {
        if (comidas == null || comidas.isEmpty()) {
            return "";
        }

        String stringTipos = "";
        for (String comida : comidas) {
            stringTipos = stringTipos + " ," + comida;
        }

        return stringTipos.substring(2, stringTipos.length());
    }

    @Exclude
    public String getDias() {
        String dias = "";

        if (lunes)
            dias = dias + "Lunes ";
        if (martes)
            dias = dias + "Martes ";
        if (miercoles)
            dias = dias + "Miercoles ";
        if (jueves)
            dias = dias + "Jueves ";
        if (viernes)
            dias = dias + "Viernes";
        if (sabado)
            dias = dias + "Sabado";
        if (domingo)
            dias = dias + "Domingo ";

        return dias;
    }
}
