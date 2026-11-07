package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record PushTokenRequestDto(
    @NotBlank(message = "El ID de la unidad es requerido")
    String unidadId,

    @NotBlank(message = "El token FCM es requerido")
    String token,

    String plataforma
) {}
