package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservaCamionRequestDto(
        @NotBlank(message = "El incidenteId es obligatorio")
        String incidenteId,

        String tipoFuego,
        Double latitud,
        Double longitud,
        Integer litrosMinimos,
        String camionId,
        String direccion,
        String ubicacion,
        String cuadrante
) {
    public String getTipoFuegoOrDefault() {
        return (tipoFuego != null && !tipoFuego.isBlank()) ? tipoFuego : "ESTRUCTURAL";
    }

    public double getLatitudOrDefault() {
        if (latitud != null) return latitud;
        if (direccion != null && direccion.toLowerCase().contains("mutualista")) return -17.7650;
        return -17.7833;
    }

    public double getLongitudOrDefault() {
        if (longitud != null) return longitud;
        if (direccion != null && direccion.toLowerCase().contains("mutualista")) return -63.1600;
        return -63.1821;
    }

    public int getLitrosMinimosOrDefault() {
        return litrosMinimos != null ? litrosMinimos : 3000;
    }
}
