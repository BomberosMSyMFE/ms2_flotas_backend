package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import java.time.Instant;

public record EvidenciaResponseDto(
        String evidenciaId,
        String idempotencyKey,
        String estado,
        String mensaje,
        Instant timestampSync
) {}
