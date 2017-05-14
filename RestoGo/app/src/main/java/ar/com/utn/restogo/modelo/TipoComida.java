package ar.com.utn.restogo.modelo;


public enum TipoComida {
    VARIADA("Variada"),
    PARRILLA("Parrilla"),
    PORTEÑA("Porteña"),
    PIZZA("Pizza"),
    CASERA("Casera"),
    DE_AUTOR("De autor"),
    INTERNACIONAL("Internacional"),
    ITALIANA("Italiana"),
    JAPONESA("Japonesa"),
    MEDITERRANEA("Mediterránea"),
    PESCADOS("Pescados"),
    NORTEAMERICANA("Norteamericana"),
    VEGETARIANA("Vegetariana");

    private String descripcion;

    TipoComida(String descripcion) {
        this.descripcion = descripcion;
    }


    @Override
    public String toString() {
        return descripcion;
    }
}
