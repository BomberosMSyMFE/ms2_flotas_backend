package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

public record DespachoNotificacionRequestDto(
    String unidadId,
    String incidenteId,
    String tipoIncidente,
    String ubicacion,
    Double latitud,
    Double longitud,
    String prioridad
) {
    public String getUnidadIdOrDefault() {
        return (unidadId != null && !unidadId.isBlank()) ? unidadId : "B-01";
    }

    public String getIncidenteIdOrDefault() {
        return (incidenteId != null && !incidenteId.isBlank()) ? incidenteId : "INC-" + System.currentTimeMillis();
    }

    public String getTipoIncidenteOrDefault() {
        return (tipoIncidente != null && !tipoIncidente.isBlank()) ? tipoIncidente : "INCENDIO ESTRUCTURAL";
    }

    public String getUbicacionOrDefault() {
        return (ubicacion != null && !ubicacion.isBlank()) ? ubicacion : "Av. Banzer y 4to Anillo, Santa Cruz";
    }

    public double getLatitudOrDefault() {
        return latitud != null ? latitud : -17.7650;
    }

    public double getLongitudOrDefault() {
        return longitud != null ? longitud : -63.1750;
    }

    public String getPrioridadOrDefault() {
        return (prioridad != null && !prioridad.isBlank()) ? prioridad : "CODIGO_ROJO";
    }
}
