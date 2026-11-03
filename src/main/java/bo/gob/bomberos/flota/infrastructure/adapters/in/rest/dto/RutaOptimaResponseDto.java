package bo.gob.bomberos.flota.infrastructure.adapters.in.rest.dto;

import bo.gob.bomberos.flota.domain.model.Hidrante;
import bo.gob.bomberos.flota.domain.model.RutaCalculada;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RutaOptimaResponseDto(
        int distanciaTotalMetros,
        int duracionEstimadaSegundos,
        double velocidadMediaKmH,
        Map<String, Object> geometriaGeoJson,
        List<HidranteDto> hidrantesCercanos
) {
    public record HidranteDto(
            String id,
            double latitud,
            double longitud,
            double presionPsi,
            int caudalLitrosMinuto,
            int distanciaMetrosAlSiniestro,
            boolean operativo
    ) {
        public static HidranteDto fromDomain(Hidrante h) {
            return new HidranteDto(
                    h.getId(),
                    h.getPosicion().getLatitud(),
                    h.getPosicion().getLongitud(),
                    h.getPresionPsi(),
                    h.getCaudalLitrosMinuto(),
                    250, // Distancia estimada en radio
                    h.isOperativo()
            );
        }
    }

    public static RutaOptimaResponseDto fromDomain(RutaCalculada ruta) {
        List<double[]> coords = ruta.getWaypoints().stream()
                .map(w -> new double[]{w.getLongitud(), w.getLatitud()})
                .collect(Collectors.toList());

        Map<String, Object> geoJson = Map.of(
                "type", "LineString",
                "coordinates", coords
        );

        List<HidranteDto> hidrantesDto = ruta.getHidrantesCercanos().stream()
                .map(HidranteDto::fromDomain)
                .collect(Collectors.toList());

        return new RutaOptimaResponseDto(
                ruta.getDistanciaTotalMetros(),
                ruta.getDuracionEstimadaSegundos(),
                ruta.getVelocidadMediaKmH(),
                geoJson,
                hidrantesDto
        );
    }
}
