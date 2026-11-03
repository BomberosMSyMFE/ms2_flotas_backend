package bo.gob.bomberos.flota.domain.model;

/**
 * Entidad de dominio que representa una toma de agua / hidrante en la red pública.
 */
public class Hidrante {
    private final String id;
    private final CoordenadaGeografica posicion;
    private final double presionPsi;
    private final int caudalLitrosMinuto;
    private final boolean operativo;

    public Hidrante(String id, CoordenadaGeografica posicion, double presionPsi, int caudalLitrosMinuto, boolean operativo) {
        this.id = id;
        this.posicion = posicion;
        this.presionPsi = presionPsi;
        this.caudalLitrosMinuto = caudalLitrosMinuto;
        this.operativo = operativo;
    }

    public String getId() {
        return id;
    }

    public CoordenadaGeografica getPosicion() {
        return posicion;
    }

    public double getPresionPsi() {
        return presionPsi;
    }

    public int getCaudalLitrosMinuto() {
        return caudalLitrosMinuto;
    }

    public boolean isOperativo() {
        return operativo;
    }
}
