package ar.com.utn.restogo.modelo;

import java.io.Serializable;

public class Reserva implements Serializable {
    private String ussid;
    private String hora;
    private String dia;
    private String cantidadPersonas;
    private Boolean fueRespondida;
    private Boolean fueRechazada;

    public String getUssid() {
        return ussid;
    }

    public void setUssid(String ussid) {
        this.ussid = ussid;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(String cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public Boolean getFueRespondida() {
        return fueRespondida;
    }

    public void setFueRespondida(Boolean fueRespondida) {
        this.fueRespondida = fueRespondida;
    }

    public Boolean getFueRechazada() {
        return fueRechazada;
    }

    public void setFueRechazada(Boolean fueRechazada) {
        this.fueRechazada = fueRechazada;
    }
}
