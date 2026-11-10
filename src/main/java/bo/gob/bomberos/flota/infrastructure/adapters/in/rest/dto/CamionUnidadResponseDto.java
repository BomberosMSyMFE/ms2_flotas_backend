package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import java.time.Instant;

public record CamionUnidadResponseDto(
        String id,
        String placa,
        String tipo,
        String estado,
        double latitud,
        double longitud,
        int litrosAguaActual,
        int capacidadMaximaAgua,
        int combustiblePorcentaje,
        String estacionOrigen,
        String incidenteAsignadoId,
        Instant ultimaActualizacion
) {
    public static CamionUnidadResponseDto fromDomain(CamionUnidad model) {
        return new CamionUnidadResponseDto(
                model.getId(),
                model.getPlaca(),
                model.getTipo().name(),
                model.getEstado().name(),
                model.getPosicion().getLatitud(),
                model.getPosicion().getLongitud(),
                model.getLitrosAguaActual(),
                model.getCapacidadMaximaAgua(),
                model.getCombustiblePorcentaje(),
                model.getEstacionOrigen(),
                model.getIncidenteAsignadoId(),
                model.getUltimaActualizacion()
        );
    }
}
