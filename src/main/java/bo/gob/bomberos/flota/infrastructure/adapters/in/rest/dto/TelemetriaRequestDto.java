package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import bo.gob.bomberos.flota.domain.model.EstadoUnidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelemetriaRequestDto(
        @NotBlank(message = "El camionId es obligatorio")
        String camionId,

        @NotNull(message = "La latitud es obligatoria")
        Double latitud,

        @NotNull(message = "La longitud es obligatoria")
        Double longitud,

        Double velocidadKmH,
        Double rumboGrados,
        Integer litrosAguaActual,
        Integer combustiblePorcentaje,
        EstadoUnidad estadoOperativo
) {}
