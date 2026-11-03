package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "hidrantes_red")
public class HidranteEntity {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @Column(name = "presion_psi", nullable = false)
    private double presionPsi;

    @Column(name = "caudal_litros_minuto", nullable = false)
    private int caudalLitrosMinuto;

    @Column(name = "operativo", nullable = false)
    private boolean operativo;

    public HidranteEntity() {}

    public HidranteEntity(String id, double latitud, double longitud, double presionPsi, int caudalLitrosMinuto, boolean operativo) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.presionPsi = presionPsi;
        this.caudalLitrosMinuto = caudalLitrosMinuto;
        this.operativo = operativo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getPresionPsi() { return presionPsi; }
    public void setPresionPsi(double presionPsi) { this.presionPsi = presionPsi; }
    public int getCaudalLitrosMinuto() { return caudalLitrosMinuto; }
    public void setCaudalLitrosMinuto(int caudalLitrosMinuto) { this.caudalLitrosMinuto = caudalLitrosMinuto; }
    public boolean isOperativo() { return operativo; }
    public void setOperativo(boolean operativo) { this.operativo = operativo; }
}
