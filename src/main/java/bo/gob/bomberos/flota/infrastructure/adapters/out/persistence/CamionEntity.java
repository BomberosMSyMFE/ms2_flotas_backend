package bo.gob.bomberos.flota.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "unidades_flota")
public class CamionEntity {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "placa", nullable = false, length = 16)
    private String placa;

    @Column(name = "tipo", nullable = false, length = 32)
    private String tipo;

    @Column(name = "estado", nullable = false, length = 32)
    private String estado;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @Column(name = "litros_agua_actual", nullable = false)
    private int litrosAguaActual;

    @Column(name = "capacidad_maxima_agua", nullable = false)
    private int capacidadMaximaAgua;

    @Column(name = "combustible_porcentaje", nullable = false)
    private int combustiblePorcentaje;

    @Column(name = "estacion_origen", length = 64)
    private String estacionOrigen;

    @Column(name = "incidente_asignado_id", length = 64)
    private String incidenteAsignadoId;

    @Column(name = "velocidad_kmh")
    private double velocidadKmH;

    @Column(name = "rumbo_grados")
    private double rumboGrados;

    @Column(name = "ultima_actualizacion")
    private Instant ultimaActualizacion;

    public CamionEntity() {}

    public CamionEntity(String id, String placa, String tipo, String estado, double latitud, double longitud,
                        int litrosAguaActual, int capacidadMaximaAgua, int combustiblePorcentaje,
                        String estacionOrigen, String incidenteAsignadoId, double velocidadKmH,
                        double rumboGrados, Instant ultimaActualizacion) {
        this.id = id;
        this.placa = placa;
        this.tipo = tipo;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.litrosAguaActual = litrosAguaActual;
        this.capacidadMaximaAgua = capacidadMaximaAgua;
        this.combustiblePorcentaje = combustiblePorcentaje;
        this.estacionOrigen = estacionOrigen;
        this.incidenteAsignadoId = incidenteAsignadoId;
        this.velocidadKmH = velocidadKmH;
        this.rumboGrados = rumboGrados;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public int getLitrosAguaActual() { return litrosAguaActual; }
    public void setLitrosAguaActual(int litrosAguaActual) { this.litrosAguaActual = litrosAguaActual; }
    public int getCapacidadMaximaAgua() { return capacidadMaximaAgua; }
    public void setCapacidadMaximaAgua(int capacidadMaximaAgua) { this.capacidadMaximaAgua = capacidadMaximaAgua; }
    public int getCombustiblePorcentaje() { return combustiblePorcentaje; }
    public void setCombustiblePorcentaje(int combustiblePorcentaje) { this.combustiblePorcentaje = combustiblePorcentaje; }
    public String getEstacionOrigen() { return estacionOrigen; }
    public void setEstacionOrigen(String estacionOrigen) { this.estacionOrigen = estacionOrigen; }
    public String getIncidenteAsignadoId() { return incidenteAsignadoId; }
    public void setIncidenteAsignadoId(String incidenteAsignadoId) { this.incidenteAsignadoId = incidenteAsignadoId; }
    public double getVelocidadKmH() { return velocidadKmH; }
    public void setVelocidadKmH(double velocidadKmH) { this.velocidadKmH = velocidadKmH; }
    public double getRumboGrados() { return rumboGrados; }
    public void setRumboGrados(double rumboGrados) { this.rumboGrados = rumboGrados; }
    public Instant getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(Instant ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
