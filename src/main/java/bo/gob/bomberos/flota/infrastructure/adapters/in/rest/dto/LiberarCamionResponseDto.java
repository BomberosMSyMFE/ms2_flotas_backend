package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import bo.gob.bomberos.flota.domain.model.CamionUnidad;
import java.time.Instant;

public record LiberarCamionResponseDto(
        String camionId,
        String nuevoEstado,
        String mensaje,
        Instant timestamp
) {
    public static LiberarCamionResponseDto fromDomain(CamionUnidad model, String mensaje) {
        return new LiberarCamionResponseDto(
                model.getId(),
                model.getEstado().name(),
                mensaje,
                Instant.now()
        );
    }
}
