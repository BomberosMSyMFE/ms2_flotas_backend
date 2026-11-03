package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;

public record EvidenciaRequestDto(
        @NotBlank(message = "El incidenteId es obligatorio")
        String incidenteId,

        @NotBlank(message = "El camionId es obligatorio")
        String camionId,

        String bomberoMatricula,
        String tipoEvidencia,
        String transcripcionLocal,
        String urlFotografia,
        Map<String, Double> coordenadasCaptura,
        Instant timestampCreacionOffline
) {}
