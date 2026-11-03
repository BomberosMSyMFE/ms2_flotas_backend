package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String codigo,
        String mensaje,
        Instant timestamp
) {
    public static ErrorResponseDto of(String codigo, String mensaje) {
        return new ErrorResponseDto(codigo, mensaje, Instant.now());
    }
}
