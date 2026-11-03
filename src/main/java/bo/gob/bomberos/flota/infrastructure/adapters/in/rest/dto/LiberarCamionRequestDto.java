package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LiberarCamionRequestDto(
        @NotBlank(message = "El incidenteId es obligatorio")
        String incidenteId,

        @NotBlank(message = "El camionId es obligatorio")
        String camionId,

        String motivoCompensacion
) {}
