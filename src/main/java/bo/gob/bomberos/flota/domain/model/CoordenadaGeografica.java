package bo.gob.bomberos.flota.domain.model;

import java.util.Objects;

/**
 * Value Object inmutable que representa una coordenada geoespacial (SRID 4326).
 */
public final class CoordenadaGeografica {
    private final double latitud;
    private final double longitud;

    public CoordenadaGeografica(double latitud, double longitud) {
        if (latitud < -90.0 || latitud > 90.0) {
            throw new IllegalArgumentException("Latitud fuera de rango (-90 a 90): " + latitud);
        }
        if (longitud < -180.0 || longitud > 180.0) {
            throw new IllegalArgumentException("Longitud fuera de rango (-180 a 180): " + longitud);
        }
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    /**
     * Calcula la distancia en kilómetros usando la fórmula de Haversine.
     */
    public double distanciaKmHacia(CoordenadaGeografica destino) {
        final double R = 6371.0; // Radio de la Tierra en km
        double dLat = Math.toRadians(destino.latitud - this.latitud);
        double dLon = Math.toRadians(destino.longitud - this.longitud);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.latitud)) * Math.cos(Math.toRadians(destino.latitud)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordenadaGeografica that = (CoordenadaGeografica) o;
        return Double.compare(that.latitud, latitud) == 0 && Double.compare(that.longitud, longitud) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitud, longitud);
    }

    @Override
    public String toString() {
        return String.format("CoordenadaGeografica(lat=%.6f, lon=%.6f)", latitud, longitud);
    }
}
