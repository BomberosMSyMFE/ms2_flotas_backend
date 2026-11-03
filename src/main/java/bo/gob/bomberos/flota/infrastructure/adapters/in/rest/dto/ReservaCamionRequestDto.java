package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservaCamionRequestDto(
        @NotBlank(message = "El incidenteId es obligatorio")
        String incidenteId,

        @NotBlank(message = "El tipoFuego es obligatorio")
        String tipoFuego,

        Double latitud,
        Double longitud,
        Integer litrosMinimos,
        String camionId
) {
    public double getLatitudOrDefault() {
        return latitud != null ? latitud : -17.7833;
    }

    public double getLongitudOrDefault() {
        return longitud != null ? longitud : -63.1821;
    }

    public int getLitrosMinimosOrDefault() {
        return litrosMinimos != null ? litrosMinimos : 3000;
    }
}
