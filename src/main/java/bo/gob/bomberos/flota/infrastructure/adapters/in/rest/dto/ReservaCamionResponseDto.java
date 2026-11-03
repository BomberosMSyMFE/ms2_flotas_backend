package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import bo.gob.bomberos.flota.domain.model.ReservaSaga;
import java.time.Instant;

public record ReservaCamionResponseDto(
        boolean exito,
        String reservaId,
        String incidenteId,
        String camionId,
        String placa,
        String tipoUnidad,
        int capacidadAguaLts,
        String estadoOperativo,
        double distanciaKms,
        double tiempoEstimadoLlegadaMinutos,
        double tiempoLlegadaMinutos,
        String estacionOrigen,
        Instant timestamp
) {
    public static ReservaCamionResponseDto fromDomain(ReservaSaga model) {
        return new ReservaCamionResponseDto(
                true,
                model.getReservaId(),
                model.getIncidenteId(),
                model.getCamionId(),
                model.getPlaca(),
                model.getTipoUnidad().name(),
                model.getCapacidadAguaLts(),
                model.getEstadoOperativo().name(),
                model.getDistanciaKms(),
                model.getTiempoEstimadoLlegadaMinutos(),
                model.getTiempoEstimadoLlegadaMinutos(),
                model.getEstacionOrigen(),
                model.getTimestamp()
        );
    }
}
