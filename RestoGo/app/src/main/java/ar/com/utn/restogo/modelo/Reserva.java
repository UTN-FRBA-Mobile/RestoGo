package ar.com.utn.restogo.modelo;

public class Reserva {
    private String ussid;
    private String hora;
    private String dia;
    private String cantidadPersonas;

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
}
