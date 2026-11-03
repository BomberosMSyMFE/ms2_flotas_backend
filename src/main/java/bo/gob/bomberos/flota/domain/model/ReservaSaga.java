package bo.gob.bomberos.flota.domain.model;

import java.time.Instant;

/**
 * Value Object inmutable que representa el resultado de la reserva atómica en la SAGA.
 */
public class ReservaSaga {
    private final String reservaId;
    private final String incidenteId;
    private final String camionId;
    private final String placa;
    private final TipoUnidad tipoUnidad;
    private final int capacidadAguaLts;
    private final EstadoUnidad estadoOperativo;
    private final double distanciaKms;
    private final double tiempoEstimadoLlegadaMinutos;
    private final String estacionOrigen;
    private final Instant timestamp;

    public ReservaSaga(String reservaId, String incidenteId, String camionId, String placa,
                       TipoUnidad tipoUnidad, int capacidadAguaLts, EstadoUnidad estadoOperativo,
                       double distanciaKms, double tiempoEstimadoLlegadaMinutos, String estacionOrigen,
                       Instant timestamp) {
        this.reservaId = reservaId;
        this.incidenteId = incidenteId;
        this.camionId = camionId;
        this.placa = placa;
        this.tipoUnidad = tipoUnidad;
        this.capacidadAguaLts = capacidadAguaLts;
        this.estadoOperativo = estadoOperativo;
        this.distanciaKms = distanciaKms;
        this.tiempoEstimadoLlegadaMinutos = tiempoEstimadoLlegadaMinutos;
        this.estacionOrigen = estacionOrigen;
        this.timestamp = timestamp;
    }

    public String getReservaId() { return reservaId; }
    public String getIncidenteId() { return incidenteId; }
    public String getCamionId() { return camionId; }
    public String getPlaca() { return placa; }
    public TipoUnidad getTipoUnidad() { return tipoUnidad; }
    public int getCapacidadAguaLts() { return capacidadAguaLts; }
    public EstadoUnidad getEstadoOperativo() { return estadoOperativo; }
    public double getDistanciaKms() { return distanciaKms; }
    public double getTiempoEstimadoLlegadaMinutos() { return tiempoEstimadoLlegadaMinutos; }
    public String getEstacionOrigen() { return estacionOrigen; }
    public Instant getTimestamp() { return timestamp; }
}
